package org.gittner.osmbugs.statics;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

import java.util.HashMap;

public class TileSources extends HashMap<Integer, ITileSource>
{
    public static final int MAPNIK = 1;
    public static final int CYCLEMAP = 2;
    public static final int PUBLIC_TRANSPORT = 3;
    public static final int MAPQUEST_OSM = 4;
    public static final int MAPQUEST_AERIAL = 5;

    private static final TileSources instance = new TileSources();


    private TileSources()
    {
        put(MAPNIK, TileSourceFactory.MAPNIK);
        put(CYCLEMAP, TileSourceFactory.CYCLEMAP);
        put(PUBLIC_TRANSPORT, TileSourceFactory.PUBLIC_TRANSPORT);
        put(MAPQUEST_OSM, TileSourceFactory.MAPQUESTOSM);
        put(MAPQUEST_AERIAL, TileSourceFactory.MAPQUESTAERIAL);
    }


    public static TileSources getInstance()
    {
        return instance;
    }


    public ITileSource getPreferredTileSource()
    {
        return get(Settings.getMapStyle());
    }
}
