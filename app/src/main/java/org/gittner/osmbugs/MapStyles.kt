package org.gittner.osmbugs

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

object MapStyles {
    fun byId(id: Int): MAP_STYLES? {
        return MAP_STYLES.values().firstOrNull {
            it.Id == id
        }
    }
}

enum class MAP_STYLES(
    val Id: Int,
    val TileSource: OnlineTileSourceBase
) {
    MAPNIK(1, TileSourceFactory.MAPNIK),
    PUBLIC_TRANSPORT(2, TileSourceFactory.PUBLIC_TRANSPORT),
    FIETS_NL(3, TileSourceFactory.FIETS_OVERLAY_NL),
    BASE_NL(4, TileSourceFactory.BASE_OVERLAY_NL),
    ROAD_NL(5, TileSourceFactory.ROADS_OVERLAY_NL),
    OPEN_SEA(6, TileSourceFactory.OPEN_SEAMAP),
    OPEN_TOPO(7, TileSourceFactory.OpenTopo)
}
