package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.fragments.BugListFragment;
import org.gittner.osmbugs.fragments.BugMapFragment;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class OsmBugsActivity extends Activity implements
        BugMapFragment.OnFragmentInteractionListener,
        BugListFragment.OnFragmentInteractionListener {

    /* Request Codes for activities */
    public static final int REQUESTCODEBUGEDITORACTIVITY = 1;
    public static final int REQUESTCODESETTINGSACTIVITY = 2;

    /* Dialog Ids */
    private static final int DIALOGNEWBUG = 1;
    private static final int DIALOGABOUT = 2;

    private static final String TAG_BUG_MAP_FRAGMENT = "BUGMAPFRAGMENT";
    private static final String TAG_BUG_LIST_FRAGMENT = "BUGLISTFRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_osm_bugs);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, BugMapFragment.newInstance(), TAG_BUG_MAP_FRAGMENT)
                    .commit();
        }

        /* Hide the ProgressBars at start */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Init Settings Class */
        Settings.init(this);

        /* Init the Drawings Class to load all Resources */
        Drawings.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_bugs, menu);

        /* Hide the Refresh Button if a Download is in Progress */
        if(mDownloadActive) {
            menu.findItem(R.id.refresh).setVisible(false);
        }
        else {
            menu.findItem(R.id.refresh).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                menuRefreshClicked();
                return true;

            case R.id.action_settings:
                menuSettingsClicked();
                return true;

            case R.id.about:
                menuAboutClicked();
                return true;

            case R.id.list:
                menuListClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODEBUGEDITORACTIVITY) {
            if (resultCode == Activity.RESULT_OK)
                refreshBugs();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(int id) {

        if (id == DIALOGNEWBUG) {
            /* Ask the User to select a Bug Platform */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final Spinner spnPlatform = new Spinner(this);

            /* Add the availabe Error Platforms to the Spinner */
            ArrayList<String> spinnerArray = new ArrayList<String>();
            for (int i = 0; i != getResources().getStringArray(R.array.new_bug_platforms).length; ++i) {
                spinnerArray.add(getResources().getStringArray(R.array.new_bug_platforms)[i]);
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spnPlatform.setAdapter(spinnerArrayAdapter);

            builder.setView(spnPlatform);

            builder.setMessage(getString(R.string.platform));
            builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (spnPlatform.getSelectedItemPosition() == 0) {
                        Intent i = new Intent(OsmBugsActivity.this, AddOpenstreetmapNoteActivity.class);

                        i.putExtra(AddMapdustBugActivity.EXTRALATITUDE, mNewBugLocation.getLatitude());
                        i.putExtra(AddMapdustBugActivity.EXTRALONGITUDE, mNewBugLocation.getLongitude());

                        startActivity(i);

                        dialog.dismiss();
                    } else if (spnPlatform.getSelectedItemPosition() == 1) {
                        Intent i = new Intent(OsmBugsActivity.this, AddMapdustBugActivity.class);

                        i.putExtra(AddOpenstreetmapNoteActivity.EXTRALATITUDE, mNewBugLocation.getLatitude());
                        i.putExtra(AddOpenstreetmapNoteActivity.EXTRALONGITUDE, mNewBugLocation.getLongitude());

                        startActivity(i);

                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();
        } else if (id == DIALOGABOUT) {
            /* Show the About Information */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.dialog_about_text));
            builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();
        }

        return super.onCreateDialog(id);
    }

    private void menuSettingsClicked() {
        /* Start the Settings Activity */
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, REQUESTCODESETTINGSACTIVITY);
    }

    private void menuRefreshClicked() {
        /* Update all Bugs */
        refreshBugs();
    }

    private void menuAboutClicked() {
        //noinspection deprecation
        this.showDialog(DIALOGABOUT);
    }

    private void menuListClicked()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BugListFragment.newInstance(), TAG_BUG_LIST_FRAGMENT)
                .addToBackStack(TAG_BUG_LIST_FRAGMENT)
                .commit();
    }

    /* Reload all Bugs */
    private void refreshBugs() {
        mDownloadActive = true;

        /* Start the Progress Bar */
        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);
        setProgress(0);

        /* Request Options Menu reload */
        invalidateOptionsMenu();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof BugMapFragment)
        {
            BugDatabase.getInstance().DownloadBBox(((BugMapFragment) fragment).getBBox(), mOnDownloadEndListener);
        }
        else
        {
            BugDatabase.getInstance().Reload(mOnDownloadEndListener);
        }
    }

    private BugDatabase.OnDownloadEndListener mOnDownloadEndListener = new BugDatabase.OnDownloadEndListener() {
        @Override
        public void onSuccess(int platform) {
        }

        @Override
        public void onError(int platform) {
        }

        @Override
        public void onCompletion() {

            mDownloadActive = false;

            /* Stop the Progressbar */
            setProgressBarIndeterminateVisibility(false);
            setProgressBarVisibility(false);

            invalidateOptionsMenu();
        }

        @Override
        public void onProgressUpdate(double progress) {
            setProgress((int) (progress * 10000));
        }
    };

    /* Used to save the Point where to create the new Bug */
    private static GeoPoint mNewBugLocation;

    @Override
    public void onBugClicked(Bug bug) {
        /* Open the selected Bug in the Bug Editor */
        Intent i = new Intent(OsmBugsActivity.this, BugEditorActivity.class);
        i.putExtra(BugEditorActivity.EXTRABUG, bug);

        startActivityForResult(i, REQUESTCODEBUGEDITORACTIVITY);

        Log.w("", "Bug " + bug.getPoint().toString());
    }

    @Override
    public void onBugMapClicked(final Bug bug) {
        /* Display the Map centered at the clicked Bug
         * and disable gps Following  */

        BugMapFragment bugMapFragment = BugMapFragment.newInstance(17, bug.getPoint());

        getFragmentManager().beginTransaction()
                .replace(R.id.container, bugMapFragment, TAG_BUG_MAP_FRAGMENT)
                .addToBackStack(TAG_BUG_MAP_FRAGMENT)
                .commit();

        Settings.setFollowGps(false);
        invalidateOptionsMenu();
    }

    @Override
    public void onAddNewBug(GeoPoint point) {
        mNewBugLocation = point;

        //noinspection deprecation
        showDialog(DIALOGNEWBUG);
    }

    /* True if a Bug Download is currently active */
    private boolean mDownloadActive = false;
}
