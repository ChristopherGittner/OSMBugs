package org.gittner.osmbugs.osmnotes

import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.gittner.osmbugs.HttpRequestAdapter
import org.gittner.osmbugs.statics.Settings
import org.koin.core.KoinComponent
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox
import java.util.*


class OsmNotesApi : KoinComponent {
    companion object {
        private const val API_SCHEME = "https"

        private const val PATH_SEGMENT_API = "api"
        private const val PATH_SEGMENT_VERSION = "0.6"
        private const val PATH_SEGMENT_NOTES = "notes"

        const val AUTH_CALLBACK_URL = "osmbugs://osmbugs.gittner.org/auth-done/osmbugs"
        const val AUTH_VERIFIER_PARAM = "oauth_verifier";


        // ------------------------------------
        // Live Server
        // ------------------------------------
        private const val DEFAULT_SERVER = "api.openstreetmap.org"

        private const val API_KEY = "aJ0PL87pUEflM1GqhwO8x4jFmw1ehzYe19x9nsme"
        private const val API_SECRET = "KYIp6rbd0OIDOVQLQCXTxawOcZwmI1bX8quR7uPk"

        private const val OAUTH_REQUEST_URL = "https://www.openstreetmap.org/oauth/request_token"
        private const val OAUTH_ACCESS_URL = "https://www.openstreetmap.org/oauth/access_token"
        private const val OAUTH_AUTH_URL = "https://www.openstreetmap.org/oauth/authorize"
        // ------------------------------------


        // ------------------------------------
        // Debug Server
        // ------------------------------------
        //private const val DEFAULT_SERVER = "api06.dev.openstreetmap.org" //Debug Server

        //private const val API_KEY = "ElJ0hTlALddTziJA3ZxjRq6cHsRjN48PGfhDL9R9"
        //private const val API_SECRET = "5MususnuRXAZQJEFFqFgfFXAIpHDkLiRVjcRHJxK"

        //private const val OAUTH_REQUEST_URL = "https://master.apis.dev.openstreetmap.org/oauth/request_token"
        //private const val OAUTH_ACCESS_URL = "https://master.apis.dev.openstreetmap.org/oauth/access_token"
        //private const val OAUTH_AUTH_URL = "https://master.apis.dev.openstreetmap.org/oauth/authorize"
        // ------------------------------------


        private val AuthService = ServiceBuilder(API_KEY)
            .apiSecret(API_SECRET)
            .build(object : DefaultApi10a() {
                override fun getRequestTokenEndpoint(): String {
                    return OAUTH_REQUEST_URL
                }

                override fun getAccessTokenEndpoint(): String {
                    return OAUTH_ACCESS_URL
                }

                override fun getAuthorizationBaseUrl(): String {
                    return OAUTH_AUTH_URL
                }
            })
    }

    private val mSettings = Settings.getInstance()

    private fun getConsumer(): CommonsHttpOAuthConsumer {
        val consumer = CommonsHttpOAuthConsumer(API_KEY, API_SECRET)
        consumer.setTokenWithSecret(mSettings.OsmNotes.Token, mSettings.OsmNotes.ConsumerSecret)

        return consumer
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
        getConsumer().sign(HttpRequestAdapter(request))

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
        getConsumer().sign(HttpRequestAdapter(request))

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
        getConsumer().sign(HttpRequestAdapter(request))

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
        getConsumer().sign(HttpRequestAdapter(request))

        val response = request.awaitStringResponse()

        val parsedNotes = OsmNotesParser().parse(response.third)

        if (parsedNotes.count() != 1) {
            throw java.lang.RuntimeException("Received an invalid number of notes: ${parsedNotes.count()}. Expected: 1")
        }

        return parsedNotes[0]
    }

    suspend fun getRequestToken(): OAuth1RequestToken = withContext(Dispatchers.IO) {
        AuthService.requestToken
    }

    fun getRequestUrl(requestToken: OAuth1RequestToken) : String {
        return AuthService.getAuthorizationUrl(requestToken)
    }

    suspend fun getAccessToken(requestToken: OAuth1RequestToken, verifier: String): OAuth1AccessToken = withContext(Dispatchers.IO) {
        AuthService.getAccessToken(requestToken, verifier)
    }
}
