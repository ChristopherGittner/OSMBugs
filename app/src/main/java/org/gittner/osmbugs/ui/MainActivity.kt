package org.gittner.osmbugs.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.gittner.osmbugs.IntentHelper.intentHasReceivers
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.activity_main, menu)

        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(mBinding.toolbar)

        checkPermissions()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_feedback -> startFeedbackIntent()
            R.id.menu_action_settings -> startSettingsFragment()
            else -> return false
        }

        return true
    }

    private fun startSettingsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .addToBackStack("Settings")
            .commit()
    }

    private fun startFeedbackIntent() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(getString(R.string.developer_mail)))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_mail_subject))
        intent.type = "plain/text"

        if (!intentHasReceivers(this, intent)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_feedback_failed_title)
                .setMessage(R.string.dialog_feedback_failed_message)
                .setCancelable(true)
                .create().show()
        }

        startActivity(intent)
    }

    private fun checkPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    afterPermissions()
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(R.string.dialog_permissions_required_title)
                        .setMessage(R.string.dialog_permissions_required_message)
                        .setPositiveButton(R.string.dialog_permissions_required_positive) { _, _: Int ->
                            token.continuePermissionRequest()
                        }
                        .setCancelable(true)
                        .setOnCancelListener {
                            token.cancelPermissionRequest()
                        }
                        .show()
                }
            })
            .check()
    }

    /**
     * Called when all Permissions have been granted by the User
     */
    private fun afterPermissions() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MapFragment())
            .commit()
    }
}
