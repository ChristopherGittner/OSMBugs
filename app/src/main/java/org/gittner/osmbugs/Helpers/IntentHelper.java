package org.gittner.osmbugs.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Class with various functions for Intents
 */
public class IntentHelper
{
    /**
     * Returns whether this Intent can be handled by any Activity
     *
     * @param context The Context of the running App
     * @param intent  The Intent to be Checked
     * @return True if there is an Activity that can handle the Intent
     */
    public static boolean intentHasReceivers(Context context, Intent intent)
    {
        PackageManager mgr = context.getPackageManager();

        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }
}
