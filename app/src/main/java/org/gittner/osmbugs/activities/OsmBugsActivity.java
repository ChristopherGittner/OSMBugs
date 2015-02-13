package org.gittner.osmbugs.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.gittner.osmbugs.Helpers.IntentHelper;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.fragments.BugListFragment;
import org.gittner.osmbugs.fragments.BugMapFragment;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class OsmBugsActivity extends ActionBarActivity implements
        BugMapFragment.OnFragmentInteractionListener,
        BugListFragment.OnFragmentInteractionListener {

    private static final String TAG = "OsmBugsActivity";

    /* Request Codes for activities */
    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;
    private static final int REQUEST_CODE_SETTINGS_ACTIVITY = 2;

    /* Dialog Ids */
    private static final int DIALOG_NEW_BUG = 1;

    private static final String TAG_BUG_MAP_FRAGMENT = "BUG_MAP_FRAGMENT";
    private static final String TAG_BUG_LIST_FRAGMENT = "TAG_BUG_LIST_FRAGMENT";

    private static GeoPoint mNewBugLocation;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_osm_bugs);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, BugMapFragment.newInstance(), TAG_BUG_MAP_FRAGMENT)
                    .commit();
        }
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_bugs, menu);

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

            case R.id.action_settings:
                menuSettingsClicked();
                return true;

            case R.id.list:
                menuListClicked();
                return true;

            case R.id.action_feedback:
                onFeedbackClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BUG_EDITOR_ACTIVITY) {
            switch (resultCode)
            {
                case BugEditActivity.RESULT_SAVED_KEEPRIGHT:
                    reloadBugs(Globals.KEEPRIGHT);
                    break;
                case BugEditActivity.RESULT_SAVED_OSMOSE:
                    reloadBugs(Globals.OSMOSE);
                    break;
                case BugEditActivity.RESULT_SAVED_MAPDUST:
                    reloadBugs(Globals.MAPDUST);
                    break;
                case BugEditActivity.RESULT_SAVED_OSM_NOTES:
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

    private void menuListClicked()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BugListFragment.newInstance(), TAG_BUG_LIST_FRAGMENT)
                .addToBackStack(TAG_BUG_LIST_FRAGMENT)
                .commit();
    }

    private void reloadBugs(int platform)
    {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof BugMapFragment)
        {
            BugDatabase.getInstance().load(((BugMapFragment) fragment).getBBox(), platform);
        }
        else
        {
            BugDatabase.getInstance().reload(platform);
        }
    }

    @Override
    public void onBugClicked(Bug bug) {
        /* Open the selected Bug in the Bug Editor */
		Intent i;
		if(bug instanceof KeeprightBug)
		{
			i = new Intent(OsmBugsActivity.this, KeeprightEditActivity.class);
		}
		else if(bug instanceof OsmoseBug)
		{
			i = new Intent(OsmBugsActivity.this, OsmoseEditActivity.class);
		}
		else if(bug instanceof MapdustBug)
		{
			i = new Intent(OsmBugsActivity.this, MapdustEditActivity.class);
		}
		else if(bug instanceof OsmNote)
		{
			i = new Intent(OsmBugsActivity.this, OsmNoteEditActivity.class);
		}
		else
		{
			throw new IllegalArgumentException("Bug type invalid: " + bug.toString());
		}

        i.putExtra(BugEditActivity.EXTRA_BUG, bug);

        startActivityForResult(i, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
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

	private void onFeedbackClicked()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_mail)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_to_osmbugs));
        emailIntent.setType("plain/text");

        if(IntentHelper.intentHasReceivers(this, emailIntent))
        {
            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_feedback)));
            }
            catch (ActivityNotFoundException e)
            {
                Log.e(TAG, "No Email Activity found: " + e.getMessage());
                e.printStackTrace();
                showSendFeedbackErrorDialog();
            }
        }
        else
        {
            showSendFeedbackErrorDialog();
        }
    }

    private void showSendFeedbackErrorDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sending_feedback_failed_title);
        builder.setMessage(R.string.sending_feedback_failed_message);
        builder.setCancelable(true);
        builder.create().show();
    }
}
