package org.gittner.osmbugs.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import java.util.Locale;

public class Settings
{
    /* The shared preferences used by all Methods */
    private static SharedPreferences mPrefs;


    public static void init(Context context)
    {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static boolean getEnableGps()
    {
        return mPrefs.getBoolean("pref_enable_gps", true);
    }


    public static void setEnableGps(boolean state)
    {
        mPrefs.edit().putBoolean("pref_enable_gps", state).apply();
    }


    public static boolean getFollowGps()
    {
        return mPrefs.getBoolean("pref_follow_gps", true);
    }


    public static void setFollowGps(boolean state)
    {
        mPrefs.edit().putBoolean("pref_follow_gps", state).apply();
    }


    public static boolean isLanguageGerman()
    {
        return Locale.getDefault().getISO3Language().equals("deu");
    }


    public static boolean getAutoLoad()
    {
        return mPrefs.getBoolean("pref_auto_load", true);
    }


    /* Location will be saved as String since Preferences can not store Double */
    public static GeoPoint getLastMapCenter()
    {
        return new GeoPoint(
                Double.parseDouble(mPrefs.getString("pref_last_map_center_lat", "51")),
                Double.parseDouble(mPrefs.getString("pref_last_map_center_lon", "8")));
    }


    public static void setLastMapCenter(IGeoPoint location)
    {
        mPrefs.edit().putString("pref_last_map_center_lat", String.valueOf(location.getLatitude())).apply();
        mPrefs.edit().putString("pref_last_map_center_lon", String.valueOf(location.getLongitude())).apply();
    }


    public static BoundingBoxE6 getLastBBox()
    {
        return new BoundingBoxE6(
                mPrefs.getInt("pref_last_bbox_lat_north", 0),
                mPrefs.getInt("pref_last_bbox_lon_east", 0),
                mPrefs.getInt("pref_last_bbox_lat_south", 0),
                mPrefs.getInt("pref_last_bbox_lon_west", 0));
    }


    public static void setLastBBox(BoundingBoxE6 bBox)
    {
        mPrefs.edit()
                .putInt("pref_last_bbox_lat_north", bBox.getLatNorthE6())
                .putInt("pref_last_bbox_lon_east", bBox.getLonEastE6())
                .putInt("pref_last_bbox_lat_south", bBox.getLatSouthE6())
                .putInt("pref_last_bbox_lon_west", bBox.getLonWestE6()).apply();
    }


    public static int getLastZoom()
    {
        return mPrefs.getInt("pref_last_zoom", 20);
    }


    public static void setLastZoom(int zoom)
    {
        mPrefs.edit().putInt("pref_last_zoom", zoom).apply();
    }


    public static boolean isDebugEnabled()
    {
        return mPrefs.getBoolean("pref_debug", false);
    }


    public static Integer getMapStyle()
    {
        return Integer.valueOf(mPrefs.getString("pref_map_style", "1"));
    }


    public static class Keepright
    {
        public static boolean isEnabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled", true);
        }


        public static boolean is20Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_20", false);
        }


