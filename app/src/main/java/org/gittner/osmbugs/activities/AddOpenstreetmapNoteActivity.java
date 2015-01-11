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
import android.widget.EditText;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OpenstreetmapNotesApi;
import org.osmdroid.util.GeoPoint;

public class AddOpenstreetmapNoteActivity extends Activity {

    /* The Intents Extras */
    public static final String EXTRALATITUDE = "EXTRALATITUDE";
    public static final String EXTRALONGITUDE = "EXTRALONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_openstreetmap_note);

        /* Hide the ProgressBars at start */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Retrieve the Intents Extras */
        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra(EXTRALATITUDE, 0);
        mLongitude = intent.getDoubleExtra(EXTRALONGITUDE, 0);

        /* Setup the Descriptions EditText */
        EditText edttxtDescription = (EditText) findViewById(R.id.edttxtDescription);
        if (edttxtDescription != null) {
            edttxtDescription.addTextChangedListener(mTextWatcherDescription);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_openstreetmap_note, menu);

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
        }

        EditText edttxtDescription = (EditText) findViewById(R.id.edttxtDescription);

        /* Prepare Parameters */
        TaskParameter parameter = new TaskParameter();
        parameter.geoPoint = new GeoPoint(mLatitude, mLongitude);
        parameter.description = edttxtDescription.getText().toString();

        /* Create and execute AsyncTask */
        new AsyncTask<TaskParameter, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                AddOpenstreetmapNoteActivity.this.setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Boolean doInBackground(TaskParameter... parameters) {
                return OpenstreetmapNotesApi.addNew(parameters[0].geoPoint, parameters[0].description);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                AddOpenstreetmapNoteActivity.this.setProgressBarIndeterminateVisibility(false);

                if (!success) {
                    Toast.makeText(AddOpenstreetmapNoteActivity.this, "Error", Toast.LENGTH_LONG).show();
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

    /* Holds the Latitude of the new Bug */
    private double mLatitude;

    /* Holds the Longitude of the new Bug */
    private double mLongitude;

}
