package org.gittner.osmbugs.osmnotes

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.AuthenticatedRequest
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import org.gittner.osmbugs.statics.Settings
import org.koin.core.component.KoinComponent
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox
import java.util.*


class OsmNotesApi(context: Context) : KoinComponent {
    companion object {
        private const val API_SCHEME = "https"

        private const val PATH_SEGMENT_API = "api"
        private const val PATH_SEGMENT_VERSION = "0.6"
        private const val PATH_SEGMENT_NOTES = "notes"

        const val AUTH_CALLBACK_URL = "osmbugs://org.gittner.osmbugs/auth"

        // ------------------------------------
        // Live Server
        // ------------------------------------
        private const val DEFAULT_SERVER = "api.openstreetmap.org"

        private const val CLIENT_ID = "PJqrMYJtFFQ0Al3KDySlyZxcUIc7_xnavtcWzH7QdNs"
        private const val CLIENT_SECRET = "TF5Dt0tj3-CNDeelwjKsdVEO9KIREd8IUJ7QzH1m6aw"

        private const val OAUTH_AUTHORIZE_URL = "https://www.openstreetmap.org/oauth2/authorize"
        private const val OAUTH_ACCESS_URL = "https://www.openstreetmap.org/oauth2/token"
        // ------------------------------------


        // ------------------------------------
        // Debug Server
        // ------------------------------------
        //private const val DEFAULT_SERVER = "api06.dev.openstreetmap.org"

        //private const val CLIENT_ID = "OuCGNYL3J0QbwV4UrKgrTC0MQCtrmFy8NJg6SGr4PDg"
        //private const val CLIENT_SECRET = "PpQovsEbpZ3A1ZXTGB4VM3L8V963m26rYNJJK7LZTxc "

        //private const val OAUTH_AUTHORIZE_URL = "https://master.apis.dev.openstreetmap.org/oauth2/authorize"
        //private const val OAUTH_ACCESS_URL = "https://master.apis.dev.openstreetmap.org/oauth2/token"
        // ------------------------------------
    }

    private var serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(OAUTH_AUTHORIZE_URL),
        Uri.parse(OAUTH_ACCESS_URL))

    private var mAuthState = AuthState(serviceConfig)

    fun isLoggedIn(): Boolean {
        return mAuthState.isAuthorized
    }

    private var mAuthService : AuthorizationService

    fun getAuthIntent() : Intent{
        return mAuthService.getAuthorizationRequestIntent(
            AuthorizationRequest.Builder(
                serviceConfig,
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(AUTH_CALLBACK_URL)).
            setScope("write_notes").
            build())
    }

    fun authDone(data: Intent) {
        val resp = AuthorizationResponse.fromIntent(data)
        val ex = AuthorizationException.fromIntent(data)

        mAuthState.update(resp, ex)

        mSettings.OsmNotes.OAuth2 = mAuthState.jsonSerializeString()

        if (resp != null) {
            val clientAuth : ClientAuthentication = ClientSecretBasic(CLIENT_SECRET)

            mAuthService.performTokenRequest(resp.createTokenExchangeRequest(), clientAuth) { tokenResp, tokenEx ->
                if (tokenResp != null) {
                    mAuthState.update(tokenResp, tokenEx);

                    mSettings.OsmNotes.OAuth2 = mAuthState.jsonSerializeString()
                }
            }
        }
    }

    private val mSettings = Settings.getInstance()

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

        val request = AuthenticatedRequest(Fuel.post(url.toString())).bearer(mAuthState.accessToken!!)

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

        val request = AuthenticatedRequest(Fuel.post(url.toString())).bearer(mAuthState.accessToken!!)

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

        val request = AuthenticatedRequest(Fuel.post(url.toString())).bearer(mAuthState.accessToken!!)

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

        val request = AuthenticatedRequest(Fuel.post(url.toString())).bearer(mAuthState.accessToken!!)

        val response = request.awaitStringResponse()

        val parsedNotes = OsmNotesParser().parse(response.third)

        if (parsedNotes.count() != 1) {
            throw java.lang.RuntimeException("Received an invalid number of notes: ${parsedNotes.count()}. Expected: 1")
        }
        return parsedNotes[0]
    }

    init {
        val data = mSettings.OsmNotes.OAuth2
        if (data.isNotEmpty()) {
            mAuthState = AuthState.jsonDeserialize(data)
        }
        mAuthService = AuthorizationService(context)
    }
}
