package org.gittner.osmbugs.events;

public class BugsDownloadCancelledEvent
{
    final int mPlatform;


    public BugsDownloadCancelledEvent(int platform)
    {
        mPlatform = platform;
    }


    public int getPlatform()
    {
        return mPlatform;
    }
}
