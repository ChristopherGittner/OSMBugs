package org.gittner.osmbugs.statics

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.gittner.osmbugs.MAP_STYLES
import org.gittner.osmbugs.MapStyles
import org.gittner.osmbugs.R
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.osmose.OsmoseError
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import java.util.*
import kotlin.math.max

class Settings(private val mContext: Context) {

    companion object {
        private lateinit var Instance: Settings

        fun getInstance(): Settings {
            return Instance
        }

        fun init(context: Context) {
            Instance = Settings(context)
        }
    }

    private val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)

    val OsmNotes = OsmNotesSettings(mContext, mSharedPreferences)
    val Keepright = KeeprightSettings(mContext, mSharedPreferences)
    val Osmose = OsmoseSettings(mContext, mSharedPreferences)

    var LastVersionCode: Int
        get() {
            return mSharedPreferences.getInt(
                mContext.getString(R.string.pref_last_version_code),
                -1
            )
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putInt(mContext.getString(R.string.pref_last_version_code), value)
                .apply()
        }

    var CacheSizeMb: Long
        get() {
            val default = (Configuration.getInstance().tileFileSystemCacheMaxBytes / 1024L / 1024L)

            try {
                val s = mSharedPreferences.getString(mContext.getString(R.string.pref_cache_size), "")?: return default
                return s.toLong()
            } catch (e: Exception) {
                return default
            }
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putString(mContext.getString(R.string.pref_cache_size), value.toString())
                .apply()
        }

    var TileTTLOverride: Long
        get() {
            try {
                val s = mSharedPreferences.getString(
                    mContext.getString(R.string.pref_tile_cache_ttl_override),
                    "0"
                )?:return 0
                return max(0, s.toLong())
            } catch (e:Exception) {
                return 0
            }
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putString(mContext.getString(R.string.pref_tile_cache_ttl_override), max(value, 0).toString())
                .apply()
        }

    var MapStyle: MAP_STYLES?
        get() {
            return MapStyles.byId(mSharedPreferences.getString(mContext.getString(R.string.pref_map_style), "1")!!.toInt())
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putString(mContext.getString(R.string.pref_map_style), value!!.Id.toString())
                .apply()
        }

    var LastMapCenter: IGeoPoint
        get() {
            return GeoPoint(
                (mSharedPreferences.getLong(mContext.getString(R.string.pref_last_map_center_lat), 51000000).toDouble() / 1000000.0),
                (mSharedPreferences.getLong(mContext.getString(R.string.pref_last_map_center_lon), 8000000).toDouble() / 1000000.0)
            )
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putLong(mContext.getString(R.string.pref_last_map_center_lat), (value.latitude * 1000000).toLong())
                .putLong(mContext.getString(R.string.pref_last_map_center_lon), (value.longitude * 1000000).toLong())
                .apply()
        }

    var LastMapZoom: Int
        get() {
            return mSharedPreferences.getInt(mContext.getString(R.string.pref_last_map_zoom), 15)
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putInt(mContext.getString(R.string.pref_last_map_zoom), value)
                .apply()
        }

    var TutorialBugStateDone: Boolean
        get() {
            return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_tutorial_bug_state_done), false)
        }
        set(value) {
            mSharedPreferences
                .edit()
                .putBoolean(mContext.getString(R.string.pref_tutorial_bug_state_done), value)
                .apply()
        }

    fun isLanguageGerman(): Boolean {
        return Locale.getDefault().isO3Language == "deu"
    }

    class OsmNotesSettings(private val mContext: Context, private val mSharedPreferences: SharedPreferences) {
        var Enabled: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_openstreetmap_notes_enabled), true)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_openstreetmap_notes_enabled), value)
                    .apply()
            }

        var OAuth2: String
            get() {
                return mSharedPreferences.getString(mContext.getString(R.string.pref_openstreetmap_notes_oauth2), "")!!
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putString(mContext.getString(R.string.pref_openstreetmap_notes_oauth2), value)
                    .apply()
            }

        var ShowClosed: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_openstreetmap_notes_show_closed), false)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_openstreetmap_notes_show_closed), value)
                    .apply()
            }

        var ErrorLimit: Int
            get() {
                return mSharedPreferences.getString(mContext.getString(R.string.pref_openstreetmap_notes_note_limit), "200")!!.toInt()
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putString(mContext.getString(R.string.pref_openstreetmap_notes_note_limit), value.toString())
                    .apply()
            }
    }

    class KeeprightSettings(private val mContext: Context, private val mSharedPreferences: SharedPreferences) {
        var Enabled: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_keepright_enabled), false)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_keepright_enabled), value)
                    .apply()
            }

        /**
         * Retrieves the State (Enabled / Disabled) of the given Type
         * @param errorType The Error Type for which the State is retrieved
         * @return True when the Type is enabled
         */
        fun GetTypeEnabled(errorType: KeeprightError.ERROR_TYPE): Boolean {
            return mSharedPreferences.getBoolean(mContext.getString(errorType.PreferenceId), errorType.DefaultEnabled)
        }

        /**
         * Sets the State of the given Error Type
         * @param errorType The Error Type for which the State will be set
         * @param enabled Wether or not this Error Type is enabled
         */
        fun SetTypeEnabled(errorType: KeeprightError.ERROR_TYPE, enabled: Boolean) {
            mSharedPreferences.edit().putBoolean(mContext.getString(errorType.PreferenceId), enabled).apply()
        }

        var ShowIgnored: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_keepright_enabled_show_ign), false)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_keepright_enabled_show_ign), value)
                    .apply()
            }

        var ShowTmpIgnored: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_keepright_enabled_show_tmp_ign), false)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_keepright_enabled_show_tmp_ign), value)
                    .apply()
            }
    }

    class OsmoseSettings(private val mContext: Context, private val mSharedPreferences: SharedPreferences) {
        var Enabled: Boolean
            get() {
                return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_osmose_enabled), false)
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putBoolean(mContext.getString(R.string.pref_osmose_enabled), value)
                    .apply()
            }

        var ErrorLevel: Int
            get() {
                return mSharedPreferences.getString(mContext.getString(R.string.pref_osmose_error_level), "1")!!.toInt()
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putString(mContext.getString(R.string.pref_osmose_enabled), value.toString())
                    .apply()
            }

        /**
         * Retrieves the State (Enabled / Disabled) of the given Type
         * @param errorType The Error Type for which the State is retrieved
         * @return True when the Type is enabled
         */
        fun GetTypeEnabled(errorType: OsmoseError.ERROR_TYPE): Boolean {
            return mSharedPreferences.getBoolean("pref_osmose_type_enabled_${errorType.Item}", true)
        }

        /**
         * Sets the State of the given Error Type
         * @param errorType The Error Type for which the State will be set
         * @param enabled Wether or not this Error Type is enabled
         */
        fun SetTypeEnabled(errorType: OsmoseError.ERROR_TYPE, enabled: Boolean) {
            mSharedPreferences.edit().putBoolean("pref_osmose_type_enabled_${errorType.Item}", enabled).apply()
        }

        var ErrorLimit: Int
            get() {
                return mSharedPreferences.getString(mContext.getString(R.string.pref_osmose_bug_limit), "200")!!.toInt()
            }
            set(value) {
                mSharedPreferences
                    .edit()
                    .putString(mContext.getString(R.string.pref_osmose_bug_limit), value.toString())
                    .apply()
            }
    }
}