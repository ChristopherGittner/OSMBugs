package org.gittner.osmbugs.ui

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import org.gittner.osmbugs.IntentHelper
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Settings
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.util.*

open class ErrorInfoWindow(layoutResId: Int, mapView: MapView) : InfoWindow(layoutResId, mapView) {

    private var mStateView: View? = null

    // Used to notify the Tutorial Runnable that the Window has been closed
    private var mCancelToken = CancelToken()

    override fun onOpen(item: Any?) {
        val error = (item as ErrorMarker<*>).mError

        // Don't close when touching the InfoWindow
        view.setOnTouchListener { _, _ ->
            true
        }

        view.findViewById<ImageView>(R.id.imgShare).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:%f,%f".format(Locale.ENGLISH, error.Point.latitude, error.Point.longitude)))

            if (!IntentHelper.intentHasReceivers(mMapView.context, intent)) {
                Toast.makeText(mMapView.context, R.string.toast_geo_intent_no_app_found, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mMapView.context.startActivity(intent)
        }
    }

    fun setStateView(view: View) {
        if (mStateView == null) {
            mStateView = view

            startShake(mCancelToken)
        }
    }

    fun stateViewClicked() {
        Settings.getInstance().TutorialBugStateDone = true
    }

    private fun startShake(cancelToken: CancelToken) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!cancelToken.cancelled && !Settings.getInstance().TutorialBugStateDone) {
                mStateView?.startAnimation(
                    AnimationUtils.loadAnimation(
                        mapView.context,
                        R.anim.shake
                    )
                )
                startShake(cancelToken)
            }
        }, 5000)
    }

    override fun onClose() {
        mCancelToken.cancelled = true
    }
}