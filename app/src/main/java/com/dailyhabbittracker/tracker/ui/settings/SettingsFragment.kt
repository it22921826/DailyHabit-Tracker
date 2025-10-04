package com.dailyhabbittracker.tracker.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.navigation.fragment.findNavController
import com.dailyhabbittracker.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        // Navigate to About screen
        findPreference<Preference>("about_screen")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.aboutFragment)
            true
        }
    }
}
