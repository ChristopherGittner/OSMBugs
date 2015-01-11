package org.gittner.osmbugs.statics;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.gittner.osmbugs.R;

/* Static class to hold all Drawings */
public class Drawings {

    private static Context mContext;

    public static Drawable MenuAddBug;
    public static Drawable MenuAddBugRed;

    public static Drawable KeeprightDrawableIgnored;
    public static Drawable KeeprightDrawableClosed;
    public static Drawable KeeprightDrawableDefault;

    public static Drawable OsmoseMarkerB0;

    public static Drawable MapdustClosed;
    public static Drawable MapdustIgnored;
    public static Drawable MapdustWrongTurn;
    public static Drawable MapdustBadRouting;
    public static Drawable MapdustOnewayRoad;
    public static Drawable MapdustBlockedStreet;
    public static Drawable MapdustMissingStreet;
    public static Drawable MapdustRoundaboutIssue;
    public static Drawable MapdustMissingSpeedInfo;
    public static Drawable MapdustOther;

    public static Drawable OpenstreetmapNotesOpen;
    public static Drawable OpenstreetmapNotesClosed;

    public static void init(Context context) {

        mContext = context;

        MenuAddBug = context.getResources().getDrawable(R.drawable.ic_menu_add_bug);
        MenuAddBugRed = context.getResources().getDrawable(R.drawable.ic_menu_add_bug_red);

        KeeprightDrawableIgnored = context.getResources().getDrawable(R.drawable.zapdevil);
        KeeprightDrawableClosed = context.getResources().getDrawable(R.drawable.zapangel);
        KeeprightDrawableDefault = context.getResources().getDrawable(R.drawable.zap);

        OsmoseMarkerB0 = context.getResources().getDrawable(R.drawable.marker_b_0);

        MapdustClosed = context.getResources().getDrawable(R.drawable.bug_green);
        MapdustIgnored = context.getResources().getDrawable(R.drawable.bug_grey);
        MapdustWrongTurn = context.getResources().getDrawable(R.drawable.wrong_turn);
        MapdustBadRouting = context.getResources().getDrawable(R.drawable.bad_routing);
        MapdustOnewayRoad = context.getResources().getDrawable(R.drawable.oneway_road);
        MapdustBlockedStreet = context.getResources().getDrawable(R.drawable.blocked_street);
        MapdustMissingStreet = context.getResources().getDrawable(R.drawable.missing_street);
        MapdustRoundaboutIssue = context.getResources().getDrawable(R.drawable.roundabout_issue);
        MapdustMissingSpeedInfo = context.getResources().getDrawable(R.drawable.missing_speed_info);
        MapdustOther = context.getResources().getDrawable(R.drawable.other);

        OpenstreetmapNotesOpen =
                context.getResources().getDrawable(R.drawable.openstreetmap_notes_open_bug);
        OpenstreetmapNotesClosed =
                context.getResources().getDrawable(R.drawable.openstreetmap_notes_closed_bug);
    }

    public static Drawable get(String name, Drawable defaultDrawable) {
        int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());

        try {
            return mContext.getResources().getDrawable(id);
        }
        catch (android.content.res.Resources.NotFoundException e)
        {
            return defaultDrawable;
        }
    }
}
