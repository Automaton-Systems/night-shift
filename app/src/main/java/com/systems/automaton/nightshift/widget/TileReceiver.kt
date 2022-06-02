/* Copyright (c) 2017-12-01  cj
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.systems.automaton.nightshift.widget

import android.annotation.TargetApi
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

import com.systems.automaton.nightshift.Command
import com.systems.automaton.nightshift.filterIsOn
import com.systems.automaton.nightshift.filterIsOnChanged
import com.systems.automaton.nightshift.EventBus

import org.greenrobot.eventbus.Subscribe

@TargetApi(24)
class TileReceiver : TileService() {

    override fun onStartListening() {
        EventBus.register(this)
        updateState()
    }

    override fun onClick() {
        super.onClick()
        Command.toggle(!filterIsOn)
    }

    override fun onStopListening() {
        EventBus.unregister(this)
    }

    @Subscribe fun onFilterIsOnChanged(event: filterIsOnChanged) {
        updateState()
    }

    private fun updateState() = qsTile.run {
        state = if (filterIsOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        updateTile()
    }
}
