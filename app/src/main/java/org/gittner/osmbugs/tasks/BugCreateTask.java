package org.gittner.osmbugs.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.OsmBugsActivity;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.osmdroid.util.GeoPoint;

public class BugCreateTask extends AsyncTask<Void, Void, Boolean> {

    public BugCreateTask(Activity activity, GeoPoint location, String text, int platform) {
        mActivity = activity;
        mGeoPoint = location;
        mText = text;
        mPlatform = platform;
    }

    @Override
    protected void onPreExecute() {
        /* Show the Round Spinning wheel do display the Upload is running */
        mActivity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        switch (mPlatform) {
            case OsmBugsActivity.OPENSTREETMAPNOTES:
                return OpenstreetmapNote.addNew(mGeoPoint, mText);
            case OsmBugsActivity.MAPDUST:
                return MapdustBug.addNew(mGeoPoint, mText);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        /* Hide the Spinnign Wheel */
        mActivity.setProgressBarIndeterminateVisibility(false);

        if (result) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.saved_bug), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.failed_to_save_bug), Toast.LENGTH_LONG).show();
        }
    }

    /* The Activity on which the Task should update Information */
    Activity mActivity;

    /* The Geopoint where the Bug is to be created */
    GeoPoint mGeoPoint;

    /* The Text to be added to the new Bug */
    String mText;

    /* The Platform on which the Bug is to be created */
    int mPlatform;
}
