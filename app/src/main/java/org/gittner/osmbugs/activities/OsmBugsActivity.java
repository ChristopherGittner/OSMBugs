package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Settings;
import org.gittner.osmbugs.tasks.DownloadBugsTask;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class OsmBugsActivity extends Activity {

    /* Request Codes for activities */
    public static final int REQUESTCODEBUGEDITORACTIVITY = 1;
    public static final int REQUESTCODESETTINGSACTIVITY = 2;

    /* Dialog Ids */
    private static final int DIALOGNEWBUG = 1;
    private static final int DIALOGNEWBUGTEXT = 2;
    private static final int DIALOGABOUT = 3;

    /* Platform Ids */
    public static final int INVALIDPLATFORM = -1;
    public static final int KEEPRIGHT = 1;
    public static final int OPENSTREETBUGS = 2;
    public static final int OPENSTREETMAPNOTES = 3;
    public static final int MAPDUST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_osm_bugs);

        /* Hide the ProgressBars at start */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Init Settings Class */
        Settings.init(this);

        /* Init the Drawings Class to load all Resources */
        Drawings.init(this);

        /* Create Bug Overlay */
        mBugs = new ArrayList<Bug>();
        mBugOverlay = new ItemizedIconOverlay<Bug>(
                mBugs,
                Drawings.KeeprightDrawable30,
                mOnitemGestureListener,
                new DefaultResourceProxyImpl(this));

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
                        new DefaultResourceProxyImpl(this)
                );

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

        /* Set the Initial Center of the map */
        mMapView.getController().setCenter(Settings.getLastKnownLocation());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Start Listening to Location updates */
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = mLocMgr.getBestProvider(locationCriteria, true);

        if (bestProvider == null)
            return;

        mLocMgr.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* Stop Listening to Location updates */
        mLocMgr.removeUpdates(mLocationListener);

        /* Save the last Location */
        Settings.setLastKnownLocation(mMapView.getMapCenter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_bugs, menu);
        menu.findItem(R.id.follow_gps).setChecked(Settings.getFollowGps());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                menuSettingsClicked(item);
                return true;

            case R.id.follow_gps:
                menuCenterGpsClicked(item);
                return true;

            case R.id.refresh:
                menuRefreshClicked(item);
                return true;

            case R.id.go_to_gps:
                menuGoToGPSClicked(item);
                return true;

            case R.id.about:
                menuAboutClicked(item);
                return true;

            case R.id.add_bug:
                menuAddBugClicked(item);
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
                        Intent i = new Intent(OsmBugsActivity.this, AddOpenstreetmapNote.class);

                        i.putExtra(AddMapdustBugActivity.EXTRALATITUDE, mNewBugLocation.getLatitude());
                        i.putExtra(AddMapdustBugActivity.EXTRALONGITUDE, mNewBugLocation.getLongitude());

                        startActivity(i);

                        dialog.dismiss();
                    } else if (spnPlatform.getSelectedItemPosition() == 1) {
                        Intent i = new Intent(OsmBugsActivity.this, AddMapdustBugActivity.class);

                        i.putExtra(AddOpenstreetmapNote.EXTRALATITUDE, mNewBugLocation.getLatitude());
                        i.putExtra(AddOpenstreetmapNote.EXTRALONGITUDE, mNewBugLocation.getLongitude());

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

    private void menuSettingsClicked(MenuItem item) {
        /* Start the Settings Activity */
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, REQUESTCODESETTINGSACTIVITY);
    }

    private void menuCenterGpsClicked(MenuItem item) {
        /* Toggle GPS Map Following */
        item.setChecked(!item.isChecked());
        Settings.setCenterGps(!Settings.getFollowGps());
    }

    private void menuRefreshClicked(MenuItem item) {
        /* Update all Bugs */
        refreshBugs();
    }

    private void menuGoToGPSClicked(MenuItem item) {
        /* Center the GPS on the last known Location */
        if (mLastLocation != null)
            mMapView.getController().setCenter(new GeoPoint(mLastLocation));
    }

    private void menuAboutClicked(MenuItem item) {
        //noinspection deprecation
        this.showDialog(DIALOGABOUT);
    }

    private void menuAddBugClicked(MenuItem item) {
        /* Enable or Disable the Bug creation mode */
        if (!mAddNewBugOnNextClick) {
            mAddNewBugOnNextClick = true;
            Toast.makeText(this, getString(R.string.bug_creation_mode_enabled), Toast.LENGTH_LONG).show();
        } else {
            mAddNewBugOnNextClick = false;
            Toast.makeText(this, getString(R.string.bug_creation_mode_disabled), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshBugs() {
        /* Reload all Bugs */
        new DownloadBugsTask(this, mBugOverlay, mMapView, mMapView.getBoundingBox()).execute();
    }

    /* The LocationManager to retrieve the current position */
    private LocationManager mLocMgr = null;

    /* The last location retrieved from the LocationListener */
    private Location mLastLocation;

    /* The main map */
    private MapView mMapView = null;

    /* Bugs displayed on the map */
    private ArrayList<Bug> mBugs;

    /* The Overlay for Bugs displayed on the map */
    private ItemizedIconOverlay<Bug> mBugOverlay;

    /* The Location Marker Overlay */
    private ItemizedIconOverlay<OverlayItem> mLocationOverlay;

    /* The next touch event on the map opens the add Bug Prompt */
    private boolean mAddNewBugOnNextClick = false;

    /* Used to save the Point where to create the new Bug */
    private static GeoPoint mNewBugLocation;

    /* Used to save the new Bugs platform */
    private static int mNewBugPlatform;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            /* Store the location for later use */
            mLastLocation = location;

            /* Center Map on GPS Position if activated */
            if (Settings.getFollowGps())
                mMapView.getController().setCenter(new GeoPoint(location));

            /* Update the Location Marker */
            mLocationOverlay.removeAllItems();
            OverlayItem i = new OverlayItem("", "", new GeoPoint(location));
            i.setMarker(Drawings.LocationMarker);
            mLocationOverlay.addItem(i);
            mMapView.invalidate();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private OnItemGestureListener<Bug> mOnitemGestureListener = new OnItemGestureListener<Bug>() {
        @Override
        public boolean onItemSingleTapUp(int position, Bug bug) {
            /* Open the selected Bug in the Bug Editor */
            Intent i = new Intent(OsmBugsActivity.this, BugEditorActivity.class);
            i.putExtra(BugEditorActivity.EXTRABUG, bug);

            startActivityForResult(i, REQUESTCODEBUGEDITORACTIVITY);

            return false;
        }

        @Override
        public boolean onItemLongPress(int i, Bug bug) {
            return false;
        }
    };
}
