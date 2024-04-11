package org.gittner.osmbugs.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.gittner.osmbugs.R
import org.gittner.osmbugs.keepright.KeeprightSelectErrorsDialog
import org.gittner.osmbugs.osmnotes.OsmNotesLoginActivity
import org.gittner.osmbugs.osmose.OsmoseSelectErrorsDialog
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.modules.SqlTileWriter


class SettingsFragment : PreferenceFragmentCompat() {
    private val mViewModel: ErrorViewModel by inject()

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
            startActivity(Intent(context, OsmNotesLoginActivity::class.java))
            true
        }

        findPreference<Preference>(getString(R.string.pref_clear_tile_cache))?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (clearTileCache()) {
                Toast.makeText(requireContext(), R.string.tile_cache_cleared, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), R.string.tile_cache_cleared_failed, Toast.LENGTH_LONG).show()
            }

            true
        }

        findPreference<EditTextPreference>(getString(R.string.pref_tile_cache_ttl_override))?.setOnPreferenceChangeListener { _, newValue ->
            Configuration.getInstance().expirationOverrideDuration = newValue.toString().toLong() * 1000

            clearTileCache()

            true
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