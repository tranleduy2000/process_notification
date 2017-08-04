package com.duy.notifi.statusbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.services.ReaderService;
import com.duy.notifi.statusbar.services.StatusService;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;

import static com.duy.notifi.statusbar.data.monitor.ProgressIcon.DEF_ENABLE;
import static com.duy.notifi.statusbar.data.monitor.ProgressIcon.DEF_TYPE;
import static com.duy.notifi.statusbar.data.monitor.ProgressIcon.ENABLE_IDS;
import static com.duy.notifi.statusbar.data.monitor.ProgressIcon.SPINNER_IDS;

/**
 * Created by Duy on 03-Aug-17.
 */

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (!StaticUtils.isPermissionsGranted(this)
                || !StaticUtils.isIgnoringOptimizations(this)
                || !StaticUtils.canDrawOverlays(this)) {
            startActivity(new Intent(this, StartActivity.class));
        }

        setupSwitch();
        setupProgress();
    }

    private void setupProgress() {


        for (int i = 0; i < ENABLE_IDS.length; i++) {
            boolean progressActive = PreferenceUtils.isProgressActive(this, i, DEF_ENABLE[i]);
            SwitchCompat enable = (SwitchCompat) findViewById(ENABLE_IDS[i]);
            enable.setChecked(progressActive);
            final int finalI = i;
            enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean active) {
                    PreferenceUtils.setActive(SettingActivity.this, finalI, active);
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(SettingActivity.this, StatusService.class);
                    startService(intent);
                }
            });

            Spinner spinner = (Spinner) findViewById(SPINNER_IDS[i]);
            int pos = PreferenceUtils.getProgressType(SettingActivity.this, finalI, DEF_TYPE[i]);
            spinner.setSelection(pos);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int type, long l) {
                    PreferenceUtils.setProgressType(SettingActivity.this, finalI, type);
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(SettingActivity.this, StatusService.class);
                    startService(intent);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void setupSwitch() {
        //setup button enable service
        Boolean enabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED);
        SwitchCompat service = (SwitchCompat) findViewById(R.id.serviceEnabled);
        service.setChecked((enabled != null && enabled) || StaticUtils.isStatusServiceRunning(this));
        service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PreferenceUtils.putPreference(SettingActivity.this,
                            PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, true);
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(SettingActivity.this, StatusService.class);
                    startService(intent);

                    intent = new Intent(ReaderService.ACTION_START);
                    intent.setClass(SettingActivity.this, ReaderService.class);
                    startService(intent);
                } else {
                    PreferenceUtils.putPreference(SettingActivity.this,
                            PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, false);
                    Intent intent = new Intent(StatusService.ACTION_STOP);
                    intent.setClass(SettingActivity.this, StatusService.class);
                    stopService(intent);

                    intent = new Intent(ReaderService.ACTION_STOP);
                    intent.setClass(SettingActivity.this, ReaderService.class);
                    startService(intent);
                }
            }
        });


    }
}
