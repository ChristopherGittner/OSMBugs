package org.gittner.osmbugs.mapdust

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.format.ISODateTimeFormat
import org.json.JSONObject
import org.osmdroid.util.GeoPoint


class MapdustParser {
    companion object {
        suspend fun parse(data: String): ArrayList<MapdustError> = withContext(Dispatchers.Default) {
            val errors = ArrayList<MapdustError>()

            val json = JSONObject(data)
            val bugArray = json.getJSONArray("features")
            for (i in 0 until bugArray.length()) {
                val error = bugArray.getJSONObject(i)

                errors.add(parseError(error))
            }

            errors
        }

        suspend fun parseError(data: String): MapdustError = withContext(Dispatchers.Default) {
            parseError(JSONObject(data))
        }

        private fun parseError(error: JSONObject): MapdustError {
            val formatter = ISODateTimeFormat.dateTimeParser()

            val id = error.getLong("id")

            val geometry = error.getJSONObject("geometry")
            val lon = geometry.getJSONArray("coordinates").getDouble(0)
            val lat = geometry.getJSONArray("coordinates").getDouble(1)

            val properties = error.getJSONObject("properties")

            val user = properties.getString("nickname")

            val state = when (properties.getInt("status")) {
                2 -> MapdustError.STATE.CLOSED
                3 -> MapdustError.STATE.IGNORED
                else -> MapdustError.STATE.OPEN
            }

            val comments: ArrayList<MapdustError.MapdustComment> = ArrayList()
            val commentArray = properties.getJSONArray("comments")
            for (n in 0 until commentArray.length()) {
                comments.add(
                    MapdustError.MapdustComment(
                        commentArray.getJSONObject(n).getString("comment"),
                        commentArray.getJSONObject(n).getString("nickname")
                    )
                )
            }

            val description = properties.getString("description")

            val type_const = properties.getString("type")

            val creationDate = formatter.parseDateTime(properties.getString("date_created"))

            val type = when (type_const) {
                "wrong_turn" -> MapdustError.ERROR_TYPE.WRONG_TURN
                "bad_routing" -> MapdustError.ERROR_TYPE.BAD_ROUTING
                "oneway_road" -> MapdustError.ERROR_TYPE.ONEWAY_ROAD
                "blocked_street" -> MapdustError.ERROR_TYPE.BLOCKED_STREET
                "missing_street" -> MapdustError.ERROR_TYPE.MISSING_STREET
                "wrong_roundabout" -> MapdustError.ERROR_TYPE.ROUNDABOUT_ISSUE
                "missing_speedlimit" -> MapdustError.ERROR_TYPE.MISSING_SPEED_INFO
                else -> MapdustError.ERROR_TYPE.OTHER
            }

            val mapdustError = MapdustError(GeoPoint(lat, lon), id, creationDate, user, type, description, state)

            mapdustError.Comments = comments

            return mapdustError
        }

        suspend fun parseAddBug(data: String): Long = withContext(Dispatchers.Default) {
            val json = JSONObject(data)

            json.getLong("id")
        }
    }
}
