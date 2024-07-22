package com.example.expensetrackingpro

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Register a listener for preference changes
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Set initial summaries for preferences
        updateSummaries(sharedPreferences)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the listener to prevent memory leaks
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Update summaries when preferences change
        updateSummaries(sharedPreferences)

        // Handle mode changes
        if (key == "modes") {
            applyModeChanges(sharedPreferences)
        }
        // Handle language changes
        if (key == "language") {
            applyLanguageChanges(sharedPreferences)
        }
    }

    private fun updateSummaries(sharedPreferences: SharedPreferences?) {
        // Update summaries for preferences
        val notifyPreference = findPreference<Preference>("notify")
        notifyPreference?.summary = if (sharedPreferences?.getBoolean("notify", true) == true) "Notifications are enabled" else "Notifications are disabled"

        // Update summaries for other preferences as needed
    }

    private fun applyModeChanges(sharedPreferences: SharedPreferences?) {
        // Apply mode changes here
        val selectedMode = sharedPreferences?.getString("modes", "Light")

        if (selectedMode == "Dark") {
            // Apply dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Apply light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun applyLanguageChanges(sharedPreferences: SharedPreferences?) {
        // Apply language changes
        val selectedLanguage = sharedPreferences?.getString("language", "English")
        val locale = getLocaleFromLanguage(selectedLanguage)
        // Update the app's locale with the selected language
        updateLocale(locale)
        // Set the default night mode before recreating the activity
        applyModeChanges(sharedPreferences)

        // Restart the activity only if necessary (e.g., configuration changes)
        if (requireContext().resources.configuration.locale != locale) {
            requireActivity().recreate()
        }
    }


    private fun getLocaleFromLanguage(language: String?): Locale {
        // Convert language name to Locale object
        return when (language) {
            "English" -> Locale("en")
            "Korean" -> Locale("ko")
            "French" -> Locale("fr")
            "Spanish" -> Locale("es")
            "German" -> Locale("de")
            "Japanese" -> Locale("ja")
            "Turkish" -> Locale("tr")
            "Italian" -> Locale("it")
            "Arabic" -> Locale("ar")
            else -> Locale("en") // Default to English if language not found
        }
    }


    @Suppress("DEPRECATION")
    private fun updateLocale(locale: Locale) {
        // Update the app's locale with the selected language
        val resources = requireContext().resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        val displayMetrics = resources.displayMetrics
        resources.updateConfiguration(configuration, displayMetrics)
    }

}