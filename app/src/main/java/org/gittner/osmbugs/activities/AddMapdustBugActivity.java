package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.osmdroid.util.GeoPoint;

public class AddMapdustBugActivity extends Activity {

    /* The Intents Extras */
    public static final String EXTRALATITUDE = "EXTRALATITUDE";
    public static final String EXTRALONGITUDE = "EXTRALONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mapdust_bug);

        /* Hide the ProgressBars at start */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Retrieve the Intents Extras */
        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra(EXTRALATITUDE, 0);
        mLongitude = intent.getDoubleExtra(EXTRALONGITUDE, 0);

        /* Setup the Type Adapter */
        mSpnType = (Spinner) findViewById(R.id.spnType);

        mTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSpnType.setAdapter(mTypeAdapter);

        mTypeAdapter.add(getString(R.string.wrong_turn));
        mTypeAdapter.add(getString(R.string.bad_routing));
        mTypeAdapter.add(getString(R.string.oneway_road));
        mTypeAdapter.add(getString(R.string.blocked_street));
        mTypeAdapter.add(getString(R.string.missing_street));
        mTypeAdapter.add(getString(R.string.roundabout));
        mTypeAdapter.add(getString(R.string.missing_speed_info));
        mTypeAdapter.add(getString(R.string.other));

        mSpnType.setSelection(7);

        mTypeAdapter.notifyDataSetChanged();

        /* Setup the Descriptions EditText */
        EditText edttxtDescription = (EditText) findViewById(R.id.edttxtDescription);
        if (edttxtDescription != null) {
            edttxtDescription.addTextChangedListener(mTextWatcherDescription);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mapdust_bug, menu);

        EditText edttxtDescription = (EditText) findViewById(R.id.edttxtDescription);
        MenuItem menuItemSave = menu.findItem(R.id.action_save);

        /* Enable or Disable the Save Entry */
        if (edttxtDescription != null && menuItemSave != null) {
            if (!edttxtDescription.getText().toString().equals("")) {
                menuItemSave.setVisible(true);
            } else {
                menuItemSave.setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_cancel:
                menuCancelClicked(item);
                return true;

            case R.id.action_save:
                menuSaveClicked(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void menuCancelClicked(MenuItem item) {
        finish();
    }

    private void menuSaveClicked(MenuItem item) {
        /* Temporary class to pass parameters to Async Task */
        class TaskParameter {
            GeoPoint geoPoint;
            String description;
            int type;
        }

        EditText edttxtDescription = (EditText) findViewById(R.id.edttxtDescription);

        int type = 7;
        switch (mSpnType.getSelectedItemPosition()) {
            case 0:
                type = MapdustBug.WRONGTURN;
                break;

            case 1:
                type = MapdustBug.BADROUTING;
                break;

            case 2:
                type = MapdustBug.ONEWAYROAD;
                break;

            case 3:
                type = MapdustBug.BLOCKEDSTREET;
                break;

            case 4:
                type = MapdustBug.MISSINGSTREET;
                break;

            case 5:
                type = MapdustBug.ROUNDABOUTISSUE;
                break;

            case 6:
                type = MapdustBug.MISSINGSPEEDINFO;
                break;

            case 7:
                type = MapdustBug.OTHER;
                break;
        }

        /* Prepare Parameters */
        TaskParameter parameter = new TaskParameter();
        parameter.geoPoint = new GeoPoint(mLatitude, mLongitude);
        parameter.description = edttxtDescription.getText().toString();
        parameter.type = type;

        /* Create and execute AsyncTask */
        new AsyncTask<TaskParameter, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                AddMapdustBugActivity.this.setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Boolean doInBackground(TaskParameter... parameters) {
                return MapdustApi.addBug(parameters[0].geoPoint, parameters[0].type, parameters[0].description);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                AddMapdustBugActivity.this.setProgressBarIndeterminateVisibility(false);

                if (!success) {
                    Toast.makeText(AddMapdustBugActivity.this, "Error", Toast.LENGTH_LONG).show();
                } else {
                    finish();
                }
            }
        }.execute(parameter);

    }

    /* TextWatcher to show or hide the Save Button */
    private TextWatcher mTextWatcherDescription = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            invalidateOptionsMenu();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /* The Spinner of different Types */
    private Spinner mSpnType;

    /* The Adapter for the types Spinner */
    private ArrayAdapter<String> mTypeAdapter;

    /* Holds the Latitude of the new Bug */
    private double mLatitude;

    /* Holds the Longitude of the new Bug */
    private double mLongitude;
}
