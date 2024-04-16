package org.gittner.osmbugs.osmose

import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.ui.ErrorMarker
import org.osmdroid.views.MapView

class OsmoseMarker(error: OsmoseError, map: MapView) : ErrorMarker<OsmoseError>(error, map) {
    init {
        icon = Images.GetDrawable(error.Type.Drawable)
    }
}