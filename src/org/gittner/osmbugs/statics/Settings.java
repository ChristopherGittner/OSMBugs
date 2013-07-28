
package org.gittner.osmbugs.statics;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Settings {

    public static final boolean DEBUG = false;

    private static SharedPreferences prefs_;

    public static void init(Context context) {
        prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences() {
        return prefs_;
    }

    public static boolean getCenterGps() {
        return prefs_.getBoolean("pref_center_gps", true);
    }

    public static void setCenterGps(boolean state) {
        prefs_.edit().putBoolean("pref_center_gps", state).commit();
    }

    public static boolean isLanguageGerman() {
        if (Locale.getDefault().getISO3Language().equals("deu"))
            return true;

        return false;
    }

    /* Location will be saved as String since Prefernces can not store Double */
    public static Location getLastKnownLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        location.setLatitude(Double.parseDouble(prefs_.getString("pref_last_location_lat", "0")));
        location.setLongitude(Double.parseDouble(prefs_.getString("pref_last_location_lon", "0")));

        return location;
    }

    public static void setLastKnownLocation(Location location) {
        prefs_.edit().putString("pref_last_location_lat", String.valueOf(location.getLatitude())).commit();
        prefs_.edit().putString("pref_last_location_lon", String.valueOf(location.getLongitude())).commit();
    }

    public static class Keepright {

        public static boolean isEnabled() {
            return prefs_.getBoolean("pref_keepright_enabled", true);
        }

        public static boolean is20Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_20", false);
        }

        public static boolean is30Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_30", true);
        }

        public static boolean is40Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_40", true);
        }

        public static boolean is50Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_50", true);
        }

        public static boolean is60Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_60", false);
        }

        public static boolean is70Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_70", true);
        }

        public static boolean is90Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_90", true);
        }

        public static boolean is100Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_100", true);
        }

        public static boolean is110Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_110", true);
        }

        public static boolean is120Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_120", true);
        }

        public static boolean is130Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_130", true);
        }

        public static boolean is150Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_150", true);
        }

        public static boolean is160Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_160", true);
        }

        public static boolean is170Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_170", true);
        }

        public static boolean is180Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_180", true);
        }

        public static boolean is190Enabled() {
            return is191Enabled() | is192Enabled() | is193Enabled() | is194Enabled()
                    | is195Enabled() | is196Enabled() | is197Enabled() | is198Enabled();
        }

        public static boolean is191Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_191", true);
        }

        public static boolean is192Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_192", true);
        }

        public static boolean is193Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_193", true);
        }

        public static boolean is194Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_194", true);
        }

        public static boolean is195Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_195", true);
        }

        public static boolean is196Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_196", true);
        }

        public static boolean is197Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_197", true);
        }

        public static boolean is198Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_198", true);
        }

        public static boolean is200Enabled() {
            return is201Enabled() | is202Enabled() | is203Enabled() | is204Enabled()
                    | is205Enabled() | is206Enabled() | is207Enabled() | is208Enabled();
        }

        public static boolean is201Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_201", true);
        }

        public static boolean is202Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_202", true);
        }

        public static boolean is203Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_203", true);
        }

        public static boolean is204Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_204", true);
        }

        public static boolean is205Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_205", true);
        }

        public static boolean is206Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_206", true);
        }

        public static boolean is207Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_207", true);
        }

        public static boolean is208Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_208", true);
        }

        public static boolean is210Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_210", true);
        }

        public static boolean is220Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_220", true);
        }

        public static boolean is230Enabled() {
            return is231Enabled() | is232Enabled();
        }

        public static boolean is231Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_231", true);
        }

        public static boolean is232Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_232", true);
        }

        public static boolean is270Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_270", true);
        }

        public static boolean is280Enabled() {
            return is281Enabled() | is282Enabled() | is283Enabled() | is284Enabled()
                    | is285Enabled();
        }

        public static boolean is281Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_281", true);
        }

        public static boolean is282Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_282", true);
        }

        public static boolean is283Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_283", true);
        }

        public static boolean is284Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_284", true);
        }

        public static boolean is285Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_285", true);
        }

        public static boolean is290Enabled() {
            return is291Enabled() | is292Enabled() | is293Enabled() | is294Enabled();
        }

        public static boolean is291Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_291", true);
        }

        public static boolean is292Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_292", true);
        }

        public static boolean is293Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_293", true);
        }

        public static boolean is294Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_294", true);
        }

        public static boolean is300Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_300", false);
        }

        public static boolean is310Enabled() {
            return is311Enabled() | is312Enabled() | is313Enabled();
        }

        public static boolean is311Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_311", true);
        }

        public static boolean is312Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_312", true);
        }

        public static boolean is313Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_313", true);
        }

        public static boolean is320Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_320", true);
        }

        public static boolean is350Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_350", true);
        }

        public static boolean is360Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_360", false);
        }

        public static boolean is370Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_370", true);
        }

        public static boolean is380Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_380", true);
        }

        public static boolean is390Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_390", false);
        }

        public static boolean is400Enabled() {
            return is401Enabled() | is402Enabled();
        }

        public static boolean is401Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_401", true);
        }

        public static boolean is402Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_402", true);
        }

        public static boolean is410Enabled() {
            return is411Enabled() | is412Enabled() | is413Enabled();
        }

        public static boolean is411Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_411", true);
        }

        public static boolean is412Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_412", true);
        }

        public static boolean is413Enabled() {
            return prefs_.getBoolean("pref_keepright_enabled_413", true);
        }

        public static boolean isShowTempIgnoredEnabled() {
            return prefs_.getBoolean("pref_keepright_enabled_show_tmpign", true);
        }

        public static boolean isShowIgnoredEnabled() {
            return prefs_.getBoolean("pref_keepright_enabled_show_ign", true);
        }
    }

    public static class Openstreetbugs {

        public static boolean isEnabled() {
            return prefs_.getBoolean("pref_openstreetbugs_enabled", true);
        }

        public static boolean isShowOnlyOpenEnabled() {
            return prefs_.getBoolean("pref_openstreetbugs_enabled_only_open", true);
        }

        public static int getBugLimit() {
            return Integer.parseInt(prefs_.getString("pref_openstreetbugs_bug_limit", "1000"));
        }

        public static String getUsername() {
            return prefs_.getString("pref_openstreetbugs_username", "John Doe");
        }
    }

    public static class Mapdust {

        public static String getApiKey() {
            return "ae58b0b4aa3f876265a4d5f29167b73c";
        }

        public static String getUsername() {
            return prefs_.getString("pref_mapdust_username", "Anonymous");
        }

        public static boolean isEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled", true);
        }

        public static boolean isShowOpenEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_open", true);
        }

        public static boolean isShowClosedEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_closed", true);
        }

        public static boolean isShowIgnoredEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_ignored", true);
        }

        public static boolean isWrongTurnEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_wrong_turn", true);
        }

        public static boolean isBadRoutingenabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_bad_routing", false);
        }

        public static boolean isOnewayRoadEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_oneway_road", true);
        }

        public static boolean isBlockedStreetEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_blocked_street", true);
        }

        public static boolean isMissingStreetEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_missing_street", true);
        }

        public static boolean isRoundaboutIssueEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_roundabout_issue", true);
        }

        public static boolean isMissingSpeedInfoEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_missing_speed_info", true);
        }

        public static boolean isOtherEnabled() {
            return prefs_.getBoolean("pref_mapdust_enabled_other", true);
        }
    }

    public static class OpenstreetmapNotes {

        public static boolean isEnabled() {
            return prefs_.getBoolean("pref_openstreetmap_notes_enabled", true);
        }

        public static boolean isShowOnlyOpenEnabled() {
            return prefs_.getBoolean("pref_openstreetmap_notes_enabled_only_open", true);
        }

        public static int getBugLimit() {
            return Integer.parseInt(prefs_.getString("pref_openstreetmap_notes_note_limit", "1000"));
        }

        public static String getUsername() {
            return prefs_.getString("pref_openstreetmap_notes_username", "");
        }

        public static String getPassword() {
            return prefs_.getString("pref_openstreetmap_notes_password", "");
        }
    }
}
