package com.duy.notifi.statusbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.adapters.SimplePagerAdapter;
import com.duy.notifi.statusbar.fragments.ProgressTypeFragment;
import com.duy.notifi.statusbar.fragments.SettingFragment;
import com.duy.notifi.statusbar.services.ProgressStatusService;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;

/**
 * Created by Duy on 03-Aug-17.
 */

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SimplePagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!StaticUtils.isAccessibilityServiceRunning(this)
                || !StaticUtils.isPermissionsGranted(this)
                || !StaticUtils.isIgnoringOptimizations(this)
                || !StaticUtils.canDrawOverlays(this)) {
            startActivity(new Intent(this, StartActivity.class));
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new SimplePagerAdapter(this, getSupportFragmentManager(), viewPager,
                ProgressTypeFragment.newInstance(), SettingFragment.newInstance());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupSwitch();
    }

    private void setupSwitch() {
        //setup button enable service
        SwitchCompat service = (SwitchCompat) findViewById(R.id.serviceEnabled);
        service.setChecked(StaticUtils.isStatusServiceRunning(MainActivity.this));
        service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PreferenceUtils.putPreference(MainActivity.this,
                            PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, true);
                    Intent intent = new Intent(ProgressStatusService.ACTION_START);
                    intent.setClass(MainActivity.this, ProgressStatusService.class);
                    startService(intent);
                } else {
                    PreferenceUtils.putPreference(MainActivity.this,
                            PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED, false);
                    Intent intent = new Intent(ProgressStatusService.ACTION_STOP);
                    intent.setClass(MainActivity.this, ProgressStatusService.class);
                    stopService(intent);
                }
            }
        });


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
