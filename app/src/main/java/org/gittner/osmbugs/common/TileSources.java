package org.gittner.osmbugs.common;

import org.gittner.osmbugs.statics.Settings;
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
    public static final int OPEN_RAILWAY_MAP_INFRASTRUCTURE = 6;
    public static final int OPEN_RAILWAY_MAP_MAXSPEED = 7;
    public static final int OPEN_RAILWAY_MAP_SIGNALS = 8;

    private static final TileSources instance = new TileSources();

    public static TileSources getInstance() {
        return instance;
    }

    private TileSources()
    {
        put(MAPNIK, TileSourceFactory.MAPNIK);
        put(CYCLEMAP, TileSourceFactory.CYCLEMAP);
        put(PUBLIC_TRANSPORT, TileSourceFactory.PUBLIC_TRANSPORT);
        put(MAPQUEST_OSM, TileSourceFactory.MAPQUESTOSM);
        put(MAPQUEST_AERIAL, TileSourceFactory.MAPQUESTAERIAL);
        put(OPEN_RAILWAY_MAP_INFRASTRUCTURE, new XYTileSource(
                "Open Railway Map Infrastructure",
                ResourceProxy.string.mapnik,
                0,
                19,
                256,
                ".png",
                new String[] {
                        "http://a.tiles.openrailwaymap.org/standard/",
                        "http://a.tiles.openrailwaymap.org/standard/",
                        "http://a.tiles.openrailwaymap.org/standard/" }));
        put(OPEN_RAILWAY_MAP_MAXSPEED, new XYTileSource(
                "Open Railway Map Maxspeed",
                ResourceProxy.string.mapnik,
                0,
                19,
                256,
                ".png",
                new String[] {
                        "http://a.tiles.openrailwaymap.org/maxspeed/",
                        "http://a.tiles.openrailwaymap.org/maxspeed/",
                        "http://a.tiles.openrailwaymap.org/maxspeed/" }));
        put(OPEN_RAILWAY_MAP_SIGNALS, new XYTileSource(
                "Open Railway Map Signals",
                ResourceProxy.string.mapnik,
                0,
                19,
                256,
                ".png",
                new String[] {
                        "http://a.tiles.openrailwaymap.org/signals/",
                        "http://a.tiles.openrailwaymap.org/signals/",
                        "http://a.tiles.openrailwaymap.org/signals/" }));
    }

    public ITileSource getPreferredTileSource()
    {
        return get(Settings.getMapStyle());
    }
}
