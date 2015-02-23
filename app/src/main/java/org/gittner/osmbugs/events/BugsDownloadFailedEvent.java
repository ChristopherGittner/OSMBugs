package org.gittner.osmbugs.events;

public class BugsDownloadFailedEvent
{
    final int mPlatform;


    public BugsDownloadFailedEvent(int platform)
    {
        mPlatform = platform;
    }


    public int getPlatform()
    {
        return mPlatform;
    }
}
