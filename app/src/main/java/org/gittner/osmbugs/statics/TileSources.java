package org.gittner.osmbugs.statics;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.HashMap;

public class TileSources extends HashMap<Integer, ITileSource>
{
    public static final int MAPNIK = 1;
    public static final int PUBLIC_TRANSPORT = 2;
    public static final int CLOUDMATE_STANDARD = 3;
    public static final int CLOUDMATE_SMALL = 4;
    public static final int FIETS_NL = 5;
    public static final int BASE_NL = 6;
    public static final int ROAD_NL = 7;
    public static final int HIKE_BIKE = 8;
    public static final int OPEN_SEA = 9;
    public static final int UGS_TOP = 10;
    public static final int UGS_SAT = 11;
    public static final int CHART_BUNDLE_WAC = 12;
    public static final int CHART_BUNDLE_ENRH = 13;
    public static final int CHART_BUNDLE_ENRL = 14;
    public static final int OPEN_TOPO = 15;

    private static final TileSources instance = new TileSources();


    private TileSources()
    {
        put(MAPNIK, TileSourceFactory.MAPNIK);
        put(PUBLIC_TRANSPORT, TileSourceFactory.PUBLIC_TRANSPORT);
        put(CLOUDMATE_STANDARD, TileSourceFactory.CLOUDMADESTANDARDTILES);
        put(CLOUDMATE_SMALL, TileSourceFactory.CLOUDMADESMALLTILES);
        put(FIETS_NL, TileSourceFactory.FIETS_OVERLAY_NL);
        put(BASE_NL, TileSourceFactory.BASE_OVERLAY_NL);
        put(ROAD_NL, TileSourceFactory.ROADS_OVERLAY_NL);
        put(HIKE_BIKE, TileSourceFactory.HIKEBIKEMAP);
        put(OPEN_SEA, TileSourceFactory.OPEN_SEAMAP);
        put(UGS_TOP, TileSourceFactory.USGS_TOPO);
        put(UGS_SAT, TileSourceFactory.USGS_SAT);
        put(CHART_BUNDLE_WAC, TileSourceFactory.ChartbundleWAC);
        put(CHART_BUNDLE_ENRH, TileSourceFactory.ChartbundleENRH);
        put(CHART_BUNDLE_ENRL, TileSourceFactory.ChartbundleENRL);
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
