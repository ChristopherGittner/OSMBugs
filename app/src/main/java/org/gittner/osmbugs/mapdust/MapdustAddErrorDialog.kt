package org.gittner.osmbugs.mapdust

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.DialogAddMapdustErrorBinding
import org.gittner.osmbugs.ui.ErrorViewModel
import org.osmdroid.api.IGeoPoint

class MapdustAddErrorDialog(context: Context, viewModel: ErrorViewModel, mapCenter: IGeoPoint, successCb: SuccessCb) : AlertDialog(context) {
    private val mViewModel = viewModel
    private val mMapCenter = mapCenter
    private val mSuccessCb = successCb

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.dialog_add_mapdust_error_title)

        val v = layoutInflater.inflate(R.layout.dialog_add_mapdust_error, null)
        setView(v)

        val binding = DialogAddMapdustErrorBinding.bind(v)

        binding.apply {
            spinner.adapter = MapdustTypeAdapter(context)
            edtxtComment.addTextChangedListener {
                imgSave.visibility = if (it!!.isEmpty()) View.GONE else View.VISIBLE
            }

            imgSave.setOnClickListener {
                MainScope().launch {
                    progressbarSave.visibility = View.VISIBLE
                    imgSave.visibility = View.GONE
                    edtxtComment.isEnabled = false

                    try {
                        mViewModel.addMapdustError(
                            mMapCenter,
                            spinner.selectedItem as MapdustError.ERROR_TYPE,
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