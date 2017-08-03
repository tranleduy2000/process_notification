package com.duy.notifi.statusbar.data.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */

public class RamProgressIcon extends ProgressIcon<RamProgressIcon.RamReceiver> {

    public static final String ACTION_UPDATE_RAM = "com.duy.notifi.ACTION_UPDATE_RAM";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_INFO = "memory_info";
    private static final String TAG = "RamIconData";
    private int process;
    private StatusView statusView;

    public RamProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, progressId);
        this.statusView = statusView;
    }

    @Override
    public RamReceiver getReceiver() {
        return new RamReceiver(this);
    }

    @Override
    public View getIconView() {
        if (statusView != null && view == null) {
            view = statusView.findViewById(progressId);
            if (view == null) {
                LinearLayout child = this.statusView.getStatusView();
                view = child.findViewById(progressId);
            }
        }
        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        return view;
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
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_UPDATE_RAM);
    }

    public static class RamReceiver extends IconUpdateReceiver<RamProgressIcon> {

        public RamReceiver(RamProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(RamProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_RAM)) {
                    ActivityManager.MemoryInfo memoryInfo = intent.getParcelableExtra(EXTRA_INFO);
                    icon.onProcessUpdate(memoryInfo.totalMem - memoryInfo.availMem,
                            memoryInfo.totalMem);
                }
            }
        }
    }

}
