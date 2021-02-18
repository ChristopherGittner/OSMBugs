package org.gittner.osmbugs.osmnotes

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.DialogAddOsmNoteBinding
import org.gittner.osmbugs.ui.ErrorViewModel
import org.osmdroid.api.IGeoPoint

class OsmNotesAddErrorDialog(context: Context, viewModel: ErrorViewModel, mapCenter: IGeoPoint, successCb: SuccessCb) : AlertDialog(context) {
    private val mViewModel = viewModel
    private val mMapCenter = mapCenter
    private val mSuccessCb = successCb

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.dialog_add_osm_note_title)

        val v = layoutInflater.inflate(R.layout.dialog_add_osm_note, null)
        setView(v)

        val binding = DialogAddOsmNoteBinding.bind(v)

        binding.apply {
            edtxtComment.addTextChangedListener {
                imgSave.visibility = if (it!!.isEmpty()) View.GONE else View.VISIBLE
            }

            imgSave.setOnClickListener {
                MainScope().launch {
                    progressbarSave.visibility = View.VISIBLE
                    imgSave.visibility = View.GONE
                    edtxtComment.isEnabled = false

                    try {
                        mViewModel.addOsmNote(
                            mMapCenter,
                            edtxtComment.text.toString()
                        )

                        dismiss()
                        mSuccessCb.onSuccess()
                    } catch (err: Exception) {
                        Toast.makeText(context, context.getString(R.string.failed_to_add_error).format(err.message), Toast.LENGTH_LONG).show()
                    } finally {
                        progressbarSave.visibility = View.GONE
                        imgSave.visibility = View.VISIBLE
                        edtxtComment.isEnabled = true
                    }
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    interface SuccessCb {
        fun onSuccess()
    }
}