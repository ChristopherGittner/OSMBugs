
package org.gittner.osmbugs.tasks;

import com.actionbarsherlock.app.SherlockActivity;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class BugUpdateTask extends AsyncTask<Bug, Void, Boolean> {

    SherlockActivity activity_;

    public BugUpdateTask(SherlockActivity activity) {
        activity_ = activity;
    }

    @Override
    protected void onPreExecute() {
        /* Show the Round Spinning wheel do display the Upload is running */
        activity_.setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Bug... bugs) {
        for (int i = 0; i != bugs.length; ++i) {
            if (!bugs[i].commit())
                return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        /* Hide the Spinnign Wheel */
        activity_.setSupportProgressBarIndeterminateVisibility(false);

        if (result) {
            Toast.makeText(activity_.getApplicationContext(),
                    activity_.getApplicationContext().getString(R.string.saved_bug),
                    Toast.LENGTH_LONG).show();
            activity_.setResult(Activity.RESULT_OK);
            activity_.finish();
        }
        else {
            Toast.makeText(activity_.getApplicationContext(),
                    activity_.getApplicationContext().getString(R.string.failed_to_save_bug),
                    Toast.LENGTH_LONG).show();
        }
    }
}
