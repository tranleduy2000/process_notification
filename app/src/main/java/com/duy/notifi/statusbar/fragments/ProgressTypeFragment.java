package com.duy.notifi.statusbar.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.services.StatusService;
import com.duy.notifi.statusbar.utils.PreferenceUtils;

import static com.duy.notifi.statusbar.data.icon.ProgressIcon.DEF_ENABLE;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.DEF_TYPE;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.ENABLE_IDS;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.SPINNER_IDS;

/**
 * Created by Duy on 04-Aug-17.
 */

public class ProgressTypeFragment extends SimpleFragment {
    public static ProgressTypeFragment newInstance() {

        Bundle args = new Bundle();

        ProgressTypeFragment fragment = new ProgressTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }


    private void bindView(View view) {

        for (int i = 0; i < ENABLE_IDS.length; i++) {
            boolean progressActive = PreferenceUtils.isProgressActive(getContext(), i, DEF_ENABLE[i]);
            SwitchCompat enable = view.findViewById(ENABLE_IDS[i]);
            enable.setChecked(progressActive);
            final int finalI = i;
            enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean active) {
                    PreferenceUtils.setActive(getContext(), finalI, active);
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(getContext(), StatusService.class);
                    getActivity().startService(intent);
                }
            });

            Spinner spinner = view.findViewById(SPINNER_IDS[i]);
            int pos = PreferenceUtils.getProgressType(getContext(), finalI, DEF_TYPE[i]);
            spinner.setSelection(pos);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int type, long l) {
                    PreferenceUtils.setProgressType(getContext(), finalI, type);
                    Intent intent = new Intent(StatusService.ACTION_START);
                    intent.setClass(getContext(), StatusService.class);
                    getActivity().startService(intent);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }


    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.progress_type);
    }

    @Override
    public void filter(@Nullable String filter) {

    }
}
