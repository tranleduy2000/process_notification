package com.duy.notifi.statusbar.data.icon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */
public class TrafficDownProgressIcon extends ProgressIcon<TrafficDownProgressIcon.BatteryReceiver> {

    public static final String ACTION_UPDATE_TRAFFIC_DOWN = "com.duy.notifi.ACTION_UPDATE_TRAFFIC_DOWN";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "TrafficDownProgressIcon";



    public TrafficDownProgressIcon(Context context, StatusView statusView, int progressId) {
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
        return new IntentFilter(ACTION_UPDATE_TRAFFIC_DOWN);
    }

    public static class BatteryReceiver extends IconUpdateReceiver<TrafficDownProgressIcon> {

        public BatteryReceiver(TrafficDownProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(TrafficDownProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_TRAFFIC_DOWN)) {
                    int percent = intent.getIntExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
