/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.systems.automaton.nightshift

import android.os.Bundle
import androidx.fragment.app.Fragment

import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment

class Intro : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStatusBar(false)
        showSkipButton(false)

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_welcome),
                getString(R.string.slide_text_welcome),
                R.mipmap.ic_launcher,
                getColor(R.color.color_primary)))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_protect_eyes),
                getString(R.string.slide_text_protect_eyes),
                R.drawable.intro_slide_2,
                getColor(R.color.color_accent)))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_improve_sleep),
                getString(R.string.slide_text_improve_sleep),
                R.drawable.intro_slide_3,
                getColor(R.color.color_primary_dark)))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_get_started),
                getString(R.string.slide_text_get_started),
                R.drawable.intro_slide_4,
                getColor(R.color.text_disabled)))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        saveAndFinish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        Config.introShown = true
        saveAndFinish()
    }

    private fun saveAndFinish() {
        Config.introShown = true
        finish()
    }
}
