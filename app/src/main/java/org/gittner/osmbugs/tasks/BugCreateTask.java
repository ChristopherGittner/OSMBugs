
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

    Activity mActivity;

    GeoPoint mLocation;

    String mText;

    int mPlatform;

    public BugCreateTask(Activity activity, GeoPoint location, String text, int platform) {
        mActivity = activity;
        mLocation = location;
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
                return OpenstreetmapNote.addNew(mLocation, mText);
            case OsmBugsActivity.MAPDUST:
                return MapdustBug.addNew(mLocation, mText);
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
}
