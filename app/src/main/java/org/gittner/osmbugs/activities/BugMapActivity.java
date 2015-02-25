package org.gittner.osmbugs.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
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
import org.gittner.osmbugs.events.BugsChangedEvents;
import org.gittner.osmbugs.events.BugsDownloadFailedEvent;
import org.gittner.osmbugs.events.BugsDownloadingStateChangedEvent;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Platforms;
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
public class BugMapActivity extends EventBusActionBarActivity
{
    private static final String TAG = "OsmBugsActivity";

    /* Request Codes for activities */
    private static final int REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY = 1;
    private static final int REQUEST_CODE_OSMOSE_EDIT_ACTIVITY = 2;
    private static final int REQUEST_CODE_MAPDUST_EDIT_ACTIVITY = 3;
    private static final int REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY = 4;
    private static final int REQUEST_CODE_SETTINGS_ACTIVITY = 5;
    private static final int REQUEST_CODE_BUG_LIST_ACTIVITY = 6;
    private static final int REQUEST_CODE_ADD_MAPDUST_BUG_ACTIVITY = 7;
    private static final int REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY = 8;

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

    @Bean
    BugDatabase mBugDatabase;

    /* The next touch event on the map opens the add Bug Prompt */
    private boolean mAddNewBugOnNextClick = false;

    /* The Overlay for Bugs displayed on the map */
    private ItemizedIconOverlay<BugOverlayItem> mKeeprightOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmoseOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mMapdustOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmNotesOverlay;

    /* The Location Marker Overlay */
    private MyLocationOverlay mLocationOverlay = null;

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


