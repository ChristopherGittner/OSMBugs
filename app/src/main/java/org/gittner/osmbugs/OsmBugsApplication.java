package org.gittner.osmbugs;

import android.app.Application;

import org.androidannotations.annotations.EApplication;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.platforms.Platforms;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.config.Configuration;

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

        Apis.init(this);

        /* Initialize Osmdroid */
        /* Set the correct User Agent */
        Configuration.getInstance().setUserAgentValue(getPackageName());

        /* Set the Tile cache to an internal location that is available on all Devices */
        Configuration.getInstance().setOsmdroidTileCache(getFilesDir());

        /* Setup cache Sizes */
        long cacheSize = Settings.getCacheSizeMb();
        Configuration.getInstance().setTileFileSystemCacheMaxBytes((cacheSize + 20L)* 1024L * 1024L); // Remove tiles only, when above 20 MB of the max cache size
        Configuration.getInstance().setTileFileSystemCacheTrimBytes(Settings.getCacheSizeMb() * 1024 * 1024);

        /* Check for App Version updates */
        int versionCode = Settings.getLastVersionCode();
        while (versionCode < BuildConfig.VERSION_CODE) {
            ++versionCode;

            switch (versionCode) {
                case 28:
                    /* Available Tiles changed. So we reset to the default Tile */
                    Settings.setMapStyle(1);

                case 36:
                    /* Max cache size preference has been introduced */
                    Settings.setCacheSizeMb(Configuration.getInstance().getTileFileSystemCacheMaxBytes() / 1024L / 1024L);
            }

            Settings.setLastVersionCode(versionCode);
        }
    }
}
