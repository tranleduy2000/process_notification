package com.duy.notifi.statusbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.services.ReaderService;
import com.duy.notifi.statusbar.services.StatusService;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;

/**
 * Created by Duy on 03-Aug-17.
 */

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindView();
    }

    private void bindView() {
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
