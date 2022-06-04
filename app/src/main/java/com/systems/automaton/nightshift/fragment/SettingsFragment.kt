/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.systems.automaton.nightshift.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.systems.automaton.nightshift.Command

import com.systems.automaton.nightshift.Config
import com.systems.automaton.nightshift.EventBus
import com.systems.automaton.nightshift.getColor
import com.systems.automaton.nightshift.helper.Logger
import com.systems.automaton.nightshift.helper.Permission
import com.systems.automaton.nightshift.inActivePeriod
import com.systems.automaton.nightshift.locationAccessDenied
import com.systems.automaton.nightshift.locationChanged
import com.systems.automaton.nightshift.locationService
import com.systems.automaton.nightshift.pref
import com.systems.automaton.nightshift.R
import com.systems.automaton.nightshift.ads.AdManager
import com.systems.automaton.nightshift.ads.BillingManager
import com.systems.automaton.nightshift.ads.getActivity
import com.systems.automaton.nightshift.scheduleChanged
import com.systems.automaton.nightshift.service.LocationUpdateService
import com.systems.automaton.nightshift.useLocationChanged

import com.topjohnwu.superuser.Shell
import org.greenrobot.eventbus.Subscribe
import org.libreshift.preferences.TimePreference
import org.libreshift.preferences.TimePreferenceDialogFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {
    private val removeAdsPref: Preference
        get() = pref(R.string.pref_remove_ads) as Preference

    private val automaticTurnOnPref: TimePreference
        get() = pref(R.string.pref_key_start_time) as TimePreference

    private val automaticTurnOffPref: TimePreference
        get() = pref(R.string.pref_key_stop_time) as TimePreference

    private val locationPref: Preference?
        get() = pref(R.string.pref_key_location)

    private val secureSuspendPref: Preference?
        get() = pref(R.string.pref_key_secure_suspend_header)

    private val themePref: SwitchPreference
        get() = pref(R.string.pref_key_dark_theme) as SwitchPreference

    private val rootPref: SwitchPreference
        get() = pref(R.string.pref_key_use_root) as SwitchPreference

    private var mSnackbar: Snackbar? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        if (AdManager.instance.isDisabled) {
            removeAdsPref.title = getString(R.string.pref_title_remove_ads_removed)
            removeAdsPref.summary = ""
            removeAdsPref.setOnPreferenceClickListener {
                Toast.makeText(context, getString(R.string.thank_you_purchase), Toast.LENGTH_SHORT).show()
                true
            }
        } else {
            removeAdsPref.setOnPreferenceClickListener {
                context?.getActivity()?.let {
                    BillingManager.instance.buy(it)
                }
                true
            }
        }

        themePref.setOnPreferenceChangeListener { _, newValue ->
            val theme = when (newValue as Boolean) {
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(theme)
            true
        }

        secureSuspendPref?.setOnPreferenceClickListener {
            fragmentManager?.beginTransaction()?.let { t ->
                t.replace(R.id.fragment_container, SecureSuspendFragment())
                t.addToBackStack(null)
                t.commit()
            }
            true
        }

        automaticTurnOnPref.neutralButtonListener =
            TimePreference.OnNeutralButtonPressListener { dialog ->
                Config.startAtSunset = true
                dialog.dismiss()
            }

        automaticTurnOnPref.setOnPreferenceChangeListener { _, _ ->
            Config.startAtSunset = false
            true
        }

        automaticTurnOffPref.neutralButtonListener =
            TimePreference.OnNeutralButtonPressListener { dialog ->
                Config.stopAtSunrise = true
                dialog.dismiss()
            }

        automaticTurnOffPref.setOnPreferenceChangeListener { _, _ ->
            Config.stopAtSunrise = false
            true
        }

        updatePrefs()
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.activity_settings)
        updatePrefs()
        EventBus.register(this)
        LocationUpdateService.update()
        rootPref.isVisible = Shell.rootAccess()
    }

    override fun onStop() {
        EventBus.unregister(this)
        super.onStop()
    }

    private fun updatePrefs() {
        updateTimePrefs()
        updateLocationPref()
        updateSecureSuspendSummary()
    }

    override fun onDisplayPreferenceDialog(p: Preference) {
        if (p is TimePreference) {
            TimePreferenceDialogFragmentCompat.newInstance(p.key).let {
                it.setTargetFragment(this, 0)
                it.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG)
            }
        } else {
            super.onDisplayPreferenceDialog(p)
        }
    }

    private fun updateLocationPref() {
        val (latitude, longitude, time) = Config.location
        locationPref?.summary = when (time) {
            null -> getString(R.string.location_not_set)
            else -> {
                val lat  = getString(R.string.latitude_short)
                val long = getString(R.string.longitude_short)
                String.format("$lat: %.3f, $long: %.3f", latitude.toFloat(), longitude.toFloat())
            }
        }
        locationPref?.isVisible = Config.useLocation
    }

    private fun updateSecureSuspendSummary() {
        secureSuspendPref?.setSummary(when (Config.secureSuspend) {
            true -> R.string.text_switch_on
            false -> R.string.text_switch_off
        })
    }

    private fun updateTimePrefs() {
        automaticTurnOnPref.summary = timeSummary(
            Config.scheduledStartTime,
            getString(R.string.pref_summary_start_time_sunset), Config.startAtSunset)
        automaticTurnOffPref.summary = timeSummary(
            Config.scheduledStopTime,
            getString(R.string.pref_summary_stop_time_sunrise), Config.stopAtSunrise)
    }

    private fun timeSummary(time: String, format: String, useFormat: Boolean): String {
        val localeTime = TimePreference.Time(time).format(context)
        return if (useFormat) String.format(format, localeTime) else localeTime
    }

    private fun showSnackbar(resId: Int, duration: Int = Snackbar.LENGTH_INDEFINITE) {
        mSnackbar = Snackbar.make(requireView(), getString(resId), duration).apply {
            if (Config.darkThemeFlag) {
                val group = this.view as ViewGroup
                group.setBackgroundColor(getColor(R.color.snackbar_color_dark_theme))
                group.findViewById<TextView>(R.id.snackbar_text)
                        .setTextColor(getColor(R.color.text_color_dark_theme))
            }
        }
        mSnackbar?.show()
    }

    private fun dismissSnackBar() {
        if (mSnackbar?.duration == Snackbar.LENGTH_INDEFINITE) {
            mSnackbar?.dismiss()
        }
    }

    //region presenter
    @Subscribe
    fun onScheduleChanged(event: scheduleChanged) {
        LocationUpdateService.update()
        updatePrefs()
        Command.toggle(Config.scheduleOn && inActivePeriod())
    }

    @Subscribe
    fun onUseLocationChanged(event: useLocationChanged) {
        LocationUpdateService.update()
        updatePrefs()
        Command.toggle(Config.scheduleOn && inActivePeriod())
    }

    @Subscribe
    fun onLocationServiceEvent(service: locationService) {
        Log.i("onLocationEvent: ${service.isSearching}")
        if (!service.isRunning) {
            dismissSnackBar()
        } else if (service.isSearching) {
            showSnackbar(R.string.snackbar_searching_location)
        } else {
            showSnackbar(R.string.snackbar_warning_no_location)
        }
    }

    @Subscribe fun onLocationChanged(event: locationChanged) {
        showSnackbar(R.string.snackbar_location_updated, Snackbar.LENGTH_SHORT)
        updatePrefs()
        Command.toggle(Config.scheduleOn && inActivePeriod())
    }

    @Subscribe
    fun onLocationAccessDenied(event: locationAccessDenied) {
        if (Config.scheduleOn && Config.useLocation) {
            Permission.Location.request(requireActivity())
        }
    }

    @Subscribe
    fun onLocationPermissionDialogClosed(event: Permission.Location) {
        if (!Permission.Location.isGranted) {
            Config.startAtSunset = false
            Config.stopAtSunrise = false
        }
        LocationUpdateService.update()
    }
    //endregion

    companion object : Logger() {
        const val DIALOG_FRAGMENT_TAG = "com.systems.automaton.nightshift.DIALOG"
    }
}
