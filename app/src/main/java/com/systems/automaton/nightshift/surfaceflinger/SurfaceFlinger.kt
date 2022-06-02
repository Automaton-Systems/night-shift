package com.systems.automaton.nightshift.filter.surfaceflinger

import android.graphics.Color
import com.topjohnwu.superuser.Shell
import kotlin.properties.Delegates
import com.systems.automaton.nightshift.Filter
import com.systems.automaton.nightshift.activeProfile
import com.systems.automaton.nightshift.helper.Logger

class SurfaceFlinger() : Filter {
    private var filtering: Boolean by Delegates.observable(false) { _, isOn, turnOn ->
        when {
            !isOn && turnOn -> show()
            isOn && !turnOn -> hide()
            isOn && turnOn -> update()
        }
    }

    private fun show() {

    }

    private fun hide() {
        val call = "service call SurfaceFlinger 1015 i32 1 " +
                "f 1 f  0 f  0 f  0 " +
                "f  0 f 1 f  0 f  0 " +
                "f  0 f  0 f 1 f  0 " +
                "f  0 f  0 f  0 f  1"
        Log.d(call)
        Shell.su(call).exec()
    }

    private fun update() {
        val color = activeProfile.multFilterColor
        val r = Color.red(color) / 255.0f
        val g = Color.green(color) / 255.0f
        val b = Color.blue(color) / 255.0f
        Log.i("Set to $r $g $b")
        val call = "service call SurfaceFlinger 1015 i32 1 " +
                "f $r f  0 f  0 f  0 " +
                "f  0 f $g f  0 f  0 " +
                "f  0 f  0 f $b f  0 " +
                "f  0 f  0 f  0 f  1"
        Log.d(call)
        Shell.su(call).exec()
    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override var profile = activeProfile.off
    set(value) {
        Log.i("profile set to: $value")
        field = value
        filtering = !value.isOff
    }

    companion object : Logger()
}
