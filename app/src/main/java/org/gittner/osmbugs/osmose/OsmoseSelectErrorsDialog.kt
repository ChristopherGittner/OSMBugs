package org.gittner.osmbugs.osmose

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Settings
import org.gittner.osmbugs.ui.EnabledErrorsAdapter
import org.koin.android.ext.android.inject

/**
 * Dialog that displays a List of all available Keepright Error Types, and lets the User select or deselect each Type
 */
class OsmoseSelectErrorsDialog : DialogFragment() {
    private val mSettings = Settings.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val adapter = EnabledErrorsAdapter<OsmoseError.ERROR_TYPE>(it)

            // Add all Keepright Error Types
            OsmoseError.ERROR_TYPE.values().forEach { errorType ->
                adapter.add(
                    EnabledErrorsAdapter.Data(
                        errorType,
                        mSettings.Osmose.GetTypeEnabled(errorType),
                        errorType.DescriptionId,
                        errorType.Icon
                    )
                )
            }

            val dialog = AlertDialog
                .Builder(it)
                .setAdapter(adapter) { _, _ -> }
                .setPositiveButton(R.string.done) { dialog, _ ->
                    // Go through each item and enable or disable it in the Settings depending on the chosen state
                    for (i in 0 until adapter.count) {
                        val item = adapter.getItem(i)!!
                        if (item.Ref != null) {
                            mSettings.Osmose.SetTypeEnabled(item.Ref, item.State)
                        }
                    }

                    dialog.dismiss()
                }
                .create()

            dialog
        }!!
    }
}