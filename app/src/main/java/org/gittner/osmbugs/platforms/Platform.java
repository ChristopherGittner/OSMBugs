package org.gittner.osmbugs.platforms;

import android.content.Context;
import android.content.Intent;

import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.loader.FixedSizeLoaderQueue;
import org.gittner.osmbugs.loader.Loader;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public abstract class Platform<TBug extends Bug>
{
    private final ArrayList<TBug> mBugs = new ArrayList<>();

    private final Loader<TBug> mLoader = new Loader<>(new FixedSizeLoaderQueue<BoundingBoxE6>(1), this);


    public abstract String getName();

    public abstract boolean isEnabled();

    public abstract BugApi<TBug> getApi();

    public abstract Intent createEditor(Context context, TBug bug);


    public ArrayList<TBug> getBugs()
    {
        return mBugs;
    }


    public Loader<TBug> getLoader()
    {
        return mLoader;
    }
}
