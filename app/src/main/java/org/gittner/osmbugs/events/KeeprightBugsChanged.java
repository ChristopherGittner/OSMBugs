package org.gittner.osmbugs.events;

import org.gittner.osmbugs.bugs.KeeprightBug;

import java.util.List;

public class KeeprightBugsChanged
{
    final List<KeeprightBug> mBugs;


    public KeeprightBugsChanged(List<KeeprightBug> platform)
    {
        mBugs = platform;
    }


    public List<KeeprightBug> getBugs()
    {
        return mBugs;
    }
}
