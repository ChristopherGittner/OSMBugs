package org.gittner.osmbugs.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.gittner.osmbugs.OsmBugsMyLocationNewOverlay
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.MapFragmentBinding
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.keepright.KeeprightInfoWindow
import org.gittner.osmbugs.keepright.KeeprightMarker
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmnotes.OsmNoteInfoWindow
import org.gittner.osmbugs.osmnotes.OsmNoteMarker
import org.gittner.osmbugs.osmnotes.OsmNotesAddErrorDialog
import org.gittner.osmbugs.osmose.OsmoseError
import org.gittner.osmbugs.osmose.OsmoseInfoWindow
import org.gittner.osmbugs.osmose.OsmoseMarker
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.statics.Settings
import org.koin.android.ext.android.inject
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * Displays a Map with Buttons to reload the Errors, toggle the visible Layers and shows a progress bar when loading new Errors
 */
class MapFragment : Fragment() {
    private lateinit var mBinding: MapFragmentBinding

    private val mErrorViewModel: ErrorViewModel by inject()
    private val mSettings = Settings.getInstance()

    // These ArrayLists store all Visible Markers on the Map, to be able to remove them when the Markers are updated or the Layers are toggled
    private val mOsmNotes = ArrayList<OsmNoteMarker>()
    private val mKeeprightErrors = ArrayList<KeeprightMarker>()
    private val mOsmoseErrors = ArrayList<OsmoseMarker>()

