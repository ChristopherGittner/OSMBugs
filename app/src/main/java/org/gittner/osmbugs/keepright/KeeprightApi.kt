package org.gittner.osmbugs.keepright

import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.statics.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.osmdroid.api.IGeoPoint

class KeeprightApi : KoinComponent {
    private val mSettings = Settings.getInstance()

    suspend fun download(
        center: IGeoPoint,
        showIgnored: Boolean,
        showTempIgnored: Boolean,
        langGerman: Boolean
    ): ArrayList<KeeprightError> = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme("https")
            .authority("keepright.at")
            .appendPath("points.php")
            .appendQueryParameter("show_ign", if (showIgnored) "1" else "0")
            .appendQueryParameter("show_tmpign", if (showTempIgnored) "1" else "0")
            .appendQueryParameter("ch", getSelectionString())
            .appendQueryParameter("lat", center.latitude.toString())
            .appendQueryParameter("lon", center.longitude.toString())
            .appendQueryParameter("lang", if (langGerman) "de" else "en")
            .build()

        val response = Fuel.get(url.toString()).awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid Status Code: ${response.second.statusCode}")
        }

        KeeprightParser().parse(response.third)
    }

    suspend fun comment(schema: Long, id: Long, comment: String, state: KeeprightError.STATE) = withContext(Dispatchers.IO) {
        val sState: String = when (state) {
            KeeprightError.STATE.OPEN -> ""
            KeeprightError.STATE.IGNORED -> "ignore"
            KeeprightError.STATE.IGNORED_TMP -> "ignore_t"
        }

        val url = Uri.Builder()
            .scheme("https")
            .authority("keepright.at")
            .appendPath("comment.php")
            .appendQueryParameter("st", sState)
            .appendQueryParameter("co", comment)
            .appendQueryParameter("schema", schema.toString())
            .appendQueryParameter("id", id.toString())
            .build()

        val response = Fuel.post(url.toString()).awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid Response: ${response.second.statusCode}")
        }
    }

    private fun getSelectionString(): String {
        // Unknown what 0 stands for but it's here for compatibility Reasons
        var result = "0"
        KeeprightError.ERROR_TYPE.values().forEach {
            if (mSettings.Keepright.GetTypeEnabled(it)) {
                result += ",${it.Type}"
            }
        }

        return result
    }
}