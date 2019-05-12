package org.gittner.osmbugs.api;

import android.content.Context;

public class Apis
{
    public static final KeeprightApi KEEPRIGHT = new KeeprightApi();
    public static final OsmoseApi OSMOSE = new OsmoseApi();
    public static final MapdustApi MAPDUST = new MapdustApi();
    public static final OsmNotesApi OSM_NOTES = new OsmNotesApi();

    public static void init(Context context)
    {
        KeeprightApi.init(context);
        OsmoseApi.init(context);
    }
}
