package org.gittner.osmbugs.osmnotes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.scribejava.core.model.OAuth1RequestToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Settings
import org.koin.android.ext.android.inject


class OsmNotesLoginActivity : AppCompatActivity(R.layout.activity_osm_notes_login) {
    private val mApi: OsmNotesApi by inject()
    private val mSettings = Settings.getInstance()

    companion object {
        private var lastRequestToken: OAuth1RequestToken? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.toUri(0).startsWith(OsmNotesApi.AUTH_CALLBACK_URL, false)) {
            authDone(intent)
        } else {
            login()
        }
    }

    // Called when the Activity is started from within the App to open the Login URL
    private fun login() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                lastRequestToken = mApi.getRequestToken()
                val requestUrl = mApi.getRequestUrl(lastRequestToken!!)

                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(requestUrl)))
            } catch (err: Exception) {
                val msg = getString(R.string.login_failed_msg).format(err.message)
                Toast.makeText(this@OsmNotesLoginActivity, msg, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    // Called when the user has granted Access
    private fun authDone(intent: Intent) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val requestToken = lastRequestToken ?: throw Exception("Request Token not yet set")

                val verifier = intent.data?.getQueryParameter(OsmNotesApi.AUTH_VERIFIER_PARAM) ?: throw Exception(getString(R.string.login_failed_no_verifier))

                val accessToken = mApi.getAccessToken(requestToken, verifier)

                mSettings.OsmNotes.Token = accessToken.token
                mSettings.OsmNotes.ConsumerSecret = accessToken.tokenSecret

                Toast.makeText(this@OsmNotesLoginActivity, R.string.login_succeed, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_OK)
                finish()
            } catch (err: Exception) {
                val msg = getString(R.string.login_failed_msg).format(err.message)
                Toast.makeText(this@OsmNotesLoginActivity, msg, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}
