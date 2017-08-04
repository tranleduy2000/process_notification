package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

public class WifiProgressIcon extends ProgressIcon<WifiProgressIcon.WifiReceiver> {

    private static final String TAG = "WifiProgressIcon";
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    public WifiProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, statusView, progressId);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public WifiReceiver getReceiver() {
        return new WifiReceiver(this);
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    @Override
    public void register() {
        super.register();

        int level = WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5);
        if (level > 0) {
            onProcessUpdate(level, 5);
        }
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.icon_wifi);
    }

    @Override
    public int getIconStyleSize() {
        return 5;
    }

    static class WifiReceiver extends IconUpdateReceiver<WifiProgressIcon> {

        private WifiReceiver(WifiProgressIcon iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(WifiProgressIcon icon, Intent intent) {
            Log.d(TAG, "onReceive() called with: icon = [" + icon + "], intent = [" + intent + "]");

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo == null) networkInfo = icon.connectivityManager.getActiveNetworkInfo();

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                int i = WifiManager.calculateSignalLevel(icon.wifiManager.getConnectionInfo().getRssi(), 5);
                icon.onProcessUpdate(i, 5);
            } else {
                icon.onProcessUpdate(0, 5);
            }
        }
    }
}
