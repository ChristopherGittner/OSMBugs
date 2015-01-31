package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.BugOverlayItem;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.MyLocationOverlay;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

public class BugMapFragment extends Fragment {

    private static final String ZOOM_LEVEL = "ZOOM_LEVEL";
    private static final String CENTER_LAT = "CENTER_LAT";
    private static final String CENTER_LON = "CENTER_LON";

    public interface OnFragmentInteractionListener {
        public void onBugClicked(Bug bug);

        public void onAddNewBug(GeoPoint point);
    }

    private OnFragmentInteractionListener mListener;

    /* The next touch event on the map opens the add Bug Prompt */
    private boolean mAddNewBugOnNextClick = false;

    /* The main map */
    private MapView mMapView = null;

    /* The Overlay for Bugs displayed on the map */
    private ItemizedIconOverlay<BugOverlayItem> mKeeprightOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmoseOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mMapdustOverlay;
    private ItemizedIconOverlay<BugOverlayItem> mOsmNotesOverlay;

    /* The Location Marker Overlay */
    private MyLocationOverlay mLocationOverlay = null;

    public static BugMapFragment newInstance() {
        return new BugMapFragment();
    }

    public static BugMapFragment newInstance(int zoomLevel, GeoPoint point) {
        BugMapFragment instance = newInstance();

        Bundle args = new Bundle();
        args.putInt(ZOOM_LEVEL, zoomLevel);
        args.putDouble(CENTER_LAT, point.getLatitude());
        args.putDouble(CENTER_LON, point.getLongitude());

        instance.setArguments(args);

        return instance;
    }

    public BugMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bug_map, container, false);

        /* Create Bug Overlays */
        mKeeprightOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.keepright_zap),
                mKeeprightGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mOsmoseOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osmose_marker_b_0),
                mOsmoseGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mMapdustOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.mapdust_other),
                mMapdustGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mOsmNotesOverlay = new ItemizedIconOverlay<>(
                new ArrayList<BugOverlayItem>(),
                Images.get(R.drawable.osm_notes_open_bug),
                mOsmNotesGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        /* Setup Main MapView */
        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);

        /*
         * This adds an empty Overlay to retrieve the Touch Events. This is some sort of Hack, since
         * the OnTouchListener will fire only once if the Built in Zoom Controls are enabled
         */
        mMapView.getOverlays().add(new Overlay(getActivity()) {
            @Override
            protected void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouchEvent(MotionEvent event, MapView mapView) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && mAddNewBugOnNextClick) {
                    mListener.onAddNewBug((GeoPoint) mMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()));
                    mAddNewBugOnNextClick = false;
                    getActivity().invalidateOptionsMenu();
                    return false;
                }

                return super.onTouchEvent(event, mapView);
            }
        });

        final Bundle args = getArguments();
        if(args == null) {
			mMapView.getController().setZoom(Settings.getLastZoom());
			mMapView.getController().setCenter(Settings.getLastMapCenter());
        }
        else
        {
			mMapView.getController().setZoom(args.getInt(ZOOM_LEVEL));
			mMapView.getController().setCenter(
					new GeoPoint(
							args.getDouble(CENTER_LAT),
							args.getDouble(CENTER_LON)));
        }

        /* Add all Bugs to the Map */
        for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs()) {
            mKeeprightOverlay.addItem(new BugOverlayItem(bug));
        }
        for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs()) {
            mOsmoseOverlay.addItem(new BugOverlayItem(bug));
        }
        for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs()) {
            mMapdustOverlay.addItem(new BugOverlayItem(bug));
        }
        for (OsmNote bug : BugDatabase.getInstance().getOpenstreetmapNotes()) {
            mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
        }

        return v;
    }

	@Override
    public void onResume() {
        super.onResume();

        /* Register a DatabaseWatcher for update notification */
        BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.bug_map, menu);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private void menuFollowGpsClicked() {
        Settings.setFollowGps(!Settings.getFollowGps());

		getActivity().invalidateOptionsMenu();

		setupLocationOverlay();
    }

    private void menuEnableGPSClicked() {
        Settings.setEnableGps(!Settings.getEnableGps());

		getActivity().invalidateOptionsMenu();

		setupLocationOverlay();
    }

    private void menuAddBugClicked() {
        /* Enable or Disable the Bug creation mode */
        mAddNewBugOnNextClick = !mAddNewBugOnNextClick;
        getActivity().invalidateOptionsMenu();
    }

	private void setupLocationOverlay()
	{
		if(mLocationOverlay == null)
		{
			mLocationOverlay = new MyLocationOverlay(getActivity(), mMapView, mFollowModeListener);
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

	private MyLocationOverlay.FollowModeListener mFollowModeListener = new MyLocationOverlay.FollowModeListener()
	{
		@Override
		public void onFollowingStopped()
		{
			Settings.setFollowGps(false);
			getActivity().invalidateOptionsMenu();
		}
	};

	public BoundingBoxE6 getBBox() {
        return mMapView.getBoundingBox();
    }

    private final ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem> mKeeprightGestureListener = new ItemizedIconOverlay.OnItemGestureListener<BugOverlayItem>() {
        @Override
        public boolean onItemSingleTapUp(int position, BugOverlayItem bugItem) {
            mListener.onBugClicked(bugItem.getBug());
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
            mListener.onBugClicked(bugItem.getBug());
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
            mListener.onBugClicked(bugItem.getBug());
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
            mListener.onBugClicked(bugItem.getBug());
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
                    for (OsmNote bug : BugDatabase.getInstance().getOpenstreetmapNotes()) {
                        mOsmNotesOverlay.addItem(new BugOverlayItem(bug));
                    }
                    break;
            }
            mMapView.invalidate();
        }
    };
}
