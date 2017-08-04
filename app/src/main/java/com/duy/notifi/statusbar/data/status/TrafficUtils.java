/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package com.duy.notifi.statusbar.data.status;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public final class TrafficUtils {

    public static final String TRAFFIC_INFO_PATH = "/proc/uid_stat/";
    public static final String TCP_SEND = "/tcp_snd";
    public static final String TCP_RECEIVE = "/tcp_rcv";
    private static final String TAG = "TrafficUtils";

    private TrafficUtils() {

    }

    public static long getTrafficDownload(String uid) {
        final String rcvPath = TRAFFIC_INFO_PATH + uid + TCP_RECEIVE;
        long rcvTraffic = -1;
        try {
            RandomAccessFile rafRcv = new RandomAccessFile(rcvPath, "r");
            rcvTraffic = Long.parseLong(rafRcv.readLine());
            rafRcv.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getTrafficDownload() returned: " + rcvTraffic);
        return rcvTraffic;
    }

    public static long getTrafficUpLoad(String uid) {
        final String sndPath = TRAFFIC_INFO_PATH + uid + TCP_SEND;
        long sndTraffic = -1;
        try {
            RandomAccessFile rafSnd = new RandomAccessFile(sndPath, "r");
            sndTraffic = Long.parseLong(rafSnd.readLine());
            rafSnd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getTrafficUpLoad() returned: " + sndTraffic);
        return sndTraffic;
    }

    public static long getTrafficInfo(String uid) {
        return getTrafficDownload(uid) + getTrafficUpLoad(uid);
    }
}
