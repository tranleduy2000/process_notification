package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */

public class BatteryProgressIcon extends ProgressIcon<BatteryProgressIcon.BatteryReceiver> {

    public static final String ACTION_UPDATE_BATTERY = "com.duy.notifi.ACTION_UPDATE_BATTERY";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "BatteryProgressIcon";

    private int process;
    private StatusView statusView;

    public BatteryProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, progressId);
        this.statusView = statusView;
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
    public void onProcessUpdate(long current, long max) {
        Log.d(TAG, "onProcessUpdate() called with: current = [" + current + "], max = [" + max + "]");
        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view;
            float factor = ((float) current) / max;
            progressBar.setMax(100);
            progressBar.setProgress((int) (factor * 100));
        }
    }

    @Override
    public View getIconView() {
        if (statusView != null && view == null) {
            view = statusView.findViewById(R.id.progress_1);
            if (view == null) {
                LinearLayout child = this.statusView.getStatusView();
                view = child.findViewById(R.id.progress_1);
            }
        }
        return view;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_UPDATE_BATTERY);
    }

    public static class BatteryReceiver extends IconUpdateReceiver<BatteryProgressIcon> {

        public BatteryReceiver(BatteryProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(BatteryProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_BATTERY)) {
                    int percent = (int) intent.getFloatExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
