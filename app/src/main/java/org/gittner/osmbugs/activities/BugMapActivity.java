package org.gittner.osmbugs.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OnActivityResult.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.Helpers.IntentHelper;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.BugOverlayItem;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.MyLocationOverlay;
import org.gittner.osmbugs.common.RotatingIconButtonFloat;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Settings;
import org.gittner.osmbugs.statics.TileSources;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

@EActivity(R.layout.activity_bug_map)
@OptionsMenu(R.menu.bug_map)
public class BugMapActivity extends ActionBarActivity
{
    private static final String TAG = "OsmBugsActivity";
    /* Request Codes for activities */
    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;
    private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mBugGestureListener
            = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>()
    {
        @Override
        public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem)
        {
            Intent bugEditorIntent = new Intent(BugMapActivity.this, bugItem.getBug().getEditorClass());
            bugEditorIntent.putExtra(BugEditActivityConstants.EXTRA_BUG, bugItem.getBug());
            startActivityForResult(bugEditorIntent, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
            return false;
        }


        @Override
        public boolean onItemLongPress(int i, BugOverlayItem bugItem)
        {
            return false;
        }
    };
    private static final int REQUEST_CODE_SETTINGS_ACTIVITY = 2;
    private static final int REQUEST_CODE_BUG_LIST_ACTIVITY = 3;
    private static final int REQUEST_CODE_ADD_MAPDUST_BUG_ACTIVITY = 4;
    private static final int REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY = 5;
    /* Dialog Ids */
    private static final int DIALOG_NEW_BUG = 1;
    private static GeoPoint mNewBugLocation;
    private final MyLocationOverlay.FollowModeListener mFollowModeListener = new MyLocationOverlay.FollowModeListener()
    {
        @Override
        public void onFollowingStopped()
        {
            Settings.setFollowGps(false);
            invalidateOptionsMenu();
        }
    };
    /* Listener for Database Updates */
    private final BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher()
    {
        @Override
        public void onDatabaseUpdated(int platform)
        {
            switch (platform)
            {
                case Globals.KEEPRIGHT:
                    mKeeprightOverlay.removeAllItems();
                    for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs())
                    {
                        mKeeprightOverlay.addItem(new BugOverlayItem(bug));
                    }
                    break;

                case Globals.OSMOSE:
                    mOsmoseOverlay.removeAllItems();
                    for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs())
                    {
                        mOsmoseOverlay.addItem(new BugOverlayItem(bug));
                    }
                    break;

                case Globals.MAPDUST:
                    mMapdustOverlay.removeAllItems();
                    for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs())
                    {
                        mMapdustOverlay.addItem(new BugOverlayItem(bug));
                    }
                    break;

