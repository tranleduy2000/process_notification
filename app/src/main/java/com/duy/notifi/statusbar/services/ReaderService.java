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
import com.duy.notifi.statusbar.data.icon.CpuProgressIcon;
import com.duy.notifi.statusbar.data.icon.CpuTempProgressIcon;
import com.duy.notifi.statusbar.data.icon.ExternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.InternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.RamProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficDownProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficUpDownProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficUpProgressIcon;
import com.duy.notifi.statusbar.data.monitor.CpuUtil;
import com.duy.notifi.statusbar.data.monitor.StorageUtil;
import com.duy.notifi.statusbar.data.monitor.TrafficManager;
import com.duy.notifi.statusbar.utils.PreferenceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderService extends Service {

    public static final String ACTION_START = "com.duy.notifi.READER_START";
    public static final String ACTION_STOP = "com.duy.notifi.READER_STOP";
    private static final String TAG = "ReaderService";
    private int intervalRead;
    private List<Integer> memoryActivityManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mPrefs;
    private Thread readThread;
    private AtomicBoolean canRead = new AtomicBoolean(false);
    private Runnable readRunnable;
    private TrafficManager mTrafficManager;

    public ReaderService() {
        readRunnable = new Runnable() {
            @Override
            public void run() {
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
        mTrafficManager = new TrafficManager();

        int maxSamples = 2000;
        memoryActivityManager = new ArrayList<>(maxSamples);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mPrefs = getSharedPreferences(getString(R.string.app_name) + Constants.prefs, MODE_PRIVATE);
        intervalRead = mPrefs.getInt(Constants.intervalRead, Constants.defaultIntervalRead);
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
            readRamUsage();
            readCpuUsage();
            readCpuTemp();
            readInternalState();
            readExternalState();
            readNetUpDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readCpuTemp() {
        try {
            float currentTemp = CpuUtil.readTemp();
            int percent = (int) (currentTemp / PreferenceUtils.getMaxCpuTemp(this) * 100f);
            Intent intent = new Intent(CpuTempProgressIcon.ACTION_UPDATE_CPU_TEMP);
            intent.putExtra(CpuTempProgressIcon.EXTRA_PERCENT, percent);
            this.sendBroadcast(intent);
        } catch (Exception e) {

        }
    }

    private void readNetUpDown() {
        if (mTrafficManager.canRead()) {
            float trafficDown = mTrafficManager.getTrafficDown();
            long max = PreferenceUtils.getMaxNetDown(this);
            int percent = (int) (trafficDown / max * 100f);
            Intent intent = new Intent(TrafficDownProgressIcon.ACTION_UPDATE_TRAFFIC_DOWN);
            intent.putExtra(ExternalStorageProgressIcon.EXTRA_PERCENT, percent);
            this.sendBroadcast(intent);

            float trafficUp = mTrafficManager.getTrafficUpLoad();
            max = PreferenceUtils.getMaxNetUp(this);
            percent = (int) (trafficUp / max * 100f);
            intent = new Intent(TrafficUpProgressIcon.ACTION_UPDATE_TRAFFIC_UP);
            intent.putExtra(TrafficUpProgressIcon.EXTRA_PERCENT, percent);
            this.sendBroadcast(intent);

            max = Math.max(PreferenceUtils.getMaxNetUp(this), PreferenceUtils.getMaxNetDown(this));
            percent = (int) ((trafficUp + trafficDown) / max * 100f);
            intent = new Intent(TrafficUpDownProgressIcon.ACTION_UPDATE_TRAFFIC_UP_DOWN);
            intent.putExtra(TrafficUpDownProgressIcon.EXTRA_PERCENT, percent);
            this.sendBroadcast(intent);
        }
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


    private void readCpuUsage() throws IOException {
        try {
            int percentage = (int) (CpuUtil.readUsage() * 100);
            Intent intent = new Intent(CpuProgressIcon.ACTION_UPDATE_CPU);
            intent.putExtra(CpuProgressIcon.EXTRA_PERCENT, percentage);
            intent.putExtra(CpuProgressIcon.EXTRA_CPU_INDEX, 0);
            sendBroadcast(intent);
        } catch (Exception e) {
        }
    }

    private void readRamUsage() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);

        //update ram notification
        Intent intent = new Intent(RamProgressIcon.ACTION_UPDATE_RAM);
        intent.putExtra(RamProgressIcon.EXTRA_INFO, memoryInfo);
        sendBroadcast(intent);
    }


    private class ServiceReaderDataBinder extends Binder {
        ReaderService getService() {
            return ReaderService.this;
        }
    }

}
