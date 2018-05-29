package org.gittner.osmbugs.common;

import android.location.Location;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MyLocationOverlay extends MyLocationNewOverlay
{
    private final FollowModeListener mListener;

    private LocationChangedCallback mLocationChangedCallbacks = null;


    public MyLocationOverlay(MapView mapView, FollowModeListener listener)
    {
        super(mapView);
        mListener = listener;
    }


    public void addLocationChangedCallback(LocationChangedCallback mCallback) {
        mLocationChangedCallbacks = mCallback;
    }


    @Override
    public void disableFollowLocation()
    {
        super.disableFollowLocation();

        mListener.onFollowingStopped();
    }


    public interface FollowModeListener
    {
        void onFollowingStopped();
    }


    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source)
    {
        super.onLocationChanged(location, source);

        if(mLocationChangedCallbacks != null) {
            mLocationChangedCallbacks.onLocationChanged(location, source);
        }
    }


    public interface LocationChangedCallback
    {
        void onLocationChanged(Location location, IMyLocationProvider source);
    }
}
