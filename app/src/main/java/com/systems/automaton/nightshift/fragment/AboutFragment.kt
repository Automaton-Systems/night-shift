/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.systems.automaton.nightshift.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.systems.automaton.nightshift.BuildConfig
import com.systems.automaton.nightshift.R
import com.systems.automaton.nightshift.helper.Logger
import com.systems.automaton.nightshift.pref
import com.systems.automaton.nightshift.showChangelog

class AboutFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about, rootKey)
        pref(R.string.pref_key_version)?.let { versionPref ->
            versionPref.summary = BuildConfig.VERSION_NAME
            versionPref.setOnPreferenceClickListener { _ ->
                activity?.let { showChangelog(it) }
                true
            }
        }
    }
    companion object : Logger()
}