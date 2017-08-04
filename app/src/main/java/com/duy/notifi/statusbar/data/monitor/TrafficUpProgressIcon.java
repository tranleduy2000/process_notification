package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */
public class TrafficUpProgressIcon extends ProgressIcon<TrafficUpProgressIcon.BatteryReceiver> {

    public static final String ACTION_UPDATE_TRAFFIC_UP = "com.duy.notifi.ACTION_UPDATE_TRAFFIC_UP";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "TrafficUpProgressIcon";

    private int process;

    public TrafficUpProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context,statusView, progressId);
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
        return new IntentFilter(ACTION_UPDATE_TRAFFIC_UP);
    }

    public static class BatteryReceiver extends IconUpdateReceiver<TrafficUpProgressIcon> {

        public BatteryReceiver(TrafficUpProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(TrafficUpProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_TRAFFIC_UP)) {
                    int percent = intent.getIntExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
