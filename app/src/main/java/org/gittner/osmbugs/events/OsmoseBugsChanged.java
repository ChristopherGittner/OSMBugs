package org.gittner.osmbugs.events;

import org.gittner.osmbugs.bugs.OsmoseBug;

import java.util.List;

public class OsmoseBugsChanged
{
    final List<OsmoseBug> mBugs;


    public OsmoseBugsChanged(List<OsmoseBug> platform)
    {
        mBugs = platform;
    }


    public List<OsmoseBug> getBugs()
    {
        return mBugs;
    }
}