    private var mBtnInitDone = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = MapFragmentBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)

        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_map, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayerToggle()

        setupViewModel()

        setupReload()

        setupAddError()

        setupFollowLocation()

        setupMap()

        // Hide all Buttons once to enable the correct SlideIn Animation.
        // If the Buttons have not been hidden once, they will not slide in, but only scale in
        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (!mBtnInitDone) {
                initialHide(mBinding.btnToggleOsmNotesLayer, mBinding.btnLayers)
                initialHide(mBinding.btnToggleKeeprightLayer, mBinding.btnLayers)
                initialHide(mBinding.btnToggleOsmoseLayer, mBinding.btnLayers)
                mBtnInitDone = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val style = mSettings.MapStyle
        if (style != null) {
            mBinding.map.setTileSource(style.TileSource)
        }

        mBinding.map.onResume()
    }

    override fun onPause() {
        super.onPause()

        mBinding.map.onPause()

        mSettings.LastMapCenter = mBinding.map.mapCenter
        mSettings.LastMapZoom = mBinding.map.zoomLevelDouble.toInt()
    }

    /**
     * Sets up everything related to the Toggle Layer Buttons
     */
    private fun setupLayerToggle() {
        mBinding.btnLayers.setOnClickListener {
            if (mBinding.btnToggleOsmNotesLayer.visibility == View.VISIBLE) {
                hideLayerMenu()
            } else {
                showLayerMenu()
            }
        }

        mBinding.btnToggleOsmNotesLayer.setOnClickListener {
            mErrorViewModel.toggleOsmNotesEnabled()
        }
        mBinding.btnToggleOsmNotesLayer.visibility = View.INVISIBLE

        mBinding.btnToggleKeeprightLayer.setOnClickListener {
            mErrorViewModel.toggleKeeprightEnabled()
        }
        mBinding.btnToggleKeeprightLayer.visibility = View.INVISIBLE

        mBinding.btnToggleOsmoseLayer.setOnClickListener {
            mErrorViewModel.toggleOsmoseEnabled()
        }
        mBinding.btnToggleOsmoseLayer.visibility = View.INVISIBLE
    }

    private fun hideLayerMenu() {
        slideOut(mBinding.btnToggleOsmoseLayer, mBinding.btnLayers, 0)
        slideOut(mBinding.btnToggleKeeprightLayer, mBinding.btnLayers, 150)
        slideOut(mBinding.btnToggleOsmNotesLayer, mBinding.btnLayers, 300)
    }

    private fun showLayerMenu() {
        slideIn(mBinding.btnToggleOsmNotesLayer, 0)
        slideIn(mBinding.btnToggleKeeprightLayer, 150)
        slideIn(mBinding.btnToggleOsmoseLayer, 300)
    }

    private fun setupReload() {
        mBinding.btnReload.setOnClickListener {
            mErrorViewModel.reloadErrors(mBinding.map.mapCenter, mBinding.map.boundingBox)
        }
    }

    /**
     * Sets up everything related to the ViewModel like Observers etc.
     */
    private fun setupViewModel() {
        mErrorViewModel.getError().observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                context?.getString(R.string.err_failed_to_download_errors)?.format(it),
                Toast.LENGTH_LONG
            ).show()
        }

        mErrorViewModel.getOsmNotes().observe(viewLifecycleOwner) {
            updateOsmNotes(it)
        }

        mErrorViewModel.getOsmNotesEnabled().observe(viewLifecycleOwner) {
            updateOsmNotes(mErrorViewModel.getOsmNotes().value!!)

            mBinding.btnToggleOsmNotesLayer.setImageDrawable(if (it) OsmNote.IcToggleLayer else OsmNote.IcToggleLayerDisabled)
        }

        mErrorViewModel.getKeeprightErrors().observe(viewLifecycleOwner) {
            updateKeeprightErrors(it)
        }

        mErrorViewModel.getKeeprightEnabled().observe(viewLifecycleOwner) {
            updateKeeprightErrors(mErrorViewModel.getKeeprightErrors().value!!)

            mBinding.btnToggleKeeprightLayer.setImageDrawable(if (it) KeeprightError.IcToggleLayer else KeeprightError.IcToggleLayerDisabled)
        }

        mErrorViewModel.getOsmoseErrors().observe(viewLifecycleOwner) {
            updateOsmoseErrors(it)
        }

        mErrorViewModel.getOsmoseEnabled().observe(viewLifecycleOwner) {
            updateOsmoseErrors(mErrorViewModel.getOsmoseErrors().value!!)

            mBinding.btnToggleOsmoseLayer.setImageDrawable(if (it) OsmoseError.IcToggleLayer else OsmoseError.IcToggleLayerDisabled)
        }

        mErrorViewModel.getContentLoading().observe(viewLifecycleOwner) {
            mBinding.progressbar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }

    /**
     * Sets up everything related to the Add Error functions
     */
    @SuppressLint("RestrictedApi")
    private fun setupAddError() {
        mBinding.btnAddOsmNote.setOnClickListener {
            OsmNotesAddErrorDialog(
                requireContext(),
                mErrorViewModel,
                mBinding.map.mapCenter).show()
        }

        mBinding.imgCrosshair.visibility = View.INVISIBLE
    }

    /**
     * Sets up everything related to the users location
     */
    private fun setupFollowLocation() {
        // Setup Location Overlay
        val locationOverlay = OsmBugsMyLocationNewOverlay(mBinding.map) {
            if (it)
                mBinding.btnLocation.setImageDrawable(Images.GetDrawable(R.drawable.ic_location_enabled))
            else
                mBinding.btnLocation.setImageDrawable(Images.GetDrawable(R.drawable.ic_location))
        }

        mBinding.map.overlays.add(locationOverlay)

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        mBinding.btnLocation.setOnClickListener {
            if (!locationOverlay.isFollowLocationEnabled)
                locationOverlay.enableFollowLocation()
            else
                locationOverlay.disableFollowLocation()
        }

        mBinding.btnLocation.visibility = if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) View.VISIBLE else View.GONE
    }

    /**
     * Sets up everything related to the Map
     */
    private fun setupMap() {
        mBinding.map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(mBinding.map)
                hideLayerMenu()

                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }))

        mBinding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mBinding.map.setMultiTouchControls(true)

        mBinding.map.controller.setCenter(mSettings.LastMapCenter)
        mBinding.map.controller.setZoom(mSettings.LastMapZoom.toDouble())

        mBinding.map.invalidate()
    }

    /**
     * Called when OsmNotes have been updated in the ViewModel
     */
    private fun updateOsmNotes(errors: java.util.ArrayList<OsmNote>) {
        mBinding.map.overlays.removeAll(mOsmNotes.toSet())
        mOsmNotes.clear()

        val window = OsmNoteInfoWindow(mBinding.map, mErrorViewModel)

        if (mErrorViewModel.getOsmNotesEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(OsmNoteMarker(it, mBinding.map))
                marker.infoWindow = window

                mOsmNotes.add(marker)
                mBinding.map.overlays.add(marker)
            }
        }

        mBinding.map.invalidate()
    }

    /**
     * Called when KeeprightErrors have been updated in the ViewModel
     */
    private fun updateKeeprightErrors(errors: List<KeeprightError>) {
        mBinding.map.overlays.removeAll(mKeeprightErrors.toSet())
        mKeeprightErrors.clear()

        val window = KeeprightInfoWindow(mBinding.map, mErrorViewModel)

        if (mErrorViewModel.getKeeprightEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(KeeprightMarker(it, mBinding.map))
                marker.infoWindow = window

                mKeeprightErrors.add(marker)
                mBinding.map.overlays.add(marker)
            }
        }

        mBinding.map.invalidate()
    }

    /**
     * Called when Osmose Errors have been updated in the ViewModel
     */
    private fun updateOsmoseErrors(errors: ArrayList<OsmoseError>) {
        mBinding.map.overlays.removeAll(mOsmoseErrors.toSet())
        mOsmoseErrors.clear()

        val window = OsmoseInfoWindow(mBinding.map)

        if (mErrorViewModel.getOsmoseEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(OsmoseMarker(it, mBinding.map))
                marker.infoWindow = window

                mOsmoseErrors.add(marker)
                mBinding.map.overlays.add(marker)
            }
        }

        mBinding.map.invalidate()
    }

    /**
     * Adds toggle Behaviour to Markers, so that when clicking a Marker all other open Markers will be closed.
     * When the InfoWindow is already open, it will be closed
     * @param marker The marker to modify
     * @return The modified Marker
     */
    private fun <T : Marker> setupMarker(marker: T): T {
        marker.setOnMarkerClickListener { _, _ ->
            if (!marker.isInfoWindowOpen) {
                InfoWindow.closeAllInfoWindowsOn(mBinding.map)
                marker.showInfoWindow()
            } else {
                marker.closeInfoWindow()
            }

            true
        }

        return marker
    }

    private fun initialHide(btn: View, parent: View) {
        val viewLoc = IntArray(2)
        val toLoc = IntArray(2)

        btn.getLocationOnScreen(viewLoc)
        parent.getLocationOnScreen(toLoc)

        val deltaX = (toLoc[0] - viewLoc[0]).toFloat()
        val deltaY = (toLoc[1] - viewLoc[1]).toFloat()

        btn.translationX = deltaX
        btn.translationY = deltaY
        btn.scaleX = .2f
        btn.scaleY = .2f
    }

    private fun slideOut(view: View, to: View, startDelay: Long) {
        if (view.visibility == View.VISIBLE) {
            val viewLoc = IntArray(2)
            val toLoc = IntArray(2)

            view.getLocationOnScreen(viewLoc)
            to.getLocationOnScreen(toLoc)

            val deltaX = (toLoc[0] - viewLoc[0]).toFloat()
            val deltaY = (toLoc[1] - viewLoc[1]).toFloat()

            view.translationX = 0f
            view.translationY = 0f
            view.scaleX = 1f
            view.scaleY = 1f

            view.animate()
                .translationX(deltaX)
                .translationY(deltaY)
                .scaleX(.2f)
                .scaleY(.2f)
                .setDuration(300L)
                .withEndAction { view.visibility = View.INVISIBLE }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(startDelay)
                .start()
        }
    }

    private fun slideIn(view: View, startDelay: Long) {
        view.visibility = View.VISIBLE

        view.animate()
            .translationX(0f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay(startDelay)
            .start()
    }
}