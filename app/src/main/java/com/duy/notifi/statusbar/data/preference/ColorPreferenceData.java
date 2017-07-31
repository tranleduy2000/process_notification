package com.duy.notifi.statusbar.data.preference;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.dialogs.ColorPickerDialog;
import com.duy.notifi.statusbar.dialogs.PreferenceDialog;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.views.ColorImageView;

public class ColorPreferenceData extends PreferenceData<Integer> {

    private int defaultValue, value;

    public ColorPreferenceData(Context context, Identifier identifier, @ColorInt int defaultValue, OnPreferenceChangeListener<Integer> listener) {
        super(context, identifier, listener);

        this.defaultValue = defaultValue;

        Integer value = PreferenceUtils.getIntegerPreference(getContext(), identifier.getPreference());
        if (value == null) value = defaultValue;
        this.value = value;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_preference_color, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ColorImageView) holder.v.findViewById(R.id.color)).setColor(value);
    }

    @Override
    public void onClick(final View v) {
        Dialog dialog = new ColorPickerDialog(getContext()).setPreference(value).setDefaultPreference(defaultValue).setListener(new PreferenceDialog.OnPreferenceListener<Integer>() {
            @Override
            public void onPreference(PreferenceDialog dialog, Integer color) {
                value = color;
                ((ColorImageView) v.findViewById(R.id.color)).setColor(color);

                PreferenceUtils.PreferenceIdentifier identifier = getIdentifier().getPreference();
                if (identifier != null)
                    PreferenceUtils.putPreference(getContext(), getIdentifier().getPreference(), color);
                onPreferenceChange(color);
            }

            @Override
            public void onCancel(PreferenceDialog dialog) {
            }
        });

        dialog.setTitle(getIdentifier().getTitle());

        dialog.show();
    }
}
