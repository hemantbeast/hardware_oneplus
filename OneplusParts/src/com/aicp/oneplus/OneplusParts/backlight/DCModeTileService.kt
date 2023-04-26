package com.aicp.oneplus.OneplusParts.backlight

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.Utils


class DCModeTileService : TileService() {
    private var enabled = false

    override fun onStartListening() {
        super.onStartListening()
        enabled = DCModeSwitch.isCurrentlyEnabled(this)
        setTile()
    }

    override fun onClick() {
        super.onClick()
        enabled = DCModeSwitch.isCurrentlyEnabled(this)
        Utils.writeValue(DCModeSwitch.getFile(this), if (enabled) "0" else "1")
        Settings.System.putString(contentResolver, DCModeSwitch.SETTINGS_KEY, if (enabled) "0" else "1")
        setTile()
    }

    private fun setTile() {
        qsTile.icon = Icon.createWithResource(
            this,
            if (enabled) R.drawable.ic_dimming_off else R.drawable.ic_dimming_on
        )
        qsTile.state = if (enabled) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        qsTile.updateTile()
    }
}