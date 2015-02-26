package org.gittner.osmbugs.events;

import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.platforms.Platform;

public class BugsChangedEvent<T extends Bug>
{
    private final Platform<T> mPlatform;


    public BugsChangedEvent(Platform<T> platform)
    {
        mPlatform = platform;
    }


    public Platform<T> getPlatform()
    {
        return mPlatform;
    }
}
