package com.duy.notifi.statusbar.data.icon;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.GroupProgressView;

/**
 * Created by Duy on 31-Jul-17.
 */

public class RamProgressIcon extends ProgressIcon<RamProgressIcon.RamReceiver> {

    public static final String ACTION_UPDATE_RAM = "com.duy.notifi.ACTION_UPDATE_RAM";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_INFO = "memory_info";
    private static final String TAG = "RamIconData";

    private GroupProgressView statusView;

    public RamProgressIcon(Context context, GroupProgressView statusView, int progressId) {
        super(context, statusView, progressId);
        this.statusView = statusView;
    }

    @Override
    public RamReceiver getReceiver() {
        return new RamReceiver(this);
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
                    int total = (int) (memoryInfo.totalMem / 1024);
                    int current = (int) (total - memoryInfo.availMem / 1024);
                    icon.onProcessUpdate(current, total);
                }
            }
        }
    }

}
