package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;

public abstract class BugEditActivity extends ActionBarActivity
{
    public static final int RESULT_SAVED_KEEPRIGHT = 1;
    public static final int RESULT_SAVED_OSMOSE = 2;
    public static final int RESULT_SAVED_MAPDUST = 3;
    public static final int RESULT_SAVED_OSM_NOTES = 4;

    /* Intent Extras Descriptions */
    public static final String EXTRA_BUG = "EXTRA_BUG";
}
