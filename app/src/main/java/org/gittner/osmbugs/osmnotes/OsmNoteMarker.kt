package org.gittner.osmbugs.osmnotes

import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.ui.ErrorMarker
import org.osmdroid.views.MapView

class OsmNoteMarker(error: OsmNote, map: MapView) : ErrorMarker<OsmNote>(error, map) {
    init {
        icon = if (error.State == OsmNote.STATE.OPEN) Images.GetDrawable(R.drawable.osm_note_open) else Images.GetDrawable(R.drawable.osm_note_closed)
    }
}