package org.gittner.osmbugs;

import android.app.Application;

import org.androidannotations.annotations.EApplication;
import org.gittner.osmbugs.platforms.Platforms;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Settings;

import timber.log.Timber;

@EApplication
public class OsmBugsApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        /* Initialize Timber */
        Timber.plant(new Timber.DebugTree());

		/* Init Settings Class */
        Settings.init(this);

        /* Init the Drawings Class to load all Resources */
        Images.init(this);

        Platforms.init(this);
    }
}
