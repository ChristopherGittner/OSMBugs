package org.gittner.osmbugs.platforms;

import org.androidannotations.annotations.EBean;
import org.gittner.osmbugs.loader.Loader;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.util.HashMap;

@EBean(scope = EBean.Scope.Singleton)
public class AllPlatforms extends HashMap<String, Platform>
{
    public Platform byName(String name)
    {
        return get(name);
    }


    public void loadIfEnabled(final BoundingBoxE6 bBox)
    {
        Settings.setLastBBox(bBox);

        for (final Entry<String, Platform> entry : entrySet())
        {
            Platform platform = entry.getValue();

            if (platform.isEnabled())
            {
                platform.getLoader().getQueue().add(bBox);
            }
        }
    }


    public void reloadIfEnabled()
    {
        loadIfEnabled(Settings.getLastBBox());
    }


    public void addAll(final Platform[] platforms)
    {
        for (Platform platform : platforms)
        {
            put(platform.getName(), platform);
        }
    }


    public int getLoaderState()
    {
        for (final Entry<String, Platform> entry : entrySet())
        {
            Platform platform = entry.getValue();

            if (platform.getLoader().getState() == Loader.LOADING)
            {
                return Loader.LOADING;
            }
        }

        return Loader.STOPPED;
    }
}
