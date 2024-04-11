package org.gittner.osmbugs

import android.annotation.SuppressLint
import android.app.Application
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.statics.Settings
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration
import timber.log.Timber

class App : Application() {
    private lateinit var mSettings: Settings

    @SuppressLint("ApplySharedPref")
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@App)
            // modules
            modules(DiModule)
        }

        Settings.init(this)
        mSettings = Settings.getInstance()

        // Initialize Logger
        Timber.plant(Timber.DebugTree())

        // Initialize static objects
        Images.init(this)

        /* Check for App Version updates */
        var versionCode: Int = mSettings.LastVersionCode
        if (versionCode != -1) {
            while (versionCode < BuildConfig.VERSION_CODE) {
                Timber.i("Upgrading from Version $versionCode to ${BuildConfig.VERSION_CODE}")
                ++versionCode
                when (versionCode) {
                    28 -> {
                        // Available Tiles changed. So we reset to the default Tile
                        mSettings.MapStyle = MAP_STYLES.MAPNIK

                        // Max cache size preference has been introduced
                        mSettings.CacheSizeMb = Configuration.getInstance().tileFileSystemCacheMaxBytes / 1024L / 1024L
                    }
                    36 -> mSettings.CacheSizeMb = Configuration.getInstance().tileFileSystemCacheMaxBytes / 1024L / 1024L
                    40 -> {
                        // Reprogrammed the whole app with Kotlin
                        // Reset all Settings
                        PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .clear()
                            .commit()
                    }
                    47 -> {
                        // Changed datatype of Room Classes
                        runBlocking {
                            GlobalScope.launch {
                                Dispatchers.IO
                                val database: AppDatabase by inject()
                                database.clearAllTables()
                            }
                        }
                    }
                }
                mSettings.LastVersionCode = versionCode
            }
        } else {
            // First start of the App --> Do nothing
            mSettings.LastVersionCode = versionCode
        }


        // Initialize Osmdroid
        // Set the correct User Agent
        Configuration.getInstance().userAgentValue = packageName

        // Set the Tile cache to an internal location that is available on all Devices
        Configuration.getInstance().osmdroidTileCache = filesDir

        // Setup cache Sizes
        val cacheSize = mSettings.CacheSizeMb
        Configuration.getInstance().tileFileSystemCacheMaxBytes = (cacheSize + 20L) * 1024L * 1024L // Remove tiles only, when above 20 MB of the max cache size
        Configuration.getInstance().tileFileSystemCacheTrimBytes = cacheSize * 1024 * 1024

        // Tile TTL
        val ttl = mSettings.TileTTLOverride
        if (ttl > 0) {
            Configuration.getInstance().expirationOverrideDuration = mSettings.TileTTLOverride * 1000
        }
    }
}