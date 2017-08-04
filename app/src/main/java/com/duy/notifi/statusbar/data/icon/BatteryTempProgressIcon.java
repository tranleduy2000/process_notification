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
public class BatteryTempProgressIcon extends ProgressIcon<BatteryTempProgressIcon.BatteryReceiver> {
    private static final String TAG = "BatteryTempProgressIcon";

    public BatteryTempProgressIcon(Context context, StatusView statusView, int progressId) {
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

    public static class BatteryReceiver extends IconUpdateReceiver<BatteryTempProgressIcon> {

        public BatteryReceiver(BatteryTempProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(BatteryTempProgressIcon icon, Intent intent) {
            if (intent != null) {
                int percent = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                if (percent != -1) {
                    icon.onProcessUpdate(percent, 100);
                }
            }
        }
    }

}
