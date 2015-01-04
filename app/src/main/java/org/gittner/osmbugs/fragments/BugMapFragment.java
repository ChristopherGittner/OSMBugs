package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Drawings;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class BugMapFragment extends Fragment {

    private static final String ZOOM_LEVEL = "ZOOMLEVEL";
    private static final String CENTER_LAT = "CENTER_LAT";
    private static final String CENTER_LON = "CENTER_LON";

    private OnFragmentInteractionListener mListener;

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
                new ArrayList<KeeprightBug>(),
                Drawings.KeeprightDrawable30,
                mKeeprightGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mOsmoseOverlay = new ItemizedIconOverlay<>(
                new ArrayList<OsmoseBug>(),
                Drawings.OsmoseMarkerB0,
                mOsmoseGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mMapdustOverlay = new ItemizedIconOverlay<>(
                new ArrayList<MapdustBug>(),
                Drawings.MapdustOther,
                mMapdustGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        mOsmNotesOverlay = new ItemizedIconOverlay<>(
                new ArrayList<OpenstreetmapNote>(),
                Drawings.OpenstreetmapNotesOpen,
                mOsmNotesGestureListener,
                new DefaultResourceProxyImpl(getActivity()));

        /* Setup Main MapView */
        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getOverlays().add(mKeeprightOverlay);
        mMapView.getOverlays().add(mOsmoseOverlay);
        mMapView.getOverlays().add(mMapdustOverlay);
        mMapView.getOverlays().add(mOsmNotesOverlay);

        mLocationOverlay = new MyLocationNewOverlay(getActivity(), mMapView);
        mMapView.getOverlays().add(mLocationOverlay);
        mLocationOverlay.enableMyLocation();
        if(Settings.getFollowGps())
            mLocationOverlay.enableFollowLocation();
        else
            mLocationOverlay.disableFollowLocation();

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
                    return false;
                }

                return super.onTouchEvent(event, mapView);
            }
        });

        final Bundle args = getArguments();
        if(args == null) {
            // Ugly Tweak since the mapview has to be layed out before setcenter works correctly
            //TODO: Remove as soon as this is fixed in osmdroid (4.3)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            /* Set the Initial Map Zoom */
                            mMapView.getController().setZoom(Settings.getLastZoom());

                            /* Set the Initial Center of the map */
                            mMapView.getController().setCenter(Settings.getLastMapCenter());
                        }
                    });
                }
            });
        }
        else
        {
            /* Set the Center and Zoomlevel */
            // Tweak since the mapview has to be layed out before setcenter works correctly
            //TODO: Remove as soon as this is fixed in osmdroid (4.3)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mMapView.getController().setZoom(args.getInt(ZOOM_LEVEL));
                    mMapView.getController().setCenter(new GeoPoint(args.getDouble(CENTER_LAT), args.getDouble(CENTER_LON)));
                }
            });
        }

        /* Add all Bugs to the Map */
        for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs()) {
            mKeeprightOverlay.addItem(bug);
        }
        for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs()) {
            mOsmoseOverlay.addItem(bug);
        }
        for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs()) {
            mMapdustOverlay.addItem(bug);
        }
        for (OpenstreetmapNote bug : BugDatabase.getInstance().getOpenstreetmapNotes()) {
            mOsmNotesOverlay.addItem(bug);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Start Listening to Location updates if we have a gps provider */
        LocationManager locMgr = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));

        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = locMgr.getBestProvider(locationCriteria, true);

        if (bestProvider == null)
            return;

        locMgr.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);

        /* Register a DatabaseWatcher for update notification */
        BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);

                /* Display or hide Bug platforms */
        mKeeprightOverlay.setEnabled(Settings.Keepright.isEnabled());
        mOsmoseOverlay.setEnabled(Settings.Osmose.isEnabled());
        mMapdustOverlay.setEnabled(Settings.Mapdust.isEnabled());
        mOsmNotesOverlay.setEnabled(Settings.OpenstreetmapNotes.isEnabled());

        mMapView.invalidate();
    }

    @Override
    public void onPause() {
        super.onPause();

        /* Save the last Center of the Map */
        Settings.setLastMapCenter(mMapView.getBoundingBox().getCenter());

        /* Save the last Map Zoom */
        Settings.setLastZoom(mMapView.getZoomLevel());

        /* Stop Listening to Location updates */
        ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).removeUpdates(mLocationListener);

        /* Stop listening to update notifications */
        BugDatabase.getInstance().removeDatabaseWatcher(mDatabaseWatcher);
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

        /* Set the checked state of the follow GPS Button according to the System Settings */
        menu.findItem(R.id.follow_gps).setChecked(Settings.getFollowGps());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                menuRefreshClicked();
                return true;

            case R.id.follow_gps:
                menuFollowGpsClicked();
                return true;

            case R.id.go_to_gps:
                menuGoToGPSClicked();
                return true;

            case R.id.add_bug:
                menuAddBugClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void menuRefreshClicked() {

    }

    private void menuFollowGpsClicked() {
        /* Toggle GPS Map Following */
        Settings.setFollowGps(!Settings.getFollowGps());

        if(Settings.getFollowGps())
            mLocationOverlay.enableFollowLocation();
        else
            mLocationOverlay.disableFollowLocation();

        getActivity().invalidateOptionsMenu();
    }

    private void menuGoToGPSClicked() {
        /* Center the GPS on the last known Location */
        if (mLastGpsLocation != null)
            mMapView.getController().setCenter(new GeoPoint(mLastGpsLocation));
    }

    private void menuAddBugClicked() {
        /* Enable or Disable the Bug creation mode */
        if (!mAddNewBugOnNextClick) {
            mAddNewBugOnNextClick = true;
            Toast.makeText(getActivity(), getString(R.string.bug_creation_mode_enabled), Toast.LENGTH_LONG).show();
        } else {
            mAddNewBugOnNextClick = false;
            Toast.makeText(getActivity(), getString(R.string.bug_creation_mode_disabled), Toast.LENGTH_LONG).show();
        }
    }

    public BoundingBoxE6 getBBox() {
        return mMapView.getBoundingBox();
    }

    public void centerMap(GeoPoint point) {
        mMapView.getController().setCenter(point);
        mMapView.invalidate();
    }

    public void setZoom(int zoomLevel) {
        mMapView.getController().setZoom(zoomLevel);
        mMapView.invalidate();
    }

    public interface OnFragmentInteractionListener {
        public void onBugClicked(Bug bug);

        public void onAddNewBug(GeoPoint point);
    }

    private ItemizedIconOverlay.OnItemGestureListener<KeeprightBug> mKeeprightGestureListener = new ItemizedIconOverlay.OnItemGestureListener<KeeprightBug>() {
        @Override
        public boolean onItemSingleTapUp(int position, KeeprightBug bug) {
            mListener.onBugClicked(bug);
            return false;
        }

        @Override
        public boolean onItemLongPress(int i, KeeprightBug bug) {
            return false;
        }
    };

    private ItemizedIconOverlay.OnItemGestureListener<OsmoseBug> mOsmoseGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OsmoseBug>() {
        @Override
        public boolean onItemSingleTapUp(int position, OsmoseBug bug) {
            mListener.onBugClicked(bug);
            return false;
        }

        @Override
        public boolean onItemLongPress(int i, OsmoseBug bug) {
            return false;
        }
    };

    private ItemizedIconOverlay.OnItemGestureListener<MapdustBug> mMapdustGestureListener = new ItemizedIconOverlay.OnItemGestureListener<MapdustBug>() {
        @Override
        public boolean onItemSingleTapUp(int position, MapdustBug bug) {
            mListener.onBugClicked(bug);
            return false;
        }

        @Override
        public boolean onItemLongPress(int i, MapdustBug bug) {
            return false;
        }
    };

    private ItemizedIconOverlay.OnItemGestureListener<OpenstreetmapNote> mOsmNotesGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OpenstreetmapNote>() {
        @Override
        public boolean onItemSingleTapUp(int position, OpenstreetmapNote bug) {
            mListener.onBugClicked(bug);
            return false;
        }

        @Override
        public boolean onItemLongPress(int i, OpenstreetmapNote bug) {
            return false;
        }
    };

    /* Listener for Database Updates */
    private BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher() {
        @Override
        public void onDatabaseUpdated(int platform) {

            switch(platform)
            {
                case Globals.KEEPRIGHT:
                    mKeeprightOverlay.removeAllItems();
                    for (KeeprightBug bug : BugDatabase.getInstance().getKeeprightBugs()) {
                        mKeeprightOverlay.addItem(bug);
                    }
                    break;

                case Globals.OSMOSE:
                    mOsmoseOverlay.removeAllItems();
                    for (OsmoseBug bug : BugDatabase.getInstance().getOsmoseBugs()) {
                        mOsmoseOverlay.addItem(bug);
                    }
                    break;

                case Globals.MAPDUST:
                    mMapdustOverlay.removeAllItems();
                    for (MapdustBug bug : BugDatabase.getInstance().getMapdustBugs()) {
                        mMapdustOverlay.addItem(bug);
                    }
                    break;

                case Globals.OPENSTREETMAPNOTES:
                    mOsmNotesOverlay.removeAllItems();
                    for (OpenstreetmapNote bug : BugDatabase.getInstance().getOpenstreetmapNotes()) {
                        mOsmNotesOverlay.addItem(bug);
                    }
                    break;
            }
            mMapView.invalidate();
        }

        @Override
        public void onDatabaseCleared() {
            mKeeprightOverlay.removeAllItems();
            mOsmoseOverlay.removeAllItems();
            mMapdustOverlay.removeAllItems();
            mOsmNotesOverlay.removeAllItems();

            mMapView.invalidate();
        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            /* Store the location for later use */
            mLastGpsLocation = location;
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

    /* The next touch event on the map opens the add Bug Prompt */
    private boolean mAddNewBugOnNextClick = false;

    /* The main map */
    private MapView mMapView = null;

    /* The Overlay for Bugs displayed on the map */
    private ItemizedIconOverlay<Bug> mBugOverlay;
    private ItemizedIconOverlay<KeeprightBug> mKeeprightOverlay;
    private ItemizedIconOverlay<OsmoseBug> mOsmoseOverlay;
    private ItemizedIconOverlay<MapdustBug> mMapdustOverlay;
    private ItemizedIconOverlay<OpenstreetmapNote> mOsmNotesOverlay;

    /* The Location Marker Overlay */
    private MyLocationNewOverlay mLocationOverlay;

    /* The last location retrieved from the LocationListener */
    private Location mLastGpsLocation;
}
