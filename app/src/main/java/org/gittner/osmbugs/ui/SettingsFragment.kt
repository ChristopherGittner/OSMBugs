package org.gittner.osmbugs.ui

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.gittner.osmbugs.R
import org.gittner.osmbugs.keepright.KeeprightSelectErrorsDialog
import org.gittner.osmbugs.osmose.OsmoseSelectErrorsDialog
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.modules.SqlTileWriter


class SettingsFragment : PreferenceFragmentCompat() {
    private val mViewModel: ErrorViewModel by activityViewModel<ErrorViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        findPreference<Preference>(getString(R.string.pref_keepright_enabled_types))?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            activity?.supportFragmentManager?.let { KeeprightSelectErrorsDialog().show(it, "Select Errors") }
            true
        }

        findPreference<Preference>(getString(R.string.pref_osmose_enabled_types))?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            activity?.supportFragmentManager?.let { OsmoseSelectErrorsDialog().show(it, "Select Errors") }
            true
        }

        findPreference<Preference>(getString(R.string.pref_openstreetmap_notes_log_in))?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mViewModel.triggerAction(ErrorViewModel.Companion.Action.OSM_NOTES_LOGIN)
            true
        }

        findPreference<EditTextPreference>(getString(R.string.pref_tile_cache_ttl_override))?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isEmpty()) {
                    Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show()
                    false
                } else {

                    Configuration.getInstance().expirationOverrideDuration =
                        newValue.toString().toLong() * 1000

                    clearTileCache()

                    true
                }
            }

            setOnBindEditTextListener{ it.inputType = InputType.TYPE_CLASS_NUMBER }
        }

        findPreference<EditTextPreference>(getString(R.string.pref_cache_size))?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isEmpty()) {
                    Toast.makeText(context, R.string.invalid_value, Toast.LENGTH_LONG).show()
                    false
                } else {
                    true
                }
            }

            setOnBindEditTextListener{ it.inputType = InputType.TYPE_CLASS_NUMBER }
        }

        findPreference<Preference>(getString(R.string.pref_clear_tile_cache))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (clearTileCache()) {
                    Toast.makeText(requireContext(), R.string.tile_cache_cleared, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), R.string.tile_cache_cleared_failed, Toast.LENGTH_LONG).show()
                }

                true
            }
        }
    }

    private fun clearTileCache(): Boolean {
        val sqlTileWriter = SqlTileWriter()
        if (!sqlTileWriter.purgeCache()) {
            return false
        }

        SqlTileWriter() // Recreates the Database. Required because purgeCache Deletes the whole database but does not recreate

        return true
    }
}