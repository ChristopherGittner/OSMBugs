
package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.gittner.osmbugs.tasks.BugCreateTask;
import org.gittner.osmbugs.tasks.DownloadBugsTask;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class OsmBugsActivity extends Activity
        implements
        LocationListener,
        OnItemGestureListener<Bug> {

    public static final int REQUESTCODEBUGEDITORACTIVITY = 1;

    public static final int REQUESTCODESETTINGSACTIVITY = 2;

    private static final int DIALOGNEWBUG = 1;

    private static final int DIALOGNEWBUGTEXT = 2;

    private static final int DIALOGABOUT = 3;

    public static final int INVALIDPLATFORM = -1;

    public static final int KEEPRIGHT = 1;

    public static final int OPENSTREETBUGS = 2;

    public static final int OPENSTREETMAPNOTES = 3;

    public static final int MAPDUST = 4;

    private LocationManager mLocMgr = null;

    private MapView mMapView = null;

    private ArrayList<Bug> mBugs;

    private ItemizedIconOverlay<Bug> mBugOverlay;

    /* The Location Marker Overlay */
    private ItemizedIconOverlay<OverlayItem> mLocationOverlay;

    private boolean mAddNewBugOnNextClick = false;

    /* Used to save the Point where to create the new Bug */
    private static GeoPoint mNewBugLocation;

    /* Used to save the new Bugs platform */
    private static int mNewBugPlatform;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_osm_bugs);

        /*
         * For devices that use ActionBarSherlock the Indeterminate State has to be set to false
         * otherwise it will be displayed at start
         */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Init Settings Class */
        Settings.init(this);

        /* Init the Drawings Class to load all Resources */
        Drawings.init(this);

        /* Create Bug Overlay */
        mBugs = new ArrayList<Bug>();
        mBugOverlay = new ItemizedIconOverlay<Bug>(mBugs, Drawings.KeeprightDrawable30, this, new DefaultResourceProxyImpl(this));

        /* Create Position Marker Overlay */
        mLocationOverlay =
                new ItemizedIconOverlay<OverlayItem>(
                        new ArrayList<OverlayItem>(),
                        new OnItemGestureListener<OverlayItem>() {
                            @Override
                            public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                                return false;
                            }

                            @Override
                            public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
                                return false;
                            }
                        },
                        new DefaultResourceProxyImpl(this));

        /* Setup Main MapView */
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getController().setZoom(20);
        mMapView.getOverlays().add(mBugOverlay);
        mMapView.getOverlays().add(mLocationOverlay);

        /*
         * This adds an empty Overlay to retrieve the Touch Events This is some sort of Hack, since
         * the OnTouchListener will fire only once if the Built in Zoom Controls are enabled
         */
        mMapView.getOverlays().add(new Overlay(this) {
            @Override
            protected void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouchEvent(MotionEvent event, MapView mapView) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && mAddNewBugOnNextClick) {
                    mAddNewBugOnNextClick = false;
                    mNewBugLocation =
                            (GeoPoint) mMapView.getProjection().fromPixels(event.getX(),
                                    event.getY());
                    showDialog(DIALOGNEWBUG);
                    return false;
                }

                return super.onTouchEvent(event, mapView);
            }
        });

        /* Setup the LocationManager */
        mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        /* Set the start Location */
        mLastLocation = Settings.getLastKnownLocation();
        centerMap(mLastLocation, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Start Listening to Location updates */
        mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* Stop Listening to Location updates */
        mLocMgr.removeUpdates(this);

        /* Save the last Location */
        Settings.setLastKnownLocation(mMapView.getMapCenter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_bugs, menu);
        menu.findItem(R.id.center_gps).setChecked(Settings.getCenterGps());
        return true;
    }

    @SuppressWarnings({
            "deprecation"
    })
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            /* Start the Settings Activity */
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, REQUESTCODESETTINGSACTIVITY);
            return true;
        } else if (item.getItemId() == R.id.center_gps) {
            /* Toggle GPS Map Following */
            item.setChecked(!item.isChecked());
            Settings.setCenterGps(!Settings.getCenterGps());

            /*
             * On android API <= 10 The Menu won't display a checkbox so we show a Toast with the
             * Status
             */
            if (android.os.Build.VERSION.SDK_INT <= 10) {
                if (Settings.getCenterGps())
                    Toast.makeText(this, getString(R.string.center_on_gps_enabled), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, getString(R.string.center_on_gps_disabled), Toast.LENGTH_LONG).show();
            }

            return true;
        } else if (item.getItemId() == R.id.refresh) {
            /* Update the current Bugs */
            refreshBugs();
        } else if (item.getItemId() == R.id.center_gps_action) {
            centerMap(mLastLocation, true);
        } else if (item.getItemId() == R.id.about) {
            this.showDialog(DIALOGABOUT);
        } else if (item.getItemId() == R.id.add_bug) {
            if (!mAddNewBugOnNextClick) {
                mAddNewBugOnNextClick = true;
                Toast.makeText(this, getString(R.string.bug_creation_mode_enabled), Toast.LENGTH_LONG).show();
            } else {
                mAddNewBugOnNextClick = false;
                Toast.makeText(this, getString(R.string.bug_creation_mode_disabled), Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshBugs() {
        new DownloadBugsTask(this, mBugOverlay, mMapView, mMapView.getBoundingBox()).execute();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        centerMap(mLastLocation, false);
    }

    private void centerMap(Location location, boolean force) {
        /* Center Map on GPS Position if activated */
        if (Settings.getCenterGps() || force) {
            mMapView.getController().setCenter(new GeoPoint(location));
        }

        /* Update the Location Marker */
        mLocationOverlay.removeAllItems();
        OverlayItem i = new OverlayItem("", "", new GeoPoint(location));
        i.setMarker(Drawings.LocationMarker);
        mLocationOverlay.addItem(i);
        mMapView.invalidate();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onItemLongPress(int n, Bug bug) {
        return false;
    }

    @Override
    public boolean onItemSingleTapUp(int index, Bug bug) {
        /* Open the selected Bug in the Bug Editor */
        Intent i = new Intent(this, BugEditorActivity.class);
        i.putExtra(BugEditorActivity.EXTRABUG, bug);

        startActivityForResult(i, REQUESTCODEBUGEDITORACTIVITY);

        return false;
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
                    if (spnPlatform.getSelectedItemPosition() == 0)
                        mNewBugPlatform = OPENSTREETMAPNOTES;
                    else if (spnPlatform.getSelectedItemPosition() == 2)
                        mNewBugPlatform = MAPDUST;
                    else
                        mNewBugPlatform = INVALIDPLATFORM;

                    if (mNewBugPlatform != INVALIDPLATFORM)
                        showDialog(DIALOGNEWBUGTEXT);

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();
        } else if (id == DIALOGNEWBUGTEXT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText newBugText = new EditText(this);
            builder.setView(newBugText);

            builder.setMessage(getString(R.string.new_bugs_text));
            builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!newBugText.getText().toString().equals(""))
                        createBug(mNewBugPlatform, newBugText.getText().toString());

                    dialog.dismiss();
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

    private void createBug(int platform, String text) {
        new BugCreateTask(
                this,
                new GeoPoint(
                        mNewBugLocation.getLatitudeE6() / 1000000.0,
                        mNewBugLocation.getLongitudeE6() / 1000000.0),
                text,
                platform).execute();
    }
}
