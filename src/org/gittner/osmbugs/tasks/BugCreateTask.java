
package org.gittner.osmbugs.tasks;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.OsmBugsActivity;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetbugsBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.osmdroid.util.GeoPoint;

import android.os.AsyncTask;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class BugCreateTask extends AsyncTask<Void, Void, Boolean> {

    SherlockActivity activity_;

    GeoPoint location_;

    String text_;

    int platform_;

    public BugCreateTask(SherlockActivity activity, GeoPoint location, String text, int platform) {
        activity_ = activity;
        location_ = location;
        text_ = text;
        platform_ = platform;
    }

    @Override
    protected void onPreExecute() {
        /* Show the Round Spinning wheel do display the Upload is running */
        activity_.setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        switch (platform_) {
            case OsmBugsActivity.OPENSTREETBUGS:
                return OpenstreetbugsBug.addNew(location_, text_);
            case OsmBugsActivity.OPENSTREETMAPNOTES:
                return OpenstreetmapNote.addNew(location_, text_);
            case OsmBugsActivity.MAPDUST:
                return MapdustBug.addNew(location_, text_);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        /* Hide the Spinnign Wheel */
        activity_.setSupportProgressBarIndeterminateVisibility(false);

        if (result) {
            Toast.makeText(activity_.getApplicationContext(), activity_.getApplicationContext().getString(R.string.saved_bug), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(activity_.getApplicationContext(), activity_.getApplicationContext().getString(R.string.failed_to_save_bug), Toast.LENGTH_LONG).show();
        }
    }
}
