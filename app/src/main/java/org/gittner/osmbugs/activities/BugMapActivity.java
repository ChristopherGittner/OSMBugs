package org.gittner.osmbugs.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

public class BugMapActivity extends ActionBarActivity
{
    private static final String TAG = "OsmBugsActivity";

    /* Request Codes for activities */
    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;
    private static final int REQUEST_CODE_SETTINGS_ACTIVITY = 2;
	private static final int REQUEST_CODE_BUG_LIST_ACTIVITY = 3;

    /* Dialog Ids */
    private static final int DIALOG_NEW_BUG = 1;

	private static GeoPoint mNewBugLocation;

	/* The next touch event on the map opens the add Bug Prompt */
	private boolean mAddNewBugOnNextClick = false;

	/* The main map */
	private MapView mMapView = null;

	private RotatingIconButtonFloat mRefreshButton = null;

	/* The Overlay for Bugs displayed on the map */
	private ItemizedIconOverlay<BugOverlayItem> mKeeprightOverlay;
	private ItemizedIconOverlay<BugOverlayItem> mOsmoseOverlay;
	private ItemizedIconOverlay<BugOverlayItem> mMapdustOverlay;
	private ItemizedIconOverlay<BugOverlayItem> mOsmNotesOverlay;

	/* The Location Marker Overlay */
	private MyLocationOverlay mLocationOverlay = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bug_map);

		/* Create Bug Overlays */
		mKeeprightOverlay = new ItemizedIconOverlay<>(
				new ArrayList<BugOverlayItem>(),
				Images.get(R.drawable.keepright_zap),
				mKeeprightGestureListener,
				new DefaultResourceProxyImpl(this));

		mOsmoseOverlay = new ItemizedIconOverlay<>(
				new ArrayList<BugOverlayItem>(),
				Images.get(R.drawable.osmose_marker_b_0),
				mOsmoseGestureListener,
				new DefaultResourceProxyImpl(this));

		mMapdustOverlay = new ItemizedIconOverlay<>(
				new ArrayList<BugOverlayItem>(),
				Images.get(R.drawable.mapdust_other),
				mMapdustGestureListener,
				new DefaultResourceProxyImpl(this));

		mOsmNotesOverlay = new ItemizedIconOverlay<>(
				new ArrayList<BugOverlayItem>(),
				Images.get(R.drawable.osm_notes_open_bug),
				mOsmNotesGestureListener,
				new DefaultResourceProxyImpl(this));

        /* Setup Main MapView */
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setMultiTouchControls(true);
		mMapView.setBuiltInZoomControls(true);

        /*
         * This adds an empty Overlay to retrieve the Touch Events. This is some sort of Hack, since
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
											   mNewBugLocation = (GeoPoint) mMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());

											   //noinspection deprecation
											   showDialog(DIALOG_NEW_BUG);

											   mAddNewBugOnNextClick = false;
											   invalidateOptionsMenu();
											   return false;
										   }

										   return super.onTouchEvent(event, mapView);
									   }
								   });

		mMapView.getController().setZoom(Settings.getLastZoom());
		mMapView.getController().setCenter(Settings.getLastMapCenter());

		mRefreshButton = (RotatingIconButtonFloat) findViewById(R.id.btnRefresh);
		mRefreshButton.setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(final View v)
					{
						reloadAllBugs();
					}
				});
    }

	@Override
	public void onResume() {
		super.onResume();

        /* Register a DatabaseWatcher for update notification */
		BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);

        /* Reload all Bugs into the Map */
		mKeeprightOverlay.removeAllItems();
		mOsmoseOverlay.removeAllItems();
		mMapdustOverlay.removeAllItems();
		mOsmNotesOverlay.removeAllItems();

		for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs()) {
			mKeeprightOverlay.addItem(new BugOverlayItem(bug));
		}
		for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs()) {
			mOsmoseOverlay.addItem(new BugOverlayItem(bug));
		}
		for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs()) {
			mMapdustOverlay.addItem(new BugOverlayItem(bug));
		}
		for (OsmNote bug : BugDatabase.getInstance().getOsmNotes()) {
			mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
		}

		/* Display enabled Bug platforms */
		if(Settings.Keepright.isEnabled())
		{
			mMapView.getOverlays().add(mKeeprightOverlay);
		}
		if(Settings.Osmose.isEnabled())
		{
			mMapView.getOverlays().add(mOsmoseOverlay);
		}
		if(Settings.Mapdust.isEnabled())
		{
			mMapView.getOverlays().add(mMapdustOverlay);
		}
		if(Settings.OsmNotes.isEnabled())
		{
			mMapView.getOverlays().add(mOsmNotesOverlay);
		}

		setupLocationOverlay();

		updateRefreshButton();

		mMapView.invalidate();
	}

	@Override
	public void onPause() {
		super.onPause();

        /* Save the last Center of the Map */
		Settings.setLastMapCenter(mMapView.getBoundingBox().getCenter());

        /* Save the last Map Zoom */
		Settings.setLastZoom(mMapView.getZoomLevel());

        /* Stop listening to update notifications */
		BugDatabase.getInstance().removeDatabaseWatcher(mDatabaseWatcher);

		mMapView.getOverlays().remove(mKeeprightOverlay);
		mMapView.getOverlays().remove(mOsmoseOverlay);
		mMapView.getOverlays().remove(mMapdustOverlay);
		mMapView.getOverlays().remove(mOsmNotesOverlay);

		mLocationOverlay.disableFollowLocation();
		mLocationOverlay.disableMyLocation();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bug_map, menu);

		menu.findItem(R.id.enable_gps).setChecked(Settings.getEnableGps());
		menu.findItem(R.id.follow_gps).setChecked(Settings.getFollowGps());
		menu.findItem(R.id.follow_gps).setEnabled(Settings.getEnableGps());

		if(mAddNewBugOnNextClick)
		{
			menu.findItem(R.id.add_bug).setIcon(Images.get(R.drawable.ic_menu_add_bug_red));
		}
		else
		{
			menu.findItem(R.id.add_bug).setIcon(Images.get(R.drawable.ic_menu_add_bug));
		}

		menu.findItem(R.id.list).setVisible(
				Settings.Keepright.isEnabled()
				|| Settings.Osmose.isEnabled()
				|| Settings.Mapdust.isEnabled()
				|| Settings.OsmNotes.isEnabled());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                menuSettingsClicked();
                return true;

            case R.id.list:
                menuListClicked();
                return true;

            case R.id.feedback:
                onFeedbackClicked();
                return true;

			case R.id.follow_gps:
				menuFollowGpsClicked();
				return true;

			case R.id.enable_gps:
				menuEnableGPSClicked();
				return true;

			case R.id.add_bug:
				menuAddBugClicked();
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
        }
		else if(requestCode == REQUEST_CODE_BUG_LIST_ACTIVITY)
		{
			if(resultCode == BugListActivity.RESULT_BUG_MINI_MAP_CLICKED)
			{
				Bug bug = data.getParcelableExtra(BugListActivity.RESULT_EXTRA_BUG);

				if (resultCode == BugListActivity.RESULT_BUG_MINI_MAP_CLICKED)
				{
					mMapView.getController().setCenter(bug.getPoint());
					mMapView.getController().setZoom(17);
					Settings.setFollowGps(false);
					invalidateOptionsMenu();
				}
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
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
                                Intent i = new Intent(BugMapActivity.this, AddOpenstreetmapNoteActivity.class);

                                i.putExtra(AddMapdustBugActivity.EXTRA_LATITUDE, mNewBugLocation.getLatitude());
                                i.putExtra(AddMapdustBugActivity.EXTRA_LONGITUDE, mNewBugLocation.getLongitude());

                                startActivity(i);
                            } else if (spnPlatform.getSelectedItemPosition() == 1) {
                                Intent i = new Intent(BugMapActivity.this, AddMapdustBugActivity.class);

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
        Intent bugListIntent = new Intent(this, BugListActivity.class);
		startActivityForResult(bugListIntent, REQUEST_CODE_BUG_LIST_ACTIVITY);
    }

    private void reloadBugs(int platform)
    {
	    BugDatabase.getInstance().load(mMapView.getBoundingBox(), platform);
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

	private void menuFollowGpsClicked() {
		Settings.setFollowGps(!Settings.getFollowGps());

		invalidateOptionsMenu();

		setupLocationOverlay();
	}

	private void menuEnableGPSClicked() {
		Settings.setEnableGps(!Settings.getEnableGps());

		invalidateOptionsMenu();

		setupLocationOverlay();
	}

	private void menuAddBugClicked() {
        /* Enable or Disable the Bug creation mode */
		mAddNewBugOnNextClick = !mAddNewBugOnNextClick;
		invalidateOptionsMenu();
	}

	private void reloadAllBugs()
	{
		BoundingBoxE6 bbox = mMapView.getBoundingBox();

		if (Settings.Keepright.isEnabled())
			BugDatabase.getInstance().load(bbox, Globals.KEEPRIGHT);
		if (Settings.Osmose.isEnabled())
			BugDatabase.getInstance().load(bbox, Globals.OSMOSE);
		if (Settings.Mapdust.isEnabled())
			BugDatabase.getInstance().load(bbox, Globals.MAPDUST);
		if (Settings.OsmNotes.isEnabled())
			BugDatabase.getInstance().load(bbox, Globals.OSM_NOTES);

		updateRefreshButton();
	}

	private void updateRefreshButton()
	{
		if(Settings.Keepright.isEnabled()
				|| Settings.Osmose.isEnabled()
				|| Settings.Mapdust.isEnabled()
				|| Settings.OsmNotes.isEnabled())
		{
			mRefreshButton.setVisibility(View.VISIBLE);
		}
		else
		{
			mRefreshButton.setVisibility(View.GONE);
		}

		mRefreshButton.setRotate(BugDatabase.getInstance().isDownloadRunning());
	}

	private void setupLocationOverlay()
	{
		if(mLocationOverlay == null)
		{
			mLocationOverlay = new MyLocationOverlay(this, mMapView, mFollowModeListener);
		}

		if(Settings.getEnableGps())
		{
			mLocationOverlay.enableMyLocation();
			if(!mMapView.getOverlays().contains(mLocationOverlay))
			{
				mMapView.getOverlays().add(mLocationOverlay);
			}
		}
		else
		{
			mLocationOverlay.disableMyLocation();
			mMapView.getOverlays().remove(mLocationOverlay);
		}

		if(Settings.getFollowGps())
		{
			mLocationOverlay.enableFollowLocation();
		}
		else
		{
			mLocationOverlay.disableFollowLocation();
		}
	}

	private final MyLocationOverlay.FollowModeListener mFollowModeListener = new MyLocationOverlay.FollowModeListener()
	{
		@Override
		public void onFollowingStopped()
		{
			Settings.setFollowGps(false);
			invalidateOptionsMenu();
		}
	};

	public BoundingBoxE6 getBBox() {
		return mMapView.getBoundingBox();
	}

	private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mKeeprightGestureListener = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>() {
		@Override
		public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem) {
			Intent bugEditorIntent = new Intent(BugMapActivity.this, KeeprightEditActivity.class);
			bugEditorIntent.putExtra(BugEditActivity.EXTRA_BUG, bugItem.getBug());
			startActivityForResult(bugEditorIntent, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
			return false;
		}

		@Override
		public boolean onItemLongPress(int i, BugOverlayItem bugItem) {
			return false;
		}
	};

	private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mOsmoseGestureListener = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>() {
		@Override
		public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem) {
			Intent bugEditorIntent = new Intent(BugMapActivity.this, OsmoseEditActivity.class);
			bugEditorIntent.putExtra(BugEditActivity.EXTRA_BUG, bugItem.getBug());
			startActivityForResult(bugEditorIntent, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
			return false;
		}

		@Override
		public boolean onItemLongPress(int i, BugOverlayItem bugItem) {
			return false;
		}
	};

	private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mMapdustGestureListener = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>() {
		@Override
		public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem) {
			Intent bugEditorIntent = new Intent(BugMapActivity.this, MapdustEditActivity.class);
			bugEditorIntent.putExtra(BugEditActivity.EXTRA_BUG, bugItem.getBug());
			startActivityForResult(bugEditorIntent, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
			return false;
		}

		@Override
		public boolean onItemLongPress(int i, BugOverlayItem bugItem) {
			return false;
		}
	};

	private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mOsmNotesGestureListener = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>() {
		@Override
		public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem) {
			Intent bugEditorIntent = new Intent(BugMapActivity.this, OsmNoteEditActivity.class);
			bugEditorIntent.putExtra(BugEditActivity.EXTRA_BUG, bugItem.getBug());
			startActivityForResult(bugEditorIntent, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
			return false;
		}

		@Override
		public boolean onItemLongPress(int i, BugOverlayItem bugItem) {
			return false;
		}
	};

	/* Listener for Database Updates */
	private final BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher() {
		@Override
		public void onDatabaseUpdated(int platform) {

			switch(platform)
			{
				case Globals.KEEPRIGHT:
					mKeeprightOverlay.removeAllItems();
					for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs()) {
						mKeeprightOverlay.addItem(new BugOverlayItem(bug));
					}
					break;

				case Globals.OSMOSE:
					mOsmoseOverlay.removeAllItems();
					for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs()) {
						mOsmoseOverlay.addItem(new BugOverlayItem(bug));
					}
					break;

				case Globals.MAPDUST:
					mMapdustOverlay.removeAllItems();
					for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs()) {
						mMapdustOverlay.addItem(new BugOverlayItem(bug));
					}
					break;

				case Globals.OSM_NOTES:
					mOsmNotesOverlay.removeAllItems();
					for (OsmNote bug : BugDatabase.getInstance().getOsmNotes()) {
						mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
					}
					break;
			}
			mMapView.invalidate();

			updateRefreshButton();
		}

		@Override
		public void onDownloadCancelled(int platform)
		{
			updateRefreshButton();
		}

		@Override
		public void onDownloadError(int platform)
		{
			String text = "";

			switch (platform) {
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

			updateRefreshButton();
		}
	};
}
