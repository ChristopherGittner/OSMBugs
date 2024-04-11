package org.gittner.osmbugs.osmnotes

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.OsmNotesMarkerBinding
import org.gittner.osmbugs.statics.Settings
import org.gittner.osmbugs.ui.ErrorInfoWindow
import org.gittner.osmbugs.ui.ErrorViewModel
import org.joda.time.DateTimeZone
import org.osmdroid.views.MapView

class OsmNoteInfoWindow(map: MapView, viewModel: ErrorViewModel) : ErrorInfoWindow(R.layout.osm_notes_marker, map) {
    private val mBinding: OsmNotesMarkerBinding = OsmNotesMarkerBinding.bind(view)

    private val mViewModel = viewModel

    private var mNewState = OsmNote.STATE.OPEN

    private var mSettings = Settings.getInstance()

    private var mEditComment = false

    override fun onOpen(item: Any?) {
        super.onOpen(item)

        val error = (item as OsmNoteMarker).mError

        mNewState = error.State

        mEditComment = false

        mBinding.apply {
            setStateView(imgState)

            txtvDescription.text = error.Description

            imgSave.visibility = View.GONE
            imgSave.setOnClickListener {
                if (!mSettings.OsmNotes.IsLoggedIn()) {
                    startLogin()
                    return@setOnClickListener
                }

                MainScope().launch {
                    progressbarSave.visibility = View.VISIBLE
                    imgSave.visibility = View.GONE

                    try {
                        mViewModel.updateOsmNote(error, edtxtComment.text.toString(), mNewState)

                        close()
                    } catch (error: Exception) {
                        Toast.makeText(
                            mMapView.context,
                            mMapView.context.getString(R.string.err_failed_to_update_error)
                                .format(error.message),
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    } finally {
                        progressbarSave.visibility = View.GONE
                        imgSave.visibility = View.VISIBLE
                    }
                }
            }

            imgState.setOnClickListener {
                stateViewClicked()

                mNewState = if (mNewState == OsmNote.STATE.OPEN) OsmNote.STATE.CLOSED else OsmNote.STATE.OPEN

                updateViews(error)
            }

            imgEditComment.setOnClickListener {
                mEditComment = true
                updateViews(error)
            }

            edtxtComment.doOnTextChanged { _, _, _, _ ->
                updateViews(error)
            }
            edtxtComment.text.clear()

            txtvUser.text = error.User

            txtvDate.text = error.Date.withZone(DateTimeZone.getDefault()).toString(mMapView.context.getString(R.string.datetime_format))

            if (error.Comments.isNotEmpty()) {
                val comments = ArrayList<OsmNote.OsmNoteComment>()
                error.Comments.reversed().forEach {
                    comments.add(it)
                }
                val adapter = OsmNoteCommentsAdapter(mMapView.context, comments)
                lstvComments.adapter = adapter
                lstvComments.visibility = View.VISIBLE
            } else {
                lstvComments.visibility = View.GONE
            }
        }

        updateViews(error)
    }

    private fun startLogin() {
        AlertDialog.Builder(mMapView.context)
            .setTitle(R.string.dialog_osmnotes_login_required_title)
            .setMessage(R.string.dialog_osmnotes_login_required_message)
            .setCancelable(true)
            .setPositiveButton(R.string.dialog_osmnotes_login_required_positive) { _, _ ->
                mMapView.context.startActivity(Intent(mMapView.context, OsmNotesLoginActivity::class.java))
            }.show()
    }

    private fun updateViews(error: OsmNote) {
        mBinding.apply {
            imgState.setImageDrawable(if (mNewState == OsmNote.STATE.OPEN) OsmNote.IcOpen else OsmNote.IcClosed)

            imgSave.visibility = if (error.State != mNewState || edtxtComment.text.toString() != "")
                View.VISIBLE else
                View.GONE

            edtxtComment.visibility =
                if ((error.State == OsmNote.STATE.OPEN && mEditComment) || (error.State == OsmNote.STATE.CLOSED && mNewState == OsmNote.STATE.OPEN)) View.VISIBLE else View.GONE

            imgEditComment.visibility = if (error.State == OsmNote.STATE.OPEN && !mEditComment) View.VISIBLE else View.GONE
        }
    }
}