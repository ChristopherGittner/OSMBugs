package org.gittner.osmbugs.mapdust

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
 * The first three Items are Open, Closed and Ignored
 */
class MapdustSelectErrorsDialog : DialogFragment() {
    private val mSettings = Settings.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val adapter = EnabledErrorsAdapter<MapdustError.ERROR_TYPE>(it)

            // Add Ignored and Temporary ignored
            adapter.add(EnabledErrorsAdapter.Data(null, mSettings.Mapdust.ShowOpen, R.string.show_open_errors, MapdustError.IcOther))
            adapter.add(EnabledErrorsAdapter.Data(null, mSettings.Mapdust.ShowClosed, R.string.show_closed_notes, MapdustError.IcClosed))
            adapter.add(EnabledErrorsAdapter.Data(null, mSettings.Mapdust.ShowIgnored, R.string.show_ignored_errors, MapdustError.IcIgnored))

            // Add all Keepright Error Types
            MapdustError.ERROR_TYPE.values().forEach { errorType ->
                adapter.add(
                    EnabledErrorsAdapter.Data(
                        errorType,
                        mSettings.Mapdust.GetTypeEnabled(errorType),
                        errorType.DescriptionId,
                        MapdustError.GetIconFor(MapdustError.STATE.OPEN, errorType)
                    )
                )
            }

            val dialog = AlertDialog
                .Builder(it)
                .setAdapter(adapter) { _, _ -> }
                .setPositiveButton(R.string.done) { dialog, _ ->
                    // Set Ignored and Tmp Ignored. These are always the first three items
                    mSettings.Mapdust.ShowOpen = adapter.getItem(0)!!.State
                    mSettings.Mapdust.ShowClosed = adapter.getItem(1)!!.State
                    mSettings.Mapdust.ShowIgnored = adapter.getItem(2)!!.State

                    // Go through each item and enable or disable it in the Settings depending on the chosen state
                    for (i in 3 until adapter.count) {
                        val item = adapter.getItem(i)!!
                        if (item.Ref != null) {
                            mSettings.Mapdust.SetTypeEnabled(item.Ref, item.State)
                        }
                    }

                    dialog.dismiss()
                }
                .create()

            dialog
        }!!
    }
}