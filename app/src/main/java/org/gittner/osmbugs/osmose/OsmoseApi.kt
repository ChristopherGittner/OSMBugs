package org.gittner.osmbugs.osmose

import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.statics.Settings
import org.koin.core.component.KoinComponent
import org.osmdroid.util.BoundingBox
import java.util.*

class OsmoseApi : KoinComponent {
    private val mSettings = Settings.getInstance()

    suspend fun download(bBox: BoundingBox): ArrayList<OsmoseError> = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme("https")
            .authority("osmose.openstreetmap.fr")
            .appendPath("en")
            .appendPath("api")
            .appendPath("0.2")
            .appendPath("errors")
            .appendQueryParameter("bbox", "%f,%f,%f,%f".format(Locale.ENGLISH, bBox.lonWest, bBox.latSouth, bBox.lonEast, bBox.latNorth))
            .appendQueryParameter("limit", mSettings.Osmose.ErrorLimit.toString())
            .appendQueryParameter("item", getItemString())
            .appendQueryParameter("level", getErrorLevelString())
            .appendQueryParameter("full", "true")
            .build()

        val response = Fuel
            .get(url.toString())
            .timeoutRead(60000)
            .awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }

        OsmoseParser.parse(response.third)
    }

    private fun getItemString(): String {
        var s = ""
        OsmoseError.ERROR_TYPE.entries.forEach {
            if (mSettings.Osmose.GetTypeEnabled(it)) {
                s += "${it.Item},"
            }
        }

        return s.removeSuffix(",")
    }

    private fun getErrorLevelString(): String {
        return mSettings.Osmose.ErrorLevel.toString()
    }
}