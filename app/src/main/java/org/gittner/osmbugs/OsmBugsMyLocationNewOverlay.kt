package org.gittner.osmbugs

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OsmBugsMyLocationNewOverlay(mapView: MapView, followCb: (Boolean) -> Unit) : MyLocationNewOverlay(mapView) {
    private val mFollowCb : (Boolean) -> Unit

    init {
        mFollowCb = followCb
    }
    override fun enableFollowLocation() {
        super.enableFollowLocation()

        mFollowCb(true)
    }

    override fun disableFollowLocation() {
        super.disableFollowLocation()

        mFollowCb(false)
    }
}