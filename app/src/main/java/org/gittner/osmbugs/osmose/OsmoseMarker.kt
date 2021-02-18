package org.gittner.osmbugs.osmose

import org.gittner.osmbugs.ui.ErrorMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class OsmoseMarker(error: OsmoseError, map: MapView) : ErrorMarker<OsmoseError>(error, map) {
    init {
        icon = error.Type.Icon
    }
}