package org.gittner.osmbugs.keepright

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Settings
import org.gittner.osmbugs.ui.EnabledErrorsAdapter

/**
 * Dialog that displays a List of all available Keepright Error Types, and lets the User select or deselect each Type
 * The first two Items are Ignored and TmpIgnored
 */
class KeeprightSelectErrorsDialog : DialogFragment() {
    private val mSettings = Settings.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val adapter = EnabledErrorsAdapter<KeeprightError.ERROR_TYPE>(it)

            // Add Ignored and Temporary ignored
            adapter.add(EnabledErrorsAdapter.Data(null, mSettings.Keepright.ShowIgnored, R.string.show_ignored, KeeprightError.IcZapIgnored))
            adapter.add(EnabledErrorsAdapter.Data(null, mSettings.Keepright.ShowTmpIgnored, R.string.show_temporary_ignored, KeeprightError.IcZapTmpIgnored))

            // Add all Keepright Error Types
            KeeprightError.ERROR_TYPE.entries.forEach { errorType ->
                adapter.add(
                    EnabledErrorsAdapter.Data(
                        errorType,
                        mSettings.Keepright.GetTypeEnabled(errorType),
                        errorType.DescriptionId,
                        errorType.Icon
                    )
                )
            }

            val dialog = AlertDialog
                .Builder(it)
                .setAdapter(adapter) { _, _ -> }
                .setPositiveButton(R.string.done) { dialog, _ ->
                    // Set Ignored and Tmp Ignored. These are always the first two items
                    mSettings.Keepright.ShowIgnored = adapter.getItem(0)!!.State
                    mSettings.Keepright.ShowTmpIgnored = adapter.getItem(1)!!.State

                    // Go through each item and enable or disable it in the Settings depending on the chosen state
                    for (i in 2 until adapter.count) {
                        val item = adapter.getItem(i)!!
                        if (item.Ref != null) {
                            mSettings.Keepright.SetTypeEnabled(item.Ref, item.State)
                        }
                    }

                    dialog.dismiss()
                }
                .create()

            dialog
        }!!
    }
}