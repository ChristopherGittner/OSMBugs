package org.gittner.osmbugs.keepright

import org.gittner.osmbugs.ui.ErrorMarker
import org.osmdroid.views.MapView

class KeeprightMarker(error: KeeprightError, map: MapView) : ErrorMarker<KeeprightError>(error, map) {
    init {
        icon = KeeprightError.GetZapIconFor(error.State, error.Type)
    }
}