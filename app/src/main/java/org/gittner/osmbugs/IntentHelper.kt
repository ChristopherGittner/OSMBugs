package org.gittner.osmbugs

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


/**
 * Class with various functions for Intents
 */
object IntentHelper {
    /**
     * Returns whether this Intent can be handled by any Activity
     *
     * @param context The Context of the running App
     * @param intent  The Intent to be Checked
     * @return True if there is an Activity that can handle the Intent
     */
    fun intentHasReceivers(context: Context, intent: Intent): Boolean {
        val list = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }
}
