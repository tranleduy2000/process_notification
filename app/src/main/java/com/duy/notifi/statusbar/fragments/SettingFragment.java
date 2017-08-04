package com.duy.notifi.statusbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.utils.PreferenceUtils;

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
        SwitchCompat fullscreen =view.findViewById(R.id.show_fullscreen);
        fullscreen.setChecked(PreferenceUtils.isShowInFullScreen(getContext()));
        fullscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setShowInFullScreen(getContext(), isChecked);
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
