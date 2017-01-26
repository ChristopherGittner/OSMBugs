package org.gittner.osmbugs.platforms;

import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.loader.FixedSizeLoaderQueue;
import org.gittner.osmbugs.loader.Loader;
import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;

public abstract class Platform<TBug extends Bug>
{
    private final ArrayList<TBug> mBugs = new ArrayList<>();

    private final Loader<TBug> mLoader = new Loader<>(new FixedSizeLoaderQueue<BoundingBox>(1), this);


    public abstract String getName();


    public abstract boolean isEnabled();


    public abstract BugApi<TBug> getApi();


    public ArrayList<TBug> getBugs()
    {
        return mBugs;
    }


    public Loader<TBug> getLoader()
    {
        return mLoader;
    }
}
