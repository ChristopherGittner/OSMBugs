package org.gittner.osmbugs.common;

import android.content.Context;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MyLocationOverlay extends MyLocationNewOverlay
{
    private final FollowModeListener mListener;


    public MyLocationOverlay(Context context, MapView mapView, FollowModeListener listener)
    {
        super(context, mapView);
        mListener = listener;
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
}
