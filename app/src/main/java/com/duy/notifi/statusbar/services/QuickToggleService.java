package com.duy.notifi.statusbar.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;

@TargetApi(24)
public class QuickToggleService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setIcon(Icon.createWithResource(this, StaticUtils.isStatusServiceRunning(this) ? R.drawable.ic_check_box_enabled : R.drawable.ic_check_box_disabled));
            tile.updateTile();
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setIcon(Icon.createWithResource(this, StaticUtils.isStatusServiceRunning(this) ? R.drawable.ic_check_box_enabled : R.drawable.ic_check_box_disabled));
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        Tile tile = getQsTile();
        if (tile != null) {
            if (StaticUtils.isStatusServiceRunning(this)) {
                PreferenceUtils.putPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, false);

                Intent intent = new Intent(ProgressStatusService.ACTION_STOP);
                intent.setClass(this, ProgressStatusService.class);
                stopService(intent);

                tile.setIcon(Icon.createWithResource(this, R.drawable.ic_check_box_disabled));
            } else {
                PreferenceUtils.putPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, true);

                Intent intent = new Intent(ProgressStatusService.ACTION_START);
                intent.setClass(this, ProgressStatusService.class);
                startService(intent);

                tile.setIcon(Icon.createWithResource(this, R.drawable.ic_check_box_enabled));
            }

            tile.updateTile();
        }
    }
}
