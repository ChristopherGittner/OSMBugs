package org.gittner.osmbugs.osmose

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.statics.OpenStreetMap
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import timber.log.Timber

class OsmoseParser {
    companion object {
        suspend fun parse(data: String): ArrayList<OsmoseError> = withContext(Dispatchers.Default) {
            val formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ssZZ")

            val errors = ArrayList<OsmoseError>()

            val json = JSONObject(data)

            val bugArray = json.getJSONArray("errors")
            for (i in 0 until bugArray.length()) {
                try {
                    val error = bugArray.getJSONArray(i)

                    val lat = error.getString(0).toDouble()
                    val lon = error.getString(1).toDouble()

                    val id = error.getString(2).toLong()

                    val type = OsmoseError.GetTypeByItemNumber(error.getString(3).toInt())!!

                    val subtitle = error.getString(8)
                    val title = error.getString(9)

                    val creationDate: DateTime = formatter.parseDateTime(error.getString(11))

                    val parsedError = OsmoseError(GeoPoint(lat, lon), id, type, creationDate, title, subtitle)

                    "(node|way|relation)(\\d+)".toRegex().findAll(error.getString(6)).forEach {
                        parsedError.Elements.add(
                            OpenStreetMap.Object(
                                it.groups[2]!!.value.toLong(),
                                when (it.groups[1]!!.value) {
                                    "node" -> OpenStreetMap.TYPE.NODE
                                    "way" -> OpenStreetMap.TYPE.WAY
                                    "relation" -> OpenStreetMap.TYPE.RELATION
                                    else -> throw RuntimeException("Invalid element type: ${it.groups[1]!!.value}")
                                }
                            )
                        )
                    }

                    errors.add(parsedError)
                } catch (err: Exception) {
                    Timber.w("Failed to parse Bug ${err.localizedMessage}")
                }
            }

            errors
        }
    }
}