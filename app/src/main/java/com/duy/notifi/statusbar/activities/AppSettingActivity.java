package com.duy.notifi.statusbar.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.duy.notifi.R;
import com.duy.notifi.statusbar.adapters.ActivityAdapter;
import com.duy.notifi.statusbar.data.AppData;
import com.duy.notifi.statusbar.dialogs.ColorPickerDialog;
import com.duy.notifi.statusbar.dialogs.PreferenceDialog;
import com.duy.notifi.statusbar.utils.ColorUtils;

public class AppSettingActivity extends AppCompatActivity {

    public final static String EXTRA_APP = "com.duy.notifi.EXTRA_APP";
    public final static String EXTRA_ACTIVITY = "com.duy.notifi.EXTRA_ACTIVITY";

    private AppData app;
    private AppData.ActivityData activity;
    private ImageView colorView;

    private ActivityAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Intent intent = getIntent();
        app = intent.getParcelableExtra(EXTRA_APP);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        colorView = (ImageView) findViewById(R.id.colorView);
        SwitchCompat fullscreenSwitch = (SwitchCompat) findViewById(R.id.fullscreenSwitch);
        SwitchCompat notificationSwitch = (SwitchCompat) findViewById(R.id.notificationSwitch);

        toolbar.setTitle(app.label);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Action<Integer>() {
                    @NonNull
                    @Override
                    public String id() {
                        return "activityColorDialog";
                    }

                    @Nullable
                    @Override
                    protected Integer run() throws InterruptedException {
                        return ColorUtils.getPrimaryColor(AppSettingActivity.this, app.getComponentName());
                    }

                    @Override
                    protected void done(@Nullable Integer result) {
                        ColorPickerDialog dialog = new ColorPickerDialog(AppSettingActivity.this);
                        dialog.setPreference(app.getIntegerPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.COLOR));
                        dialog.setDefaultPreference(result != null ? result : ColorUtils.getDefaultColor(AppSettingActivity.this));
                        dialog.setListener(new PreferenceDialog.OnPreferenceListener<Integer>() {
                            @Override
                            public void onPreference(PreferenceDialog dialog, Integer preference) {
                                app.putPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.COLOR, preference);
                                colorView.setImageDrawable(new ColorDrawable(preference));
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancel(PreferenceDialog dialog) {
                            }
                        });
                        dialog.show();
                    }
                }.execute();
            }
        });

        Integer color = app.getIntegerPreference(this, AppData.PreferenceIdentifier.COLOR);
        if (color != null) {
            colorView.setImageDrawable(new ColorDrawable(color));
        } else {
            new Action<Integer>() {
                @NonNull
                @Override
                public String id() {
                    return "activityColor";
                }

                @Nullable
                @Override
                protected Integer run() throws InterruptedException {
                    return ColorUtils.getPrimaryColor(AppSettingActivity.this, app.getComponentName());
                }

                @Override
                protected void done(@Nullable Integer result) {
                    colorView.setImageDrawable(new ColorDrawable(result != null ? result : ColorUtils.getDefaultColor(AppSettingActivity.this)));
                }
            }.execute();
        }

        Boolean isFullscreen = app.getBooleanPreference(this, AppData.PreferenceIdentifier.FULLSCREEN);
        fullscreenSwitch.setChecked(isFullscreen != null && isFullscreen);

        fullscreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.putPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.FULLSCREEN, isChecked);
                adapter.notifyDataSetChanged();
            }
        });

        Boolean isNotifications = app.getSpecificBooleanPreference(this, AppData.PreferenceIdentifier.NOTIFICATIONS);
        notificationSwitch.setChecked(isNotifications == null || isNotifications);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.putSpecificPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.NOTIFICATIONS, isChecked);
            }
        });

        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        recycler.setNestedScrollingEnabled(false);

        adapter = new ActivityAdapter(this, app.activities);
        recycler.setAdapter(adapter);

        if (intent.hasExtra(EXTRA_ACTIVITY)) {
            activity = intent.getParcelableExtra(EXTRA_ACTIVITY);

            new Action<Integer>() {
                @NonNull
                @Override
                public String id() {
                    return "activityColorDialog";
                }

                @Nullable
                @Override
                protected Integer run() throws InterruptedException {
                    return ColorUtils.getPrimaryColor(AppSettingActivity.this, activity.getComponentName());
                }

                @Override
                protected void done(@Nullable Integer result) {
                    if (activity == null) return;

                    ColorPickerDialog dialog = new ColorPickerDialog(AppSettingActivity.this);
                    dialog.setPreference(activity.getIntegerPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.COLOR));
                    dialog.setDefaultPreference(result != null ? result : ColorUtils.getDefaultColor(AppSettingActivity.this));
                    dialog.setListener(new PreferenceDialog.OnPreferenceListener<Integer>() {
                        @Override
                        public void onPreference(PreferenceDialog dialog, Integer preference) {
                            if (activity != null) {
                                activity.putPreference(AppSettingActivity.this, AppData.PreferenceIdentifier.COLOR, preference);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancel(PreferenceDialog dialog) {
                        }
                    });
                    dialog.show();
                }
            }.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
