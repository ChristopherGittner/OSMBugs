package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
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
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class OsmBugsActivity extends Activity implements
        BugMapFragment.OnFragmentInteractionListener,
        BugListFragment.OnFragmentInteractionListener {

    /* Request Codes for activities */
    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;
    private static final int REQUEST_CODE_SETTINGS_ACTIVITY = 2;

    /* Dialog Ids */
    private static final int DIALOG_NEW_BUG = 1;
    private static final int DIALOG_ABOUT = 2;

    private static final String TAG_BUG_MAP_FRAGMENT = "BUG_MAP_FRAGMENT";
    private static final String TAG_BUG_LIST_FRAGMENT = "TAG_BUG_LIST_FRAGMENT";

    private static GeoPoint mNewBugLocation;

    private int mActiveDownloads = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_osm_bugs);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, BugMapFragment.newInstance(), TAG_BUG_MAP_FRAGMENT)
                    .commit();
        }

        /* Hide the ProgressBars at start */
        setProgressBarIndeterminateVisibility(false);

        /* Init Settings Class */
        Settings.init(this);

        /* Init the Drawings Class to load all Resources */
        Drawings.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_bugs, menu);

        /* Hide the Refresh Button if a Download is in Progress */
        if(mActiveDownloads != 0) {
            menu.findItem(R.id.refresh).setEnabled(false);
            setProgressBarIndeterminateVisibility(true);
        }
        else {
            menu.findItem(R.id.refresh).setEnabled(true);
            setProgressBarIndeterminateVisibility(false);
        }

        if(getFragmentManager().findFragmentById(R.id.container).getTag().equals(TAG_BUG_LIST_FRAGMENT)) {
            menu.findItem(R.id.list).setVisible(false);
        }
        else {
            menu.findItem(R.id.list).setVisible(true);
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

            case R.id.list:
                menuListClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BUG_EDITOR_ACTIVITY) {
            switch (resultCode)
            {
                case BugEditorActivity.RESULT_SAVED_KEEPRIGHT:
                    reloadBugs(Globals.KEEPRIGHT);
                    break;
                case BugEditorActivity.RESULT_SAVED_OSMOSE:
                    reloadBugs(Globals.OSMOSE);
                    break;
                case BugEditorActivity.RESULT_SAVED_MAPDUST:
                    reloadBugs(Globals.MAPDUST);
                    break;
                case BugEditorActivity.RESULT_SAVED_OSM_NOTES:
                    reloadBugs(Globals.OSM_NOTES);
                    break;
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(int id) {

        if (id == DIALOG_NEW_BUG) {
            final Spinner spnPlatform = new Spinner(this);

            /* Add the available Error Platforms to the Spinner */
            ArrayList<String> spinnerArray = new ArrayList<>();
            for (int i = 0; i != getResources().getStringArray(R.array.new_bug_platforms).length; ++i) {
                spinnerArray.add(getResources().getStringArray(R.array.new_bug_platforms)[i]);
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spnPlatform.setAdapter(spinnerArrayAdapter);

            return new AlertDialog.Builder(this)
                    .setView(spnPlatform)
                    .setMessage(getString(R.string.platform))
                    .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (spnPlatform.getSelectedItemPosition() == 0) {
                                Intent i = new Intent(OsmBugsActivity.this, AddOpenstreetmapNoteActivity.class);

                                i.putExtra(AddMapdustBugActivity.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                                i.putExtra(AddMapdustBugActivity.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());

                                startActivity(i);
                            } else if (spnPlatform.getSelectedItemPosition() == 1) {
                                Intent i = new Intent(OsmBugsActivity.this, AddMapdustBugActivity.class);

                                i.putExtra(AddOpenstreetmapNoteActivity.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                                i.putExtra(AddOpenstreetmapNoteActivity.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());

                                startActivity(i);
                            } else {
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }

        return super.onCreateDialog(id);
    }

    private void menuSettingsClicked() {
        /* Start the Settings Activity */
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, REQUEST_CODE_SETTINGS_ACTIVITY);
    }

    private void menuRefreshClicked() {
        reloadAllBugs();
    }

    private void menuListClicked()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BugListFragment.newInstance(), TAG_BUG_LIST_FRAGMENT)
                .addToBackStack(TAG_BUG_MAP_FRAGMENT)
                .commit();
    }

    private void reloadAllBugs() {
        reloadBugs(Globals.KEEPRIGHT);
        reloadBugs(Globals.OSMOSE);
        reloadBugs(Globals.MAPDUST);
        reloadBugs(Globals.OSM_NOTES);
    }

    private void reloadBugs(int platform)
    {
        mActiveDownloads += 1;

        invalidateOptionsMenu();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof BugMapFragment)
        {
            BugDatabase.getInstance().reload(((BugMapFragment) fragment).getBBox(), platform, mOnDownloadEndListener);
        }
        else
        {
            BugDatabase.getInstance().reload(platform, mOnDownloadEndListener);
        }
    }

    private final BugDatabase.OnDownloadEndListener mOnDownloadEndListener = new BugDatabase.OnDownloadEndListener() {
        @Override
        public void onCompletion() {
            if(mActiveDownloads > 0) {
                mActiveDownloads -= 1;
            }
            invalidateOptionsMenu();
        }
    };

    @Override
    public void onBugClicked(Bug bug) {
        /* Open the selected Bug in the Bug Editor */
        Intent i = new Intent(OsmBugsActivity.this, BugEditorActivity.class);
        i.putExtra(BugEditorActivity.EXTRA_BUG, bug);

        startActivityForResult(i, REQUEST_CODE_BUG_EDITOR_ACTIVITY);

        Log.w("", "Bug " + bug.getPoint().toString());
    }

    @Override
    public void onBugMiniMapClicked(Bug bug) {
        /* Display the Map centered at the clicked Bug
         * and disable gps Following */
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
        showDialog(DIALOG_NEW_BUG);
    }
}