        public static boolean is30Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_30", true);
        }


        public static boolean is40Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_40", true);
        }


        public static boolean is50Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_50", true);
        }


        public static boolean is60Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_60", false);
        }


        public static boolean is70Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_70", true);
        }


        public static boolean is90Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_90", true);
        }


        public static boolean is100Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_100", true);
        }


        public static boolean is110Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_110", true);
        }


        public static boolean is120Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_120", true);
        }


        public static boolean is130Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_130", true);
        }


        public static boolean is150Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_150", true);
        }


        public static boolean is160Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_160", true);
        }


        public static boolean is170Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_170", true);
        }


        public static boolean is180Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_180", true);
        }


        public static boolean is190Enabled()
        {
            return is191Enabled() | is192Enabled() | is193Enabled() | is194Enabled()
                    | is195Enabled() | is196Enabled() | is197Enabled() | is198Enabled();
        }


        public static boolean is191Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_191", true);
        }


        public static boolean is192Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_192", true);
        }


        public static boolean is193Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_193", true);
        }


        public static boolean is194Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_194", true);
        }


        public static boolean is195Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_195", true);
        }


        public static boolean is196Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_196", true);
        }


        public static boolean is197Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_197", true);
        }


        public static boolean is198Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_198", true);
        }


        public static boolean is200Enabled()
        {
            return is201Enabled() | is202Enabled() | is203Enabled() | is204Enabled()
                    | is205Enabled() | is206Enabled() | is207Enabled() | is208Enabled();
        }


        public static boolean is201Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_201", true);
        }


        public static boolean is202Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_202", true);
        }


        public static boolean is203Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_203", true);
        }


        public static boolean is204Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_204", true);
        }


        public static boolean is205Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_205", true);
        }


        public static boolean is206Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_206", true);
        }


        public static boolean is207Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_207", true);
        }


        public static boolean is208Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_208", true);
        }


        public static boolean is210Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_210", true);
        }


        public static boolean is220Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_220", true);
        }


        public static boolean is230Enabled()
        {
            return is231Enabled() | is232Enabled();
        }


        public static boolean is231Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_231", true);
        }


        public static boolean is232Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_232", true);
        }


        public static boolean is270Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_270", true);
        }


        public static boolean is280Enabled()
        {
            return is281Enabled() | is282Enabled() | is283Enabled() | is284Enabled()
                    | is285Enabled();
        }


        public static boolean is281Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_281", true);
        }


        public static boolean is282Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_282", true);
        }


        public static boolean is283Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_283", true);
        }


        public static boolean is284Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_284", true);
        }


        public static boolean is285Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_285", true);
        }


        public static boolean is290Enabled()
        {
            return is291Enabled() | is292Enabled() | is293Enabled() | is294Enabled();
        }


        public static boolean is291Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_291", true);
        }


        public static boolean is292Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_292", true);
        }


        public static boolean is293Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_293", true);
        }


        public static boolean is294Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_294", true);
        }


        public static boolean is300Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_300", false);
        }


        public static boolean is310Enabled()
        {
            return is311Enabled() | is312Enabled() | is313Enabled();
        }


        public static boolean is311Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_311", true);
        }


        public static boolean is312Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_312", true);
        }


        public static boolean is313Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_313", true);
        }


        public static boolean is320Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_320", true);
        }


        public static boolean is350Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_350", true);
        }


        public static boolean is360Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_360", false);
        }


        public static boolean is370Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_370", true);
        }


        public static boolean is380Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_380", true);
        }


        public static boolean is390Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_390", false);
        }


        public static boolean is400Enabled()
        {
            return is401Enabled() | is402Enabled();
        }


        public static boolean is401Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_401", true);
        }


        public static boolean is402Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_402", true);
        }


        public static boolean is410Enabled()
        {
            return is411Enabled() | is412Enabled() | is413Enabled();
        }


        public static boolean is411Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_411", true);
        }


        public static boolean is412Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_412", true);
        }


        public static boolean is413Enabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_413", true);
        }


        public static boolean isShowTempIgnoredEnabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_show_tmpign", true);
        }


        public static boolean isShowIgnoredEnabled()
        {
            return mPrefs.getBoolean("pref_keepright_enabled_show_ign", true);
        }
    }

    public static class Osmose
    {
        public static boolean isEnabled()
        {
            return mPrefs.getBoolean("pref_osmose_enabled", true);
        }


        public static int getBugLimit()
        {
            return Math.max(Math.min(Integer.parseInt(mPrefs.getString("pref_osmose_bug_limit", "100")), 500), 1);
        }


        public static int getBugsToDisplay()
        {
            return Integer.valueOf(mPrefs.getString("pref_osmose_bugs_to_display", "0"));
        }
    }

    public static class Mapdust
    {
        private static final String DEFAULT_USERNAME = "Anonymous";


        public static String getUsername()
        {
            String username = mPrefs.getString("pref_mapdust_username", DEFAULT_USERNAME);

            return !username.equals("") ? username : DEFAULT_USERNAME;
        }


        public static boolean isEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled", true);
        }


        public static boolean isShowOpenEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_open", true);
        }


        public static boolean isShowClosedEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_closed", true);
        }


        public static boolean isShowIgnoredEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_ignored", true);
        }


        public static boolean isWrongTurnEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_wrong_turn", true);
        }


        public static boolean isBadRoutingEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_bad_routing", false);
        }


        public static boolean isOnewayRoadEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_oneway_road", true);
        }


        public static boolean isBlockedStreetEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_blocked_street", true);
        }


        public static boolean isMissingStreetEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_missing_street", true);
        }


        public static boolean isRoundaboutIssueEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_roundabout_issue", true);
        }


        public static boolean isMissingSpeedInfoEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_missing_speed_info", true);
        }


        public static boolean isOtherEnabled()
        {
            return mPrefs.getBoolean("pref_mapdust_enabled_other", true);
        }
    }

    public static class OsmNotes
    {
        public static boolean isEnabled()
        {
            return mPrefs.getBoolean("pref_openstreetmap_notes_enabled", true);
        }


        public static boolean isShowOnlyOpenEnabled()
        {
            return mPrefs.getBoolean("pref_openstreetmap_notes_enabled_only_open", true);
        }


        public static int getBugLimit()
        {
            return Integer.parseInt(mPrefs.getString("pref_openstreetmap_notes_note_limit", "200"));
        }


        public static String getUsername()
        {
            return mPrefs.getString("pref_openstreetmap_notes_username", "");
        }


        public static String getPassword()
        {
            return mPrefs.getString("pref_openstreetmap_notes_password", "");
        }
    }
}
