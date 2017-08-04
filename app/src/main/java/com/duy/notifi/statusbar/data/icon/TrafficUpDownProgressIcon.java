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
public class TrafficUpDownProgressIcon extends ProgressIcon<TrafficUpDownProgressIcon.TrafficReceiver> {

    public static final String ACTION_UPDATE_TRAFFIC_UP_DOWN = "com.duy.notifi.ACTION_UPDATE_TRAFFIC_UP_DOWN";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "TrafficUpDownProgressIcon";



    public TrafficUpDownProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context,statusView, progressId);
    }

    @Override
    public TrafficReceiver getReceiver() {
        return new TrafficReceiver(this);
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
        return new IntentFilter(ACTION_UPDATE_TRAFFIC_UP_DOWN);
    }

    public static class TrafficReceiver extends IconUpdateReceiver<TrafficUpDownProgressIcon> {

        public TrafficReceiver(TrafficUpDownProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(TrafficUpDownProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_TRAFFIC_UP_DOWN)) {
                    int percent = intent.getIntExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
