package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.icon.IconData;
import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */

public class CpuIconData extends IconData<CpuIconData.CpuReceiver> {

    public static final String ACTION_UPDATE_CPU = "com.duy.notifi.ACTION_UPDATE_CPU";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "CpuIconData";

    private int process;
    private StatusView statusView;

    public CpuIconData(Context context, StatusView statusView) {
        super(context);
        this.statusView = statusView;
    }

    @Override
    public CpuReceiver getReceiver() {
        return new CpuReceiver(this);
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
        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        return view;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_UPDATE_CPU);
    }

    public static class CpuReceiver extends IconUpdateReceiver<CpuIconData> {

        public CpuReceiver(CpuIconData iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(CpuIconData icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_CPU)) {
                    int percent = (int) intent.getFloatExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
