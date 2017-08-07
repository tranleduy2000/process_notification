package com.duy.notifi.statusbar.data.icon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.View;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.GroupProgressView;

/**
 * Created by Duy on 31-Jul-17.
 */
public class BatteryLevelProgressIcon extends ProgressIcon<BatteryLevelProgressIcon.BatteryReceiver> {

    public static final String ACTION_UPDATE_BATTERY = "com.duy.notifi.ACTION_UPDATE_BATTERY";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "BatteryProgressIcon";


    public BatteryLevelProgressIcon(Context context, GroupProgressView statusView, int progressId) {
        super(context, statusView, progressId);
    }

    @Override
    public BatteryReceiver getReceiver() {
        return new BatteryReceiver(this);
    }

    @Override
    public void register() {
        super.register();
        //init first value
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        int percent = (int) (batteryPct * 100);
        onProcessUpdate(percent, 100);
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
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float) scale;

                int percent = (int) (batteryPct * 100);
                icon.onProcessUpdate(percent, 100);
            }
        }
    }
}
