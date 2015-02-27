package org.gittner.osmbugs.common;

import android.os.Handler;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Calls the listener if the Map was scrolled and stopped for a time of INTERVAL * QUEUE_SIZE milliseconds
 */
public class MapScrollWatcher
{
    private static final long INTERVAL = 100;

    private static final int QUEUE_SIZE = 10;

    private final MapView mMap;

    private final Listener mListener;

    private boolean mScrolling = false;

    private boolean mCancelled = false;

    private Deque<IGeoPoint> mLastCentres = new LinkedList<>();


    public MapScrollWatcher(final MapView map, final Listener listener)
    {
        mMap = map;

        mListener = listener;

        for (int i = 0; i != QUEUE_SIZE; ++i)
        {
            mLastCentres.add(mMap.getMapCenter());
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!mCancelled)
                {
                    handler.postDelayed(this, INTERVAL);
                }

                update();
            }
        }, INTERVAL);
    }


    private void setScrolling(boolean scrolling)
    {
        if (mScrolling && !scrolling)
        {
            mListener.onScrolled();
        }

        mScrolling = scrolling;
    }


    private void update()
    {
        IGeoPoint currentCenter = mMap.getMapCenter();

        boolean differ = false;
        for (IGeoPoint center : mLastCentres)
        {
            if (center.getLatitudeE6() != currentCenter.getLatitudeE6()
                    || center.getLongitudeE6() != currentCenter.getLongitudeE6())
            {
                differ = true;
            }
        }

        setScrolling(differ);

        mLastCentres.removeFirst();
        mLastCentres.add(currentCenter);
    }


    public void cancel()
    {
        mCancelled = true;
    }


    public interface Listener
    {
        void onScrolled();
    }
}
