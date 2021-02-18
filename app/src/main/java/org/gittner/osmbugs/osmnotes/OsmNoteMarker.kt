package org.gittner.osmbugs.osmnotes

import org.gittner.osmbugs.ui.ErrorMarker
import org.osmdroid.views.MapView

class OsmNoteMarker(error: OsmNote, map: MapView) : ErrorMarker<OsmNote>(error, map) {
    init {
        icon = if (error.State == OsmNote.STATE.OPEN) OsmNote.IcOpen else OsmNote.IcClosed
    }
}