                case Globals.OSM_NOTES:
                    mOsmNotesOverlay.removeAllItems();
                    for (OsmNote bug : BugDatabase.getInstance().getOsmNotes())
                    {
                        mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
                    }
                    break;
            }

            mMap.invalidate();

            updateRefreshBugsButton();
        }


        @Override
        public void onDownloadCancelled(int platform)
        {
            updateRefreshBugsButton();
        }


        @Override
        public void onDownloadError(int platform)
        {
            String text = "";
            switch (platform)
            {
                case Globals.KEEPRIGHT:
                    text = getString(R.string.toast_failed_download_keepright_bugs);
                    break;

                case Globals.OSMOSE:
                    text = getString(R.string.toast_failed_download_osmose_bugs);
                    break;

                case Globals.MAPDUST:
                    text = getString(R.string.toast_failed_download_mapdust_bugs);
                    break;

                case Globals.OSM_NOTES:
                    text = getString(R.string.toast_failed_download_osm_notes_bugs);
                    break;
            }

            Toast.makeText(BugMapActivity.this, text, Toast.LENGTH_LONG).show();

            updateRefreshBugsButton();
        }
    };
    @ViewById(R.id.mapview)
    MapView mMap;
    @ViewById(R.id.btnRefreshBugs)
    RotatingIconButtonFloat mRefreshBugs;
    @OptionsMenuItem(R.id.add_bug)
    MenuItem mMenuAddBug;
    @OptionsMenuItem(R.id.enable_gps)
    MenuItem mMenuEnableGps;
    @OptionsMenuItem(R.id.follow_gps)
    MenuItem mMenuFollowGps;
    @OptionsMenuItem(R.id.list)
    MenuItem mMenuList;
    /* The next touch event on the map opens the add Bug Prompt */
    private boolean mAddNewBugOnNextClick = false;
    /* The Overlay for Bugs displayed on the map */
    private ItemizedIconOverlay<BugOverlayItem> mKeeprightOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmoseOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mMapdustOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmNotesOverlay;
    /* The Location Marker Overlay */
    private MyLocationOverlay mLocationOverlay = null;


    @AfterViews
    void init()
    {
        /* Create Bug Overlays */
        mKeeprightOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.keepright_zap),
                mBugGestureListener,
                new DefaultResourceProxyImpl(this));

        mOsmoseOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osmose_marker_b_0),
                mBugGestureListener,
                new DefaultResourceProxyImpl(this));

        mMapdustOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.mapdust_other),
                mBugGestureListener,
                new DefaultResourceProxyImpl(this));

        mOsmNotesOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osm_notes_open_bug),
                mBugGestureListener,
                new DefaultResourceProxyImpl(this));

        /* Setup Main MapView */
        mMap.setMultiTouchControls(true);
        mMap.setBuiltInZoomControls(true);
        /*
         * This adds an empty Overlay to retrieve the Touch Events. This is some sort of Hack, since
         * the OnTouchListener will fire only once if the Built in Zoom Controls are enabled
         */
        mMap.getOverlays().add(new Overlay(this)
        {
            @Override
            protected void draw(Canvas arg0, MapView arg1, boolean arg2)
            {
            }


            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouchEvent(MotionEvent event, MapView mapView)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN && mAddNewBugOnNextClick)
                {
                    mNewBugLocation = (GeoPoint) mMap.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                    //noinspection deprecation
                    showNewBugDialogDialog();
                    mAddNewBugOnNextClick = false;
                    invalidateOptionsMenu();
                    return false;
                }
                return super.onTouchEvent(event, mapView);
            }
        });
        mMap.getController().setZoom(Settings.getLastZoom());
        mMap.getController().setCenter(Settings.getLastMapCenter());
    }


    private void showNewBugDialogDialog()
    {
        final Spinner spnPlatform = new Spinner(this);

            /* Add the available Error Platforms to the Spinner */
        ArrayList<String> spinnerArray = new ArrayList<>();
        for (int i = 0;
             i != getResources().getStringArray(R.array.new_bug_platforms).length;
             ++i)
        {
            spinnerArray.add(getResources().getStringArray(R.array.new_bug_platforms)[i]);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spnPlatform.setAdapter(spinnerArrayAdapter);

        new MaterialDialog.Builder(this)
                .title(getString(R.string.platform))
                .cancelable(true)
                .customView(spnPlatform, false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        if (spnPlatform.getSelectedItemPosition() == 0)
                        {
                            Intent i = new Intent(BugMapActivity.this, AddOsmNoteActivity_.class);
                            i.putExtra(AddOsmNoteActivity.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                            i.putExtra(AddOsmNoteActivity.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());
                            startActivityForResult(i, REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY);
                        }
                        else if (spnPlatform.getSelectedItemPosition() == 1)
                        {
                            Intent i = new Intent(BugMapActivity.this, AddMapdustBugActivity_.class);
                            i.putExtra(AddMapdustBugActivity_.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                            i.putExtra(AddMapdustBugActivity_.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());
                            startActivityForResult(i, REQUEST_CODE_ADD_MAPDUST_BUG_ACTIVITY);
                        }
                    }
                })
                .show();
    }


    @Override
    public void onPause()
    {
        super.onPause();

        Settings.setLastMapCenter(mMap.getBoundingBox().getCenter());
        Settings.setLastZoom(mMap.getZoomLevel());

        BugDatabase.getInstance().removeDatabaseWatcher(mDatabaseWatcher);

        mMap.getOverlays().remove(mKeeprightOverlay);
        mMap.getOverlays().remove(mOsmoseOverlay);
        mMap.getOverlays().remove(mMapdustOverlay);
        mMap.getOverlays().remove(mOsmNotesOverlay);

        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
    }


    @Override
    public void onResume()
    {
        super.onResume();

        /* Register a DatabaseWatcher for update notification */
        BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);

        /* Reload all Bugs into the Map */
        mKeeprightOverlay.removeAllItems();
        mOsmoseOverlay.removeAllItems();
        mMapdustOverlay.removeAllItems();
        mOsmNotesOverlay.removeAllItems();

        for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs())
        {
            mKeeprightOverlay.addItem(new BugOverlayItem(bug));
        }
        for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs())
        {
            mOsmoseOverlay.addItem(new BugOverlayItem(bug));
        }
        for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs())
        {
            mMapdustOverlay.addItem(new BugOverlayItem(bug));
        }
        for (OsmNote bug : BugDatabase.getInstance().getOsmNotes())
        {
            mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
        }

		/* Display enabled Bug platforms */
        if (Settings.Keepright.isEnabled())
        {
            mMap.getOverlays().add(mKeeprightOverlay);
        }
        if (Settings.Osmose.isEnabled())
        {
            mMap.getOverlays().add(mOsmoseOverlay);
        }
        if (Settings.Mapdust.isEnabled())
        {
            mMap.getOverlays().add(mMapdustOverlay);
        }
        if (Settings.OsmNotes.isEnabled())
        {
            mMap.getOverlays().add(mOsmNotesOverlay);
        }

        mMap.setTileSource(TileSources.getInstance().getPreferredTileSource());

        setupLocationOverlay();

        updateRefreshBugsButton();

        mMap.invalidate();
    }


    private void setupLocationOverlay()
    {
        if (mLocationOverlay == null)
        {
            mLocationOverlay = new MyLocationOverlay(this, mMap, mFollowModeListener);
        }

        if (Settings.getEnableGps())
        {
            mLocationOverlay.enableMyLocation();
            if (!mMap.getOverlays().contains(mLocationOverlay))
            {
                mMap.getOverlays().add(mLocationOverlay);
            }
        }
        else
        {
            mLocationOverlay.disableMyLocation();
            mMap.getOverlays().remove(mLocationOverlay);
        }

        if (Settings.getFollowGps())
        {
            mLocationOverlay.enableFollowLocation();
        }
        else
        {
            mLocationOverlay.disableFollowLocation();
        }
    }


    private void updateRefreshBugsButton()
    {
        if (Settings.Keepright.isEnabled()
                || Settings.Osmose.isEnabled()
                || Settings.Mapdust.isEnabled()
                || Settings.OsmNotes.isEnabled())
        {
            mRefreshBugs.setVisibility(View.VISIBLE);
        }
        else
        {
            mRefreshBugs.setVisibility(View.GONE);
        }
        mRefreshBugs.setRotate(BugDatabase.getInstance().isDownloadRunning());
    }


    @OnActivityResult(REQUEST_CODE_BUG_EDITOR_ACTIVITY)
    void onBugEditorActivityResult(int resultCode)
    {
        switch (resultCode)
        {
            case BugEditActivityConstants.RESULT_SAVED_KEEPRIGHT:
                BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.KEEPRIGHT);
                break;

            case BugEditActivityConstants.RESULT_SAVED_OSMOSE:
                BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.OSMOSE);
                break;

            case BugEditActivityConstants.RESULT_SAVED_MAPDUST:
                BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.MAPDUST);
                break;

            case BugEditActivityConstants.RESULT_SAVED_OSM_NOTES:
                BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.OSM_NOTES);
                break;
        }
    }


    @OnActivityResult(REQUEST_CODE_BUG_LIST_ACTIVITY)
    void onListActivityResult(int resultCode, @Extra(value = BugListActivity.RESULT_EXTRA_BUG) Bug bug)
    {
        if (resultCode == BugListActivity.RESULT_BUG_MINI_MAP_CLICKED)
        {
            mMap.getController().setCenter(bug.getPoint());
            mMap.getController().setZoom(17);

            Settings.setFollowGps(false);

            invalidateOptionsMenu();
        }
    }


    @OnActivityResult(REQUEST_CODE_ADD_MAPDUST_BUG_ACTIVITY)
    void onAddMapdustBugActivityResult(int resultCode)
    {
        BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.MAPDUST);
    }


    @OnActivityResult(REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY)
    void onAddOsmNoteActivityResult(int resultCode)
    {
        BugDatabase.getInstance().load(mMap.getBoundingBox(), Globals.OSM_NOTES);
    }


    @Click(R.id.btnRefreshBugs)
    void refreshBugs()
    {
        BoundingBoxE6 bbox = mMap.getBoundingBox();

        if (Settings.Keepright.isEnabled())
        {
            BugDatabase.getInstance().load(bbox, Globals.KEEPRIGHT);
        }
        if (Settings.Osmose.isEnabled())
        {
            BugDatabase.getInstance().load(bbox, Globals.OSMOSE);
        }
        if (Settings.Mapdust.isEnabled())
        {
            BugDatabase.getInstance().load(bbox, Globals.MAPDUST);
        }
        if (Settings.OsmNotes.isEnabled())
        {
            BugDatabase.getInstance().load(bbox, Globals.OSM_NOTES);
        }

        updateRefreshBugsButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mMenuEnableGps.setChecked(Settings.getEnableGps());
        mMenuFollowGps.setChecked(Settings.getFollowGps());
        mMenuFollowGps.setEnabled(Settings.getEnableGps());

        if (mAddNewBugOnNextClick)
        {
            mMenuAddBug.setIcon(Images.get(R.drawable.ic_menu_add_bug_red));
        }
        else
        {
            mMenuAddBug.setIcon(Images.get(R.drawable.ic_menu_add_bug));
        }

        mMenuList.setVisible(
                Settings.Keepright.isEnabled()
                        || Settings.Osmose.isEnabled()
                        || Settings.Mapdust.isEnabled()
                        || Settings.OsmNotes.isEnabled());

        return true;
    }


    @OptionsItem(R.id.settings)
    void menuSettings()
    {
        Intent i = new Intent(this, SettingsActivity_.class);
        startActivityForResult(i, REQUEST_CODE_SETTINGS_ACTIVITY);
    }


    @OptionsItem(R.id.list)
    void menuListClicked()
    {
        Intent bugListIntent = new Intent(this, BugListActivity_.class);
        startActivityForResult(bugListIntent, REQUEST_CODE_BUG_LIST_ACTIVITY);
    }


    @OptionsItem(R.id.feedback)
    void onFeedbackClicked()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_mail)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_to_osmbugs));
        emailIntent.setType("plain/text");

        if (IntentHelper.intentHasReceivers(this, emailIntent))
        {
            try
            {
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
        new AlertDialog.Builder(this)
                .setTitle(R.string.sending_feedback_failed_title)
                .setMessage(R.string.sending_feedback_failed_message)
                .setCancelable(true)
                .create().show();
    }


    @OptionsItem(R.id.follow_gps)
    void menuFollowGpsClicked()
    {
        Settings.setFollowGps(!Settings.getFollowGps());

        invalidateOptionsMenu();

        setupLocationOverlay();
    }


    @OptionsItem(R.id.enable_gps)
    void menuEnableGPSClicked()
    {
        Settings.setEnableGps(!Settings.getEnableGps());

        invalidateOptionsMenu();

        setupLocationOverlay();
    }


    @OptionsItem(R.id.add_bug)
    void menuAddBugClicked()
    {
        mAddNewBugOnNextClick = !mAddNewBugOnNextClick;

        invalidateOptionsMenu();
    }
}
