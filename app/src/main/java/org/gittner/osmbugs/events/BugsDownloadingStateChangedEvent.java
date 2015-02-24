package org.gittner.osmbugs.events;

public class BugsDownloadingStateChangedEvent
{
    final boolean mState;


    public BugsDownloadingStateChangedEvent(boolean state)
    {
        mState = state;
    }


    public boolean getState()
    {
        return mState;
    }
}
