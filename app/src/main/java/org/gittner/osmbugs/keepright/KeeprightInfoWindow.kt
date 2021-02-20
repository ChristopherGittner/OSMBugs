package org.gittner.osmbugs.keepright

import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.KeeprightMarkerBinding
import org.gittner.osmbugs.statics.OpenStreetMap
import org.gittner.osmbugs.ui.ErrorInfoWindow
import org.gittner.osmbugs.ui.ErrorViewModel
import org.osmdroid.views.MapView

class KeeprightInfoWindow(map: MapView, viewModel: ErrorViewModel) : ErrorInfoWindow(R.layout.keepright_marker, map) {
    private val mBinding = KeeprightMarkerBinding.bind(view)

    private val mViewModel: ErrorViewModel = viewModel

    private var mNewState = KeeprightError.STATE.OPEN

    override fun onOpen(item: Any?) {
        super.onOpen(item)

        val error = (item as KeeprightMarker).mError

        mNewState = error.State

        mBinding.apply {
            setStateView(imgState)

            txtvTitle.text = error.Title

            description.text = error.Description
            Linkify.addLinks(description, Linkify.WEB_URLS)
            description.text = HtmlCompat.fromHtml(description.text.toString(), 0)

            imgSave.visibility = View.GONE
            imgSave.setOnClickListener {
                MainScope().launch {
                    progressbar.visibility = View.VISIBLE
                    imgSave.visibility = View.GONE

                    try {
                        mViewModel.updateKeeprightError(error, edtxtComment.text.toString(), mNewState)

                        close()
                    } catch (error: Exception) {
                        Toast.makeText(
                            mMapView.context,
                            mMapView.context.getString(R.string.err_failed_to_update_error).format(error.message),
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    } finally {
                        progressbar.visibility = View.GONE
                        imgSave.visibility = View.VISIBLE
                    }
                }
            }

            imgState.setImageDrawable(KeeprightError.GetZapIconFor(mNewState, error.Type))
            imgState.setOnClickListener {
                stateViewClicked()

                mNewState = when (mNewState) {
                    KeeprightError.STATE.OPEN -> KeeprightError.STATE.IGNORED_TMP
                    KeeprightError.STATE.IGNORED_TMP -> KeeprightError.STATE.IGNORED
                    KeeprightError.STATE.IGNORED -> KeeprightError.STATE.OPEN
                }
                imgState.setImageDrawable(KeeprightError.GetZapIconFor(mNewState, error.Type))

                updateSaveState(error)
            }

            imgBrowse.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.Builder()
                    .scheme("https")
                    .authority("openstreetmap.org")
                    .appendPath(
                        when (error.ObjectType) {
                            OpenStreetMap.TYPE.NODE -> "node"
                            OpenStreetMap.TYPE.WAY -> "way"
                            OpenStreetMap.TYPE.RELATION -> "relation"
                        }
                    )
                    .appendPath(error.Way.toString())
                    .build()
                mMapView.context.startActivity(intent)
            }

            edtxtComment.setText(error.Comment)
            edtxtComment.doOnTextChanged { _, _, _, _ ->
                updateSaveState(error)
            }
        }
    }

    override fun onClose() {

    }

    private fun updateSaveState(error: KeeprightError) {
        mBinding.apply {
            imgSave.visibility = if (error.State != mNewState || edtxtComment.text.toString() != error.Comment)
                View.VISIBLE else
                View.GONE
        }
    }
}