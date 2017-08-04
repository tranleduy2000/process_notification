package com.duy.notifi.statusbar.data.icon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.View;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */
public class BatteryLevelProgressIcon extends ProgressIcon<BatteryLevelProgressIcon.BatteryReceiver> {

    public static final String ACTION_UPDATE_BATTERY = "com.duy.notifi.ACTION_UPDATE_BATTERY";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "BatteryProgressIcon";


    public BatteryLevelProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, statusView, progressId);
    }

    @Override
    public BatteryReceiver getReceiver() {
        return new BatteryReceiver(this);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void unregister() {
        super.unregister();
    }


    @Override
    public View initView() {
        return super.initView();
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    public static class BatteryReceiver extends IconUpdateReceiver<BatteryLevelProgressIcon> {

        public BatteryReceiver(BatteryLevelProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(BatteryLevelProgressIcon icon, Intent intent) {
            if (intent != null) {
                int percent = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (percent != -1) {
                    icon.onProcessUpdate(percent, 100);
                }
            }
        }
    }
}
