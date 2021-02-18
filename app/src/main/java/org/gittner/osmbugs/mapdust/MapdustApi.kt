package org.gittner.osmbugs.mapdust

import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.statics.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox
import java.util.*
import kotlin.collections.ArrayList


class MapdustApi : KoinComponent {
    private val API_KEY = "ae58b0b4aa3f876265a4d5f29167b73c"

    private val mSettings = Settings.getInstance()

    suspend fun download(bBox: BoundingBox): ArrayList<MapdustError> {
        val url = Uri.Builder()
            .scheme("http")
            .authority("www.mapdust.com")
            .appendPath("api")
            .appendPath("getBugs")
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("bbox", "%f,%f,%f,%f".format(Locale.US, bBox.lonEast, bBox.latSouth, bBox.lonWest, bBox.latNorth))
            .appendQueryParameter("comments", "1")
            .appendQueryParameter("ft", getMapdustSelectionString())
            .appendQueryParameter("fs", getMapdustEnabledTypesString())
            .build()

        val response = Fuel
            .get(url.toString())
            .awaitStringResponse()

        // No content
        if (response.second.statusCode == 204) {
            return ArrayList()
        }
        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }

        return MapdustParser.parse(response.third)
    }

    private fun getMapdustSelectionString(): String? {
        var result = ""
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.WRONG_TURN)) {
            result += "wrong_turn,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.BAD_ROUTING)) {
            result += "bad_routing,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.ONEWAY_ROAD)) {
            result += "oneway_road,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.BLOCKED_STREET)) {
            result += "blocked_street,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.MISSING_STREET)) {
            result += "missing_street,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.ROUNDABOUT_ISSUE)) {
            result += "wrong_roundabout,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.MISSING_SPEED_INFO)) {
            result += "missing_speedlimit,"
        }
        if (mSettings.Mapdust.GetTypeEnabled(MapdustError.ERROR_TYPE.OTHER)) {
            result += "other,"
        }
        if (result.endsWith(",")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }


    private fun getMapdustEnabledTypesString(): String? {
        var result = ""
        if (mSettings.Mapdust.ShowOpen) {
            result += "1,"
        }
        if (mSettings.Mapdust.ShowClosed) {
            result += "2,"
        }
        if (mSettings.Mapdust.ShowIgnored) {
            result += "3,"
        }
        if (result.endsWith(",")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }

    suspend fun download(id: Long): MapdustError = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme("http")
            .authority("www.mapdust.com")
            .appendPath("api")
            .appendPath("getBug")
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("id", id.toString())
            .build()

        val response = Fuel
            .get(url.toString())
            .awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }

        MapdustParser.parseError(response.third)
    }

    suspend fun commentError(Id: Long, comment: String) = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme("http")
            .authority("www.mapdust.com")
            .appendPath("api")
            .appendPath("commentBug")
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("id", Id.toString())
            .appendQueryParameter("comment", comment)
            .appendQueryParameter("nickname", mSettings.Mapdust.Username)
            .build()

        val response = Fuel
            .post(url.toString())
            .awaitStringResponse()

        // Mapdust returns 201 for commentBug as Success
        if (response.second.statusCode != 201) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }
    }

    suspend fun addBug(point: IGeoPoint, type: MapdustError.ERROR_TYPE, description: String): Long = withContext(Dispatchers.IO) {
        val sType = when (type) {
            MapdustError.ERROR_TYPE.WRONG_TURN -> "wrong_turn"
            MapdustError.ERROR_TYPE.BAD_ROUTING -> "bad_routing"
            MapdustError.ERROR_TYPE.ONEWAY_ROAD -> "oneway_road"
            MapdustError.ERROR_TYPE.BLOCKED_STREET -> "blocked_street"
            MapdustError.ERROR_TYPE.MISSING_STREET -> "missing_street"
            MapdustError.ERROR_TYPE.ROUNDABOUT_ISSUE -> "wrong_roundabout"
            MapdustError.ERROR_TYPE.MISSING_SPEED_INFO -> "missing_speedlimit"
            MapdustError.ERROR_TYPE.OTHER -> "other"
        }

        val url = Uri.Builder()
            .scheme("http")
            .authority("www.mapdust.com")
            .appendPath("api")
            .appendPath("addBug")
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("coordinates", "%f,%f".format(Locale.ENGLISH, point.longitude, point.latitude))
            .appendQueryParameter("description", description)
            .appendQueryParameter("nickname", mSettings.Mapdust.Username)
            .appendQueryParameter("type", sType)
            .build()

        val response = Fuel
            .post(url.toString())
            .awaitStringResponse()

        // Mapdust returns 201 for addBug as Success
        if (response.second.statusCode != 201) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }

        MapdustParser.parseAddBug(response.third)
    }

    suspend fun changeErrorStatus(id: Long, comment: String, state: MapdustError.STATE) = withContext(Dispatchers.IO) {
        val sState = when (state) {
            MapdustError.STATE.OPEN -> "1"
            MapdustError.STATE.CLOSED -> "2"
            else -> "3"
        }

        val url = Uri.Builder()
            .scheme("http")
            .authority("www.mapdust.com")
            .appendPath("api")
            .appendPath("changeBugStatus")
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("id", id.toString())
            .appendQueryParameter("comment", comment)
            .appendQueryParameter("nickname", mSettings.Mapdust.Username)
            .appendQueryParameter("status", sState)
            .build()

        val response = Fuel
            .post(url.toString())
            .awaitStringResponse()

        // Mapdust returns 201 for addBug as Success
        if (response.second.statusCode != 201) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }
    }
}