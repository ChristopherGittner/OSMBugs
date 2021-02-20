package org.gittner.osmbugs.osmnotes

import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.impl.client.DefaultHttpClient
import org.gittner.osmbugs.HttpRequestAdapter
import org.gittner.osmbugs.statics.Settings
import org.koin.core.KoinComponent
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox
import java.util.*


class OsmNotesApi : KoinComponent {
    companion object {
        private const val API_SCHEME = "https"

        // Live Server
        private const val DEFAULT_SERVER = "api.openstreetmap.org"

        // Debug Server
        //private const val DEFAULT_SERVER = "api06.dev.openstreetmap.org" //Debug Server

        private const val PATH_SEGMENT_API = "api"
        private const val PATH_SEGMENT_VERSION = "0.6"
        private const val PATH_SEGMENT_NOTES = "notes"
    }

    private val mSettings = Settings.getInstance()

    private val mConsumer = CommonsHttpOAuthConsumer(
        // Live Server
        "aJ0PL87pUEflM1GqhwO8x4jFmw1ehzYe19x9nsme",
        "KYIp6rbd0OIDOVQLQCXTxawOcZwmI1bX8quR7uPk"

        // Debug Server
        //"ElJ0hTlALddTziJA3ZxjRq6cHsRjN48PGfhDL9R9",
        //"5MususnuRXAZQJEFFqFgfFXAIpHDkLiRVjcRHJxK"
    )

    private val mProvider = CommonsHttpOAuthProvider(
        // Live Server
        "https://www.openstreetmap.org/oauth/request_token",
        "https://www.openstreetmap.org/oauth/access_token",
        "https://www.openstreetmap.org/oauth/authorize"

        // Debug Server
        //"https://master.apis.dev.openstreetmap.org/oauth/request_token",
        //"https://master.apis.dev.openstreetmap.org/oauth/access_token",
        //"https://master.apis.dev.openstreetmap.org/oauth/authorize"
    )

    init {
        mConsumer.setTokenWithSecret(mSettings.OsmNotes.Token, mSettings.OsmNotes.ConsumerSecret)
    }

    suspend fun download(
        bBox: BoundingBox,
        showClosed: Boolean,
        limit: Int
    ): ArrayList<OsmNote> = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendQueryParameter(
                "bbox", "%f,%f,%f,%f".format(
                    Locale.ENGLISH,
                    bBox.lonWest,
                    bBox.latSouth,
                    bBox.lonEast,
                    bBox.latNorth
                )
            )
            .appendQueryParameter("closed", if (showClosed) "1" else "0")
            .appendQueryParameter("limit", limit.toString())
            .build()

        val response = Fuel
            .get(url.toString())
            .awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid Status Code: ${response.second.statusCode}")
        }

        OsmNotesParser().parse(response.third)
    }

    suspend fun download(id: Long): OsmNote = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendPath(id.toString())
            .build()

        val response = Fuel
            .get(url.toString())
            .awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid Status Code: ${response.second.statusCode}")
        }

        val parsedNotes = OsmNotesParser().parse(response.third)

        if (parsedNotes.count() != 1) {
            throw java.lang.RuntimeException("Received an invalid number of notes: ${parsedNotes.count()}. Expected: 1")
        }

        parsedNotes[0]
    }

    suspend fun addComment(id: Long, comment: String) {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendPath(id.toString())
            .appendPath("comment")
            .appendQueryParameter("text", comment)
            .build()

        val request = Fuel.post(url.toString())
        mConsumer.sign(HttpRequestAdapter(request))

        val response = request.awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }
    }

    suspend fun closeNote(id: Long, comment: String) {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendPath(id.toString())
            .appendPath("close")
            .appendQueryParameter("text", comment)
            .build()

        val request = Fuel.post(url.toString())
        mConsumer.sign(HttpRequestAdapter(request))

        val response = request.awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }
    }

    suspend fun reopenNote(id: Long, comment: String) {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendPath(id.toString())
            .appendPath("reopen")
            .appendQueryParameter("text", comment)
            .build()

        val request = Fuel.post(url.toString())
        mConsumer.sign(HttpRequestAdapter(request))

        val response = request.awaitStringResponse()

        if (response.second.statusCode != 200) {
            throw RuntimeException("Invalid status code: ${response.second.statusCode}")
        }
    }

    suspend fun addNote(point: IGeoPoint, comment: String): OsmNote {
        val url = Uri.Builder()
            .scheme(API_SCHEME)
            .authority(DEFAULT_SERVER)
            .appendPath(PATH_SEGMENT_API)
            .appendPath(PATH_SEGMENT_VERSION)
            .appendPath(PATH_SEGMENT_NOTES)
            .appendQueryParameter("lat", point.latitude.toString())
            .appendQueryParameter("lon", point.longitude.toString())
            .appendQueryParameter("text", comment)
            .build()

        val request = Fuel.post(url.toString())
        mConsumer.sign(HttpRequestAdapter(request))

        val response = request.awaitStringResponse()

        val parsedNotes = OsmNotesParser().parse(response.third)

        if (parsedNotes.count() != 1) {
            throw java.lang.RuntimeException("Received an invalid number of notes: ${parsedNotes.count()}. Expected: 1")
        }

        return parsedNotes[0]
    }

    suspend fun getRequestToken(): String = withContext(Dispatchers.IO) {
        val client = DefaultHttpClient();
        client.getConnectionManager().getSchemeRegistry()
            .register(Scheme("https", SSLSocketFactory.getSocketFactory(), 443))
        mProvider.setHttpClient(client)
        mProvider.retrieveRequestToken(mConsumer, OsmNotesLoginActivity.CALLBACK_URL, "")
    }

    suspend fun getAccessToken(verifier: String): Pair<String, String> = withContext(Dispatchers.IO) {
        mProvider.retrieveAccessToken(mConsumer, verifier, "")

        Pair(mConsumer.token, mConsumer.tokenSecret)
    }
}