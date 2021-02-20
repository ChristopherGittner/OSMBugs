package org.gittner.osmbugs.osmnotes

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.OAuthWebClient
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.ActivityOsmNotesLoginBinding
import org.gittner.osmbugs.statics.Settings
import org.koin.android.ext.android.inject


class OsmNotesLoginActivity : AppCompatActivity(R.layout.activity_osm_notes_login), OAuthWebClient.AuthDone {
    private val mApi : OsmNotesApi by inject()
    private val mSettings = Settings.getInstance()

    private lateinit var mBinding : ActivityOsmNotesLoginBinding

    companion object {
        const val CALLBACK_URL = "osmbugs://osmbugs.gittner.org/auth-done/osmbugs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOsmNotesLoginBinding.inflate(layoutInflater)
        mBinding.webview.webViewClient = OAuthWebClient(CALLBACK_URL, this)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val requestUrl = mApi.getRequestToken()

                mBinding.webview.loadUrl(requestUrl)
            } catch (err: Exception) {
                val msg = getString(R.string.login_failed_msg).format(err.message)
                Toast.makeText(this@OsmNotesLoginActivity, msg, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun authDone(token: String?) {
        if (token == null) {
            Toast.makeText(this@OsmNotesLoginActivity, R.string.login_failed_no_token, Toast.LENGTH_LONG).show()

            setResult(Activity.RESULT_CANCELED)
            finish()

            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val data = mApi.getAccessToken(token)

                mSettings.OsmNotes.Token = data.first
                mSettings.OsmNotes.ConsumerSecret = data.second

                Toast.makeText(this@OsmNotesLoginActivity, R.string.login_succeed, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_OK)
                finish()
            } catch(err : Exception) {
                val msg = getString(R.string.login_failed_msg).format(err.message)
                Toast.makeText(this@OsmNotesLoginActivity, msg, Toast.LENGTH_LONG).show()

                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}
