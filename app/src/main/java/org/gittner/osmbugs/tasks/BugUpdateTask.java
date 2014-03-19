
package org.gittner.osmbugs.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;

public class BugUpdateTask extends AsyncTask<Bug, Void, Boolean> {

    Activity mActivity;

    public BugUpdateTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        /* Show the Round Spinning wheel do display the Upload is running */
        mActivity.setProgressBarIndeterminateVisibility(true);
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
        mActivity.setProgressBarIndeterminateVisibility(false);

        if (result) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.saved_bug), Toast.LENGTH_LONG).show();
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
        } else {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.failed_to_save_bug), Toast.LENGTH_LONG).show();
        }
    }
}
