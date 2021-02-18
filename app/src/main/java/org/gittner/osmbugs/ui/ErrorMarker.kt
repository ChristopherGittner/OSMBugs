package org.gittner.osmbugs.ui

import org.gittner.osmbugs.Error
import org.gittner.osmbugs.osmnotes.OsmNote
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

open class ErrorMarker<T : Error>(error : T, mapView: MapView) : Marker(mapView) {
    open val mError = error

    init {
        position = GeoPoint(error.Point)

        setAnchor(.15f, .15f)
        setInfoWindowAnchor(.2f, .1f)
    }
}
