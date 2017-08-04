/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package com.duy.notifi.statusbar.data.monitor;

import android.net.TrafficStats;
import android.util.Log;


public class TrafficManager {
    private static final String TAG = "TrafficManager";
    private long mLastTx, mLastRx;

    public TrafficManager() {
        mLastRx = TrafficStats.getTotalRxBytes();
        mLastTx = TrafficStats.getMobileTxBytes();
    }

    public long getTrafficDown() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long rx = totalRxBytes - mLastRx;
        mLastRx = totalRxBytes;
        Log.d(TAG, "getTrafficDown() returned: " + (rx / 1024) + "kb");
        return rx;
    }

    public long getTrafficUpLoad() {
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long tx = totalTxBytes - mLastTx;
        mLastTx = totalTxBytes;
        Log.d(TAG, "getTrafficUpLoad() returned: " + (tx / 1024) + "kb");
        return tx;
    }

    public long getTrafficUpDown() {
        return getTrafficDown() + getTrafficUpLoad();
    }
}
