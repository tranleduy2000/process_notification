package com.duy.notifi.statusbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.utils.PreferenceUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Duy on 04-Aug-17.
 */

public class SettingFragment extends SimpleFragment {
    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }

    private void bindView(View view) {
        SwitchCompat fullscreen = view.findViewById(R.id.show_fullscreen);
        fullscreen.setChecked(PreferenceUtils.isShowInFullScreen(getContext()));
        fullscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setShowInFullScreen(getContext(), isChecked);
            }
        });

        DiscreteSeekBar cpuTemp = view.findViewById(R.id.cpu_temp);
        cpuTemp.setProgress(PreferenceUtils.getMaxCpuTemp(getContext()));
        cpuTemp.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                PreferenceUtils.setMaxCpuTemp(getContext(), seekBar.getProgress());
            }
        });

        DiscreteSeekBar batteryTemp = view.findViewById(R.id.battery_temp);
        batteryTemp.setProgress(PreferenceUtils.getMaxBatteryTemp(getContext()));
        batteryTemp.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                PreferenceUtils.setMaxBatteryTemp(getContext(), seekBar.getProgress());
            }
        });

        EditText cpuTempPath = view.findViewById(R.id.edit_cpu_temp_path);
        cpuTempPath.setText(PreferenceUtils.getCpuTempPath(getContext()));
        cpuTempPath.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    PreferenceUtils.setCpuTempPath(getContext(),
                            ((EditText) v).getText().toString());
                }
            }
        });
        EditText maxUp = view.findViewById(R.id.edit_max_up);
        maxUp.setText(PreferenceUtils.getMaxNetUp(getContext()) + "");
        maxUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    if (editText.getText().toString().trim().isEmpty()) {
                        editText.setError(getString(R.string.enter_value));
                        return;
                    }
                    PreferenceUtils.setMaxUploadSpeed(getContext(),
                            Integer.parseInt(editText.getText().toString()));
                }
            }
        });
        EditText maxDown = view.findViewById(R.id.edit_max_down);
        maxDown.setText(PreferenceUtils.getMaxNetDown(getContext()) + "");
        maxDown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    if (editText.getText().toString().trim().isEmpty()) {
                        editText.setError(getString(R.string.enter_value));
                        return;
                    }
                    PreferenceUtils.setMaxDownloadSpeed(getContext(),
                            Integer.parseInt(editText.getText().toString()));
                }
            }
        });


    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.setting);
    }

    @Override
    public void filter(@Nullable String filter) {

    }
}
