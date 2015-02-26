package org.gittner.osmbugs.loader;

import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public abstract class Loader<TBug extends Bug>
{
    public static final int LOADING = 1;
    public static final int STOPPED = 2;
    public static final int FAILED = 3;
    public static final int CANCELLED = 4;

    private int mState = STOPPED;

    final Platform<TBug> mPlatform;
    final ArrayList<TBug> mBugs;


    public Loader(final Platform<TBug> platform)
    {
        mPlatform = platform;
        mBugs = platform.getBugs();

        EventBus.getDefault().postSticky(new StateChangedEvent<>(mPlatform, STOPPED));
    }


    protected void setState(int newState)
    {
        if (mState != newState)
        {
            mState = newState;

            EventBus.getDefault().post(new StateChangedEvent<>(mPlatform, newState));
        }
    }


    public int getState()
    {
        return mState;
    }


    public abstract void load(final BoundingBoxE6 bBox);


    public void reload()
    {
        load(Settings.getLastBBox());
    }


    public class StateChangedEvent<TPlatform extends Platform>
    {
        private final TPlatform mPlatform;
        private final int mState;


        public StateChangedEvent(TPlatform platform, final int state)
        {
            mPlatform = platform;
            mState = state;
        }


        public TPlatform getPlatform()
        {
            return mPlatform;
        }


        public int getState()
        {
            return mState;
        }
    }
}
