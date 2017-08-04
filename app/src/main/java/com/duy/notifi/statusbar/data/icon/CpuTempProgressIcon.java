package com.duy.notifi.statusbar.data.icon;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.views.StatusView;

/**
 * Created by Duy on 31-Jul-17.
 */
public class CpuTempProgressIcon extends ProgressIcon
        implements SensorEventListener {

    public static final String ACTION_UPDATE_CPU_TEMP = "com.duy.notifi.ACTION_UPDATE_CPU_TEMP";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_USED_VALUE = "used_value";
    public static final String EXTRA_PERCENT = "percent";
    private static final String TAG = "BatteryProgressIcon";


    public CpuTempProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, statusView, progressId);
    }

    @Override
    public IconUpdateReceiver getReceiver() {
        return null;
    }

    @Override
    public void register() {
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void unregister() {
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }


    @Override
    public View initView() {
        return super.initView();
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION_UPDATE_CPU_TEMP);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (values != null) {
            onProcessUpdate((int) values[0], 100);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
