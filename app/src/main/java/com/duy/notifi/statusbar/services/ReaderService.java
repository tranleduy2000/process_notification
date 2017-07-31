/* 
 * 2010-2015 (C) Antonio Redondo
 * http://antonioredondo.com
 * https://github.com/AntonioRedondo/AnotherMonitor
 *
 * Code under the terms of the GNU General Public License v3.
 *
 */

package com.duy.notifi.statusbar.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.monitor.CpuIconData;
import com.duy.notifi.statusbar.data.monitor.RamIconData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderService extends Service {

    public static final String ACTION_START = "com.duy.notifi.READER_START";
    public static final String ACTION_STOP = "com.duy.notifi.READER_STOP";
    private static final String TAG = "ReaderService";
    private int intervalRead;
    private int intervalUpdate;
    private int intervalWidth;
    private List<Integer> memoryActivityManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mPrefs;
    private Thread readThread;
    private AtomicBoolean canRead = new AtomicBoolean(false);
    private long totalBefore, workBefore;
    private Runnable readRunnable;

    public ReaderService() {
        readRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run() called");
                while (canRead.get()) {
                    collectData();
                    try {
                        Thread.sleep(intervalRead);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

        };
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");

        int maxSamples = 2000;
        memoryActivityManager = new ArrayList<>(maxSamples);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mPrefs = getSharedPreferences(getString(R.string.app_name) + Constants.prefs, MODE_PRIVATE);
        intervalRead = mPrefs.getInt(Constants.intervalRead, Constants.defaultIntervalRead);
        intervalUpdate = mPrefs.getInt(Constants.intervalUpdate, Constants.defaultIntervalUpdate);
        intervalWidth = mPrefs.getInt(Constants.intervalWidth, Constants.defaultIntervalWidth);


//        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public void onDestroy() {
        canRead.set(false);
        try {
            readThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (this) {
            readThread = null;
            notify();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceReaderDataBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    canRead.set(true);
                    readThread = new Thread(readRunnable, Constants.readThread);
                    readThread.start();
                    break;
                case ACTION_STOP:
                    canRead.set(false);
                    break;
            }
        }
        return START_STICKY;
    }

    private void collectData() {
        try {
            readRamInfo();
            readCpuInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readCpuInfo() throws IOException {
//			CPU usage percents calculation. It is possible negative values or values higher than 100% may appear.
//			http://stackoverflow.com/questions/1420426
//			http://kernel.org/doc/Documentation/filesystems/proc.txt
        BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
        String[] args = reader.readLine().split("[ ]+", 9);
        long work = Long.parseLong(args[1]) + Long.parseLong(args[2]) + Long.parseLong(args[3]);
        long total = work + Long.parseLong(args[4]) + Long.parseLong(args[5]) + Long.parseLong(args[6]) + Long.parseLong(args[7]);
        reader.close();


        float percentage = 0;
        if (totalBefore != 0) {
            long totalT = total - totalBefore;
            long workT = work - workBefore;
            percentage = restrictPercentage(workT * 100 / (float) totalT);
            Log.d(TAG, "readCpuInfo percentage = " + percentage);
        }
        totalBefore = total;
        workBefore = work;
        reader.close();

        updateCpuInfo(percentage);
    }


    private float restrictPercentage(float percentage) {
        if (percentage > 100)
            return 100;
        else if (percentage < 0)
            return 0;
        else return percentage;
    }

    private void readRamInfo() {

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);

        long memTotal = memoryInfo.totalMem / 1024;
        long memoryUsed = memTotal - memoryInfo.availMem / 1024;
        long memoryAvailable = memoryInfo.availMem / 1024;

        Log.d(TAG, "read memoryUsed = " + memoryUsed);
        Log.d(TAG, "read memoryAvailable = " + memoryAvailable);
        Log.d(TAG, "read totalMem = " + memTotal);

        updateRamInfo(memoryInfo);
    }

    private void updateRamInfo(ActivityManager.MemoryInfo memoryInfo) {
        //update ram notification
        Intent intent = new Intent(RamIconData.ACTION_UPDATE_RAM);
        intent.putExtra(RamIconData.EXTRA_INFO, memoryInfo);
        sendBroadcast(intent);
    }


    private void updateCpuInfo(float percentage) {
        //update cpu notification
        Intent intent = new Intent(CpuIconData.ACTION_UPDATE_CPU);
        intent.putExtra(CpuIconData.EXTRA_PERCENT, percentage);
        sendBroadcast(intent);
    }

    void setIntervals(int intervalRead, int intervalUpdate, int intervalWidth) {
        this.intervalRead = intervalRead;
        this.intervalUpdate = intervalUpdate;
        this.intervalWidth = intervalWidth;
    }

    int getIntervalRead() {
        return intervalRead;
    }

    int getIntervalUpdate() {
        return intervalUpdate;
    }

    int getIntervalWidth() {
        return intervalWidth;
    }

    List<Integer> getMemoryActivityManager() {
        return memoryActivityManager;
    }


    private class ServiceReaderDataBinder extends Binder {
        ReaderService getService() {
            return ReaderService.this;
        }
    }
}
