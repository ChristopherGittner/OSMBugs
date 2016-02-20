package org.gittner.osmbugs.statics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Locale;

public class GeoIntentStarter
{
    public static void start(Context context, GeoPoint geoPoint)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format(
                Locale.US,
                "geo:%f,%f",
                geoPoint.getLatitude(),
                geoPoint.getLongitude())));

        if (!canStartActivity(context, intent))
        {
            Toast.makeText(context, R.string.toast_geo_intent_no_app_found, Toast.LENGTH_LONG).show();
            return;
        }

        context.startActivity(intent);
    }


    public static boolean canStartActivity(Context context, Intent intent)
    {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        return activities.size() != 0;
    }
}
