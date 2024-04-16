package org.gittner.osmbugs.osmose

import android.view.View
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.OsmoseMarkerBinding
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.ui.ErrorInfoWindow
import org.osmdroid.views.MapView

class OsmoseInfoWindow(map: MapView) : ErrorInfoWindow(R.layout.osmose_marker, map) {
    private val mBinding: OsmoseMarkerBinding = OsmoseMarkerBinding.bind(view)

    override fun onOpen(item: Any?) {
        super.onOpen(item)

        val error = (item as OsmoseMarker).mError

        mBinding.apply {
            txtvTitle.text = error.Title
            txtvSubTitle.text = error.SubTitle

            imgIcon.setImageDrawable(Images.GetDrawable(error.Type.Drawable))

            if (error.Elements.size == 0) {
                lstvElements.visibility = View.GONE
            }
            lstvElements.adapter = OsmoseElementAdapter(mMapView.context, error.Elements)
        }
    }

    override fun onClose() {

    }
}