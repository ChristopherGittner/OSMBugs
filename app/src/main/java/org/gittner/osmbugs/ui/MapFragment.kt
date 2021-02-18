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
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.map_fragment.*
import org.gittner.osmbugs.R
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.keepright.KeeprightInfoWindow
import org.gittner.osmbugs.keepright.KeeprightMarker
import org.gittner.osmbugs.mapdust.MapdustAddErrorDialog
import org.gittner.osmbugs.mapdust.MapdustError
import org.gittner.osmbugs.mapdust.MapdustInfoWindow
import org.gittner.osmbugs.mapdust.MapdustMarker
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmnotes.OsmNoteInfoWindow
import org.gittner.osmbugs.osmnotes.OsmNoteMarker
import org.gittner.osmbugs.osmnotes.OsmNotesAddErrorDialog
import org.gittner.osmbugs.osmose.OsmoseError
import org.gittner.osmbugs.osmose.OsmoseInfoWindow
import org.gittner.osmbugs.osmose.OsmoseMarker
import org.gittner.osmbugs.statics.Settings
import org.koin.android.ext.android.inject
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Displays a Map with Buttons to reload the Errors, toggle the visible Layers and shows a progress bar when loading new Errors
 */
class MapFragment : Fragment(R.layout.map_fragment) {
    private val mErrorViewModel: ErrorViewModel by inject()
    private val mSettings = Settings.getInstance()

    // These ArrayLists store all Visible Markers on the Map, to be able to remove them when the Markers are updated or the Layers are toggled
    private val mOsmNotes = ArrayList<OsmNoteMarker>()
    private val mKeeprightErrors = ArrayList<KeeprightMarker>()
    private val mMapdustErrors = ArrayList<MapdustMarker>()
    private val mOsmoseErrors = ArrayList<OsmoseMarker>()

    private var mBtnInitDone = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
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
                initialHide(btn_add_mapdust_error, btn_add_error)
                initialHide(btn_add_osm_note, btn_add_error)
                initialHide(btn_toggle_osm_notes_layer, btn_layers)
                initialHide(btn_toggle_keepright_layer, btn_layers)
                initialHide(btn_toggle_mapdust_layer, btn_layers)
                initialHide(btn_toggle_osmose_layer, btn_layers)
                mBtnInitDone = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val style = mSettings.MapStyle
        if (style != null) {
            map.setTileSource(style.TileSource)
        }

        map.onResume()
    }

    override fun onPause() {
        super.onPause()

        map.onPause()

        mSettings.LastMapCenter = map.mapCenter
        mSettings.LastMapZoom = map.zoomLevelDouble.toInt()
    }

    /**
     * Sets up everything related to the Toggle Layer Buttons
     */
    private fun setupLayerToggle() {
        btn_layers.setOnClickListener {
            if (btn_toggle_osm_notes_layer.visibility == View.VISIBLE) {
                hideLayerMenu()
            } else {
                showLayerMenu()
            }

            hideAddErrorMenu()
        }

        btn_toggle_osm_notes_layer.setOnClickListener {
            mErrorViewModel.toggleOsmNotesEnabled()
        }
        btn_toggle_osm_notes_layer.visibility = View.INVISIBLE

        btn_toggle_keepright_layer.setOnClickListener {
            mErrorViewModel.toggleKeeprightEnabled()
        }
        btn_toggle_keepright_layer.visibility = View.INVISIBLE

        btn_toggle_mapdust_layer.setOnClickListener {
            mErrorViewModel.toggleMapdustEnabled()
        }
        btn_toggle_mapdust_layer.visibility = View.INVISIBLE

        btn_toggle_osmose_layer.setOnClickListener {
            mErrorViewModel.toggleOsmoseEnabled()
        }
        btn_toggle_osmose_layer.visibility = View.INVISIBLE
    }

    private fun hideLayerMenu() {
        slideOut(btn_toggle_osmose_layer, btn_layers, 300, 0)
        slideOut(btn_toggle_mapdust_layer, btn_layers, 300, 150)
        slideOut(btn_toggle_keepright_layer, btn_layers, 300, 150)
        slideOut(btn_toggle_osm_notes_layer, btn_layers, 300, 300)
    }

    private fun showLayerMenu() {
        slideIn(btn_toggle_osm_notes_layer, btn_layers, 300, 0)
        slideIn(btn_toggle_keepright_layer, btn_layers, 300, 150)
        slideIn(btn_toggle_mapdust_layer, btn_layers, 300, 150)
        slideIn(btn_toggle_osmose_layer, btn_layers, 300, 300)
    }

    private fun setupReload() {
        btn_reload.setOnClickListener {
            mErrorViewModel.onMapMoved(map.mapCenter, map.boundingBox)
        }
    }

    /**
     * Sets up everything related to the ViewModel like Observers etc.
     */
    private fun setupViewModel() {
        mErrorViewModel.getError().observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, context?.getString(R.string.err_failed_to_download_errors)?.format(it), Toast.LENGTH_LONG).show()
        })

        mErrorViewModel.getOsmNotes().observe(viewLifecycleOwner, Observer {
            updateOsmNotes(it)
        })
        mErrorViewModel.getOsmNotesEnabled().observe(viewLifecycleOwner, Observer {
            updateOsmNotes(mErrorViewModel.getOsmNotes().value!!)

            btn_toggle_osm_notes_layer.setImageDrawable(if (it) OsmNote.IcToggleLayer else OsmNote.IcToggleLayerDisabled)
        })

        mErrorViewModel.getKeeprightErrors().observe(viewLifecycleOwner, Observer {
            updateKeeprightErrors(it)
        })
        mErrorViewModel.getKeeprightEnabled().observe(viewLifecycleOwner, Observer {
            updateKeeprightErrors(mErrorViewModel.getKeeprightErrors().value!!)

            btn_toggle_keepright_layer.setImageDrawable(if (it) KeeprightError.IcToggleLayer else KeeprightError.IcToggleLayerDisabled)
        })

        mErrorViewModel.getMapdustErrors().observe(viewLifecycleOwner, Observer {
            updateMapdustErrors(it)
        })
        mErrorViewModel.getMapdustEnabled().observe(viewLifecycleOwner, Observer {
            updateMapdustErrors(mErrorViewModel.getMapdustErrors().value!!)

            btn_toggle_mapdust_layer.setImageDrawable(if (it) MapdustError.IcToggleLayer else MapdustError.IcToggleLayerDisabled)
        })

        mErrorViewModel.getOsmoseErrors().observe(viewLifecycleOwner, Observer {
            updateOsmoseErrors(it)
        })
        mErrorViewModel.getOsmoseEnabled().observe(viewLifecycleOwner, Observer {
            updateOsmoseErrors(mErrorViewModel.getOsmoseErrors().value!!)

            btn_toggle_osmose_layer.setImageDrawable(if (it) OsmoseError.IcToggleLayer else OsmoseError.IcToggleLayerDisabled)
        })

        mErrorViewModel.getContentLoading().observe(viewLifecycleOwner, Observer {
            progressbar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    /**
     * Sets up everything related to te Add Error functions
     */
    @SuppressLint("RestrictedApi")
    private fun setupAddError() {
        btn_add_error.setOnClickListener {
            toggleAddBug()

            hideLayerMenu()
        }

        btn_add_osm_note.setOnClickListener {
            OsmNotesAddErrorDialog(
                requireContext(),
                mErrorViewModel,
                map.mapCenter,
                object : OsmNotesAddErrorDialog.SuccessCb {
                    override fun onSuccess() {
                        hideAddErrorMenu()
                    }
                }).show()
        }
        btn_add_osm_note.visibility = View.INVISIBLE

        btn_add_mapdust_error.setOnClickListener {
            MapdustAddErrorDialog(
                requireContext(),
                mErrorViewModel,
                map.mapCenter,
                object : MapdustAddErrorDialog.SuccessCb {
                    override fun onSuccess() {
                        hideAddErrorMenu()
                    }
                }).show()
        }
        btn_add_mapdust_error.visibility = View.INVISIBLE

        img_crosshair.visibility = View.INVISIBLE
    }

    /**
     * Sets up everything related to the users location
     */
    private fun setupFollowLocation() {
        // Setup Location Overlay
        val locationOverlay = MyLocationNewOverlay(map)
        map.overlays.add(locationOverlay)

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        btn_location.setOnClickListener {
            locationOverlay.enableFollowLocation()
        }



        btn_location.visibility = if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) View.VISIBLE else View.GONE
    }

    /**
     * Sets up everything related to the Map
     */
    private fun setupMap() {
        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                hideLayerMenu()
                hideAddErrorMenu()

                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }))

        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)

        map.controller.setCenter(mSettings.LastMapCenter)
        map.controller.setZoom(mSettings.LastMapZoom.toDouble())

        map.invalidate()
    }

    /**
     * Called when OsmNotes have been updated in the ViewModel
     */
    private fun updateOsmNotes(errors: java.util.ArrayList<OsmNote>) {
        map.overlays.removeAll(mOsmNotes)
        mOsmNotes.clear()

        val window = OsmNoteInfoWindow(map, mErrorViewModel)

        if (mErrorViewModel.getOsmNotesEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(OsmNoteMarker(it, map))
                marker.infoWindow = window

                mOsmNotes.add(marker)
                map.overlays.add(marker)
            }
        }

        map.invalidate()
    }

    /**
     * Called when KeeprightErrors have been updated in the ViewModel
     */
    private fun updateKeeprightErrors(errors: List<KeeprightError>) {
        map.overlays.removeAll(mKeeprightErrors)
        mKeeprightErrors.clear()

        val window = KeeprightInfoWindow(map, mErrorViewModel)

        if (mErrorViewModel.getKeeprightEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(KeeprightMarker(it, map))
                marker.infoWindow = window

                mKeeprightErrors.add(marker)
                map.overlays.add(marker)
            }
        }

        map.invalidate()
    }

    /**
     * Called when Mapdust Errors have been updated in the ViewModel
     */
    private fun updateMapdustErrors(errors: ArrayList<MapdustError>) {
        map.overlays.removeAll(mMapdustErrors)
        mMapdustErrors.clear()

        val window = MapdustInfoWindow(map, mErrorViewModel)

        if (mErrorViewModel.getMapdustEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(MapdustMarker(it, map))
                marker.infoWindow = window

                mMapdustErrors.add(marker)
                map.overlays.add(marker)
            }
        }

        map.invalidate()
    }

    /**
     * Called when Osmose Errors have been updated in the ViewModel
     */
    private fun updateOsmoseErrors(errors: ArrayList<OsmoseError>) {
        map.overlays.removeAll(mOsmoseErrors)
        mOsmoseErrors.clear()

        val window = OsmoseInfoWindow(map)

        if (mErrorViewModel.getOsmoseEnabled().value!!) {
            errors.forEach {
                val marker = setupMarker(OsmoseMarker(it, map))
                marker.infoWindow = window

                mOsmoseErrors.add(marker)
                map.overlays.add(marker)
            }
        }

        map.invalidate()
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
                InfoWindow.closeAllInfoWindowsOn(map)
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

    private fun slideOut(view: View, to : View, duration : Long, startDelay : Long) {
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
                .setDuration(duration)
                .withEndAction { view.visibility = View.INVISIBLE }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(startDelay)
                .start()
        }
    }

    private fun slideIn(view: View, from : View, duration : Long, startDelay : Long) {
        view.visibility = View.VISIBLE

        view.animate()
                .translationX(0f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .setInterpolator(OvershootInterpolator())
                .setStartDelay(startDelay)
                .start()
    }

    private fun showAddErrorMenu() {
        slideIn(btn_add_osm_note, btn_add_error, 300, 0)
        slideIn(btn_add_mapdust_error, btn_add_error, 300, 150)

        img_crosshair.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator())
                .setDuration(300)
                .start()
        img_crosshair.visibility = View.VISIBLE
    }

    private fun hideAddErrorMenu() {
        slideOut(btn_add_mapdust_error, btn_add_error, 300, 0)
        slideOut(btn_add_osm_note, btn_add_error, 300, 150)

        img_crosshair.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(300)
                .withEndAction { img_crosshair.visibility = View.INVISIBLE }
                .start()
    }

    private fun toggleAddBug() {
        if (btn_add_osm_note.visibility == View.VISIBLE) {
            hideAddErrorMenu()
        } else {
            showAddErrorMenu()
        }
    }
}