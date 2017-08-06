package org.gittner.osmbugs.statics;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.HashMap;

public class TileSources extends HashMap<Integer, ITileSource>
{
    public static final int MAPNIK = 1;
    public static final int PUBLIC_TRANSPORT = 3;

    private static final TileSources instance = new TileSources();


    private TileSources()
    {
        put(MAPNIK, TileSourceFactory.MAPNIK);
        put(PUBLIC_TRANSPORT, TileSourceFactory.PUBLIC_TRANSPORT);
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
