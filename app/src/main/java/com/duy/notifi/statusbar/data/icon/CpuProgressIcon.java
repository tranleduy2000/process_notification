package com.duy.notifi.statusbar.data.icon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.GroupProgressView;

/**
 * Created by Duy on 31-Jul-17.
 */

public class CpuProgressIcon extends ProgressIcon<CpuProgressIcon.CpuReceiver> {

    public static final String ACTION_UPDATE_CPU = "com.duy.notifi.ACTION_UPDATE_CPU";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    public static final String EXTRA_CPU_INDEX = "cpu_index";
    private static final String TAG = "CpuIconData";

    private GroupProgressView statusView;

    public CpuProgressIcon(Context context, GroupProgressView statusView, int progressId) {
        super(context, statusView, progressId);
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
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_UPDATE_CPU);
    }


    public static class CpuReceiver extends IconUpdateReceiver<CpuProgressIcon> {

        public CpuReceiver(CpuProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(CpuProgressIcon icon, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_UPDATE_CPU)) {
                    int percent = (int) intent.getIntExtra(EXTRA_PERCENT, -1);
                    if (percent != -1) {
                        icon.onProcessUpdate(percent, 100);
                    }
                }
            }
        }
    }

}
