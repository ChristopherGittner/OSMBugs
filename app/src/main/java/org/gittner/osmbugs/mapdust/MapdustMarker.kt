package org.gittner.osmbugs.mapdust

import org.gittner.osmbugs.ui.ErrorMarker
import org.koin.core.component.KoinComponent
import org.osmdroid.views.MapView

class MapdustMarker(error: MapdustError, map: MapView) : ErrorMarker<MapdustError>(error, map),
    KoinComponent {
    init {
        icon = MapdustError.GetIconFor(error.State, error.Type)
    }
}