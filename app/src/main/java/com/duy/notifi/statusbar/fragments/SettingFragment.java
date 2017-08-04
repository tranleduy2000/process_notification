package com.duy.notifi.statusbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.notifi.R;

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
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.setting);
    }

    @Override
    public void filter(@Nullable String filter) {

    }
}
