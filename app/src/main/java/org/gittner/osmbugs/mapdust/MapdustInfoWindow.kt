package org.gittner.osmbugs.mapdust

import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.MapdustMarkerBinding
import org.gittner.osmbugs.ui.ErrorInfoWindow
import org.gittner.osmbugs.ui.ErrorViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.osmdroid.views.MapView

class MapdustInfoWindow(map: MapView, viewModel: ErrorViewModel) : ErrorInfoWindow(R.layout.mapdust_marker, map), KoinComponent {
    private val mBinding = MapdustMarkerBinding.bind(view)

    private val mApi: MapdustApi by inject()

    private val mViewModel: ErrorViewModel = viewModel

    private var mNewState = MapdustError.STATE.OPEN

    override fun onOpen(item: Any?) {
        super.onOpen(item)

        val error = (item as MapdustMarker).mError

        mNewState = error.State

        mBinding.apply {
            setStateView(imgState)

            description.text = error.Description

            imgSave.visibility = View.GONE
            imgSave.setOnClickListener {
                MainScope().launch {
                    progressbarSave.visibility = View.VISIBLE
                    imgSave.visibility = View.GONE

                    try {
                        mViewModel.updateMapdustError(error, edtxtComment.text.toString(), mNewState)

                        close()
                    } catch (error: Exception) {
                        Toast.makeText(mMapView.context, mMapView.context.getString(R.string.err_failed_to_update_error).format(error.message), Toast.LENGTH_LONG).show()
                        return@launch
                    } finally {
                        progressbarSave.visibility = View.GONE
                        imgSave.visibility = View.VISIBLE
                    }
                }
            }

            imgState.setImageDrawable(MapdustError.GetIconFor(mNewState, error.Type))
            imgState.setOnClickListener {
                stateViewClicked()

                mNewState = when (mNewState) {
                    MapdustError.STATE.OPEN -> MapdustError.STATE.CLOSED
                    MapdustError.STATE.CLOSED -> MapdustError.STATE.IGNORED
                    MapdustError.STATE.IGNORED -> MapdustError.STATE.OPEN
                }
                imgState.setImageDrawable(MapdustError.GetIconFor(mNewState, error.Type))

                updateUi(error)
            }

            lstvComments.adapter = MapdustCommentsAdapter(mMapView.context, error.Comments)

            edtxtComment.setText("")
            edtxtComment.doOnTextChanged { _, _, _, _ ->
                updateUi(error)
            }

            user.text = error.User

            GlobalScope.launch(Dispatchers.Main) {
                error.Comments = mApi.download(error.Id).Comments

                progressbarComments.visibility = View.GONE

                lstvComments.invalidate()
                if (error.Comments.size > 0) {
                    lstvComments.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClose() {

    }

    private fun updateUi(error : MapdustError) {
        mBinding.apply {
            imgSave.visibility = if (edtxtComment.text.toString() != "") // No matter what, we always need a comment, even if changing State
                View.VISIBLE else
                View.GONE

            edtxtComment.setHint(if (error.State == mNewState) R.string.comment else R.string.comment_required)
        }
    }
}