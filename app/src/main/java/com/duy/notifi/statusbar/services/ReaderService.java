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
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.icon.BatteryProgressIcon;
import com.duy.notifi.statusbar.data.icon.CpuProgressIcon;
import com.duy.notifi.statusbar.data.icon.ExternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.InternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.RamProgressIcon;
import com.duy.notifi.statusbar.data.monitor.CpuUtil;
import com.duy.notifi.statusbar.utils.StorageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.duy.notifi.statusbar.services.Constants.percent;

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
            readCpuFreq();
            readBattery();
            readInternalState();
            readExternalState();
            readNetUpDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void readNetUpDown() {
//        TrafficUtils.getTrafficDownload()
        Intent intent = new Intent(ExternalStorageProgressIcon.ACTION_UPDATE_EXTERNAL_STORAGE);
        intent.putExtra(ExternalStorageProgressIcon.EXTRA_PERCENT, percent);
        this.sendBroadcast(intent);
    }

    private void readExternalState() {
        if (StorageUtil.hasExternalMemory()) {
            StorageUtil.Storage storage = StorageUtil.getExternalMemory();
            int percent = storage.getPercent();
            Intent intent = new Intent(ExternalStorageProgressIcon.ACTION_UPDATE_EXTERNAL_STORAGE);
            intent.putExtra(ExternalStorageProgressIcon.EXTRA_PERCENT, percent);
            this.sendBroadcast(intent);
        }
    }

    private void readInternalState() {
        StorageUtil.Storage storage = StorageUtil.getInternalMemory();
        int percent = storage.getPercent();
        Intent intent = new Intent(InternalStorageProgressIcon.ACTION_UPDATE_INTERNAL_STORAGE);
        intent.putExtra(InternalStorageProgressIcon.EXTRA_PERCENT, percent);
        this.sendBroadcast(intent);
    }

    private void readBattery() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        int percent = (int) (batteryPct * 100);
        Intent intent = new Intent(BatteryProgressIcon.ACTION_UPDATE_BATTERY);
        intent.putExtra(BatteryProgressIcon.EXTRA_PERCENT, percent);
        this.sendBroadcast(intent);
    }

    private void readCpuFreq() throws IOException {
        try {
//            String maxFreq = CpuUtil.getMaxFreq(0);
//            String currentFreq = CpuUtil.getCurrentFreq(0);
////            String currentFreq = CpuUtil.getCurrentCPULoad();
//            Long max = Long.parseLong(maxFreq.trim());
//            Long current = Long.parseLong(currentFreq.trim());
//            int percentage = (int) ((float) current / (float) max) * 100;
            //update cpu notification
            int percentage = (int) (CpuUtil.readUsage() * 100);
            Intent intent = new Intent(CpuProgressIcon.ACTION_UPDATE_CPU);
            intent.putExtra(CpuProgressIcon.EXTRA_PERCENT, percentage);
            intent.putExtra(CpuProgressIcon.EXTRA_CPU_INDEX, 0);
            sendBroadcast(intent);
        } catch (Exception e) {
        }
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
        Intent intent = new Intent(RamProgressIcon.ACTION_UPDATE_RAM);
        intent.putExtra(RamProgressIcon.EXTRA_INFO, memoryInfo);
        sendBroadcast(intent);
    }


    private void updateCpuInfo(float percentage) {

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
