package org.gittner.osmbugs.statics;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.HashMap;

public class TileSources extends HashMap<Integer, ITileSource>
{
    private static final int MAPNIK = 1;
    private static final int PUBLIC_TRANSPORT = 2;
    private static final int FIETS_NL = 5;
    private static final int BASE_NL = 6;
    private static final int ROAD_NL = 7;
    private static final int HIKE_BIKE = 8;
    private static final int OPEN_SEA = 9;
    private static final int OPEN_TOPO = 15;

    private static final TileSources instance = new TileSources();


    private TileSources()
    {
        put(MAPNIK, TileSourceFactory.MAPNIK);
        put(PUBLIC_TRANSPORT, TileSourceFactory.PUBLIC_TRANSPORT);
        put(FIETS_NL, TileSourceFactory.FIETS_OVERLAY_NL);
        put(BASE_NL, TileSourceFactory.BASE_OVERLAY_NL);
        put(ROAD_NL, TileSourceFactory.ROADS_OVERLAY_NL);
        put(HIKE_BIKE, TileSourceFactory.HIKEBIKEMAP);
        put(OPEN_SEA, TileSourceFactory.OPEN_SEAMAP);
        put(OPEN_TOPO, TileSourceFactory.OpenTopo);
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