    @AfterViews
    void init()
    {
        /* Create Bug Overlays */
        mKeeprightOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.keepright_zap),
                new LaunchEditorListener(KeeprightEditActivity_.class, REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY),
                new DefaultResourceProxyImpl(this));

        mOsmoseOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osmose_marker_b_0),
                new LaunchEditorListener(KeeprightEditActivity_.class, REQUEST_CODE_OSMOSE_EDIT_ACTIVITY),
                new DefaultResourceProxyImpl(this));

        mMapdustOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.mapdust_other),
                new LaunchEditorListener(KeeprightEditActivity_.class, REQUEST_CODE_MAPDUST_EDIT_ACTIVITY),
                new DefaultResourceProxyImpl(this));

        mOsmNotesOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osm_notes_open_bug),
                new LaunchEditorListener(KeeprightEditActivity_.class, REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY),
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
        new MaterialDialog.Builder(this)
                .title(getString(R.string.platform))
                .cancelable(true)
                .items(R.array.new_bug_platforms)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(
                            MaterialDialog materialDialog,
                            View view,
                            int i,
                            CharSequence charSequence)
                    {
                        if (i == 0)
                        {
                            Intent addBugIntent = new Intent(BugMapActivity.this, AddOsmNoteActivity_.class);
                            addBugIntent.putExtra(AddOsmNoteActivity.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                            addBugIntent.putExtra(AddOsmNoteActivity.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());
                            startActivityForResult(addBugIntent, REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY);
                        }
                        else if (i == 1)
                        {
                            Intent addBugIntent = new Intent(BugMapActivity.this, AddMapdustBugActivity_.class);
                            addBugIntent.putExtra(AddMapdustBugActivity_.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                            addBugIntent.putExtra(AddMapdustBugActivity_.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());
                            startActivityForResult(addBugIntent, REQUEST_CODE_ADD_MAPDUST_BUG_ACTIVITY);
                        }
                    }
                })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .show();
    }


    @Override
    public void onPause()
    {
        super.onPause();

        Settings.setLastMapCenter(mMap.getBoundingBox().getCenter());
        Settings.setLastZoom(mMap.getZoomLevel());

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

        mMap.invalidate();

        /* Show the refresh Button only if at least one Platform is enabled */
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


    @OnActivityResult(REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY)
    void onKeeprightEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.KEEPRIGHT);
        }
    }


    @OnActivityResult(REQUEST_CODE_OSMOSE_EDIT_ACTIVITY)
    void onOsmoseEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.OSMOSE);
        }
    }


    @OnActivityResult(REQUEST_CODE_MAPDUST_EDIT_ACTIVITY)
    void onMapdustEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.MAPDUST);
        }
    }


    @OnActivityResult(REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY)
    void onOsmNoteEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.OSM_NOTES);
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
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.MAPDUST);
        }
    }


    @OnActivityResult(REQUEST_CODE_ADD_OSM_NOTE_BUG_ACTIVITY)
    void onAddOsmNoteActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.load(mMap.getBoundingBox(), Platforms.OSM_NOTES);
        }
    }


    @Click(R.id.btnRefreshBugs)
    void refreshBugs()
    {
        BoundingBoxE6 bBox = mMap.getBoundingBox();

        if (Settings.Keepright.isEnabled())
        {
            mBugDatabase.load(bBox, Platforms.KEEPRIGHT);
        }
        if (Settings.Osmose.isEnabled())
        {
            mBugDatabase.load(bBox, Platforms.OSMOSE);
        }
        if (Settings.Mapdust.isEnabled())
        {
            mBugDatabase.load(bBox, Platforms.MAPDUST);
        }
        if (Settings.OsmNotes.isEnabled())
        {
            mBugDatabase.load(bBox, Platforms.OSM_NOTES);
        }
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
        BugListActivity_.intent(this).startForResult(REQUEST_CODE_BUG_LIST_ACTIVITY);
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


    public void onEventMainThread(BugsChangedEvents.Keepright event)
    {
        mKeeprightOverlay.removeAllItems();

        for (KeeprightBug bug : event.getBugs())
        {
            mKeeprightOverlay.addItem(new BugOverlayItem(bug));
        }

        mMap.invalidate();
    }


    public void onEventMainThread(BugsChangedEvents.Osmose event)
    {
        mOsmoseOverlay.removeAllItems();

        for (OsmoseBug bug : event.getBugs())
        {
            mOsmoseOverlay.addItem(new BugOverlayItem(bug));
        }

        mMap.invalidate();
    }


    public void onEventMainThread(BugsChangedEvents.Mapdust event)
    {
        mMapdustOverlay.removeAllItems();

        for (MapdustBug bug : event.getBugs())
        {
            mMapdustOverlay.addItem(new BugOverlayItem(bug));
        }

        mMap.invalidate();
    }


    public void onEventMainThread(BugsChangedEvents.OsmNotes event)
    {
        mOsmNotesOverlay.removeAllItems();

        for (OsmNote bug : event.getBugs())
        {
            mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
        }

        mMap.invalidate();
    }


    public void onEventMainThread(BugsDownloadFailedEvent event)
    {
        String text = "";
        switch (event.getPlatform())
        {
            case Platforms.KEEPRIGHT:
                text = getString(R.string.toast_failed_download_keepright_bugs);
                break;

            case Platforms.OSMOSE:
                text = getString(R.string.toast_failed_download_osmose_bugs);
                break;

            case Platforms.MAPDUST:
                text = getString(R.string.toast_failed_download_mapdust_bugs);
                break;

            case Platforms.OSM_NOTES:
                text = getString(R.string.toast_failed_download_osm_notes_bugs);
                break;
        }

        Toast.makeText(BugMapActivity.this, text, Toast.LENGTH_LONG).show();
    }


    public void onEventMainThread(BugsDownloadingStateChangedEvent event)
    {
        mRefreshBugs.setRotate(event.getState());
    }


    private class LaunchEditorListener implements ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>
    {
        final Class<?> mEditorClass;
        final int mRequestCode;


        public LaunchEditorListener(Class<?> editorClass, int requestCode)
        {
            mEditorClass = editorClass;
            mRequestCode = requestCode;
        }


        @Override
        public boolean onItemSingleTapUp(int index, BugOverlayItem bugItem)
        {
            Intent bugEditorIntent = new Intent(BugMapActivity.this, mEditorClass);
            bugEditorIntent.putExtra(BugEditActivityConstants.EXTRA_BUG, bugItem.getBug());
            startActivityForResult(bugEditorIntent, mRequestCode);

            return true;
        }


        @Override
        public boolean onItemLongPress(int index, BugOverlayItem item)
        {
            return false;
        }
    }
}
