package com.duy.notifi.statusbar.data.preference;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.duy.notifi.statusbar.dialogs.FormatDialog;
import com.duy.notifi.statusbar.dialogs.PreferenceDialog;
import com.duy.notifi.statusbar.utils.PreferenceUtils;

public class FormatPreferenceData extends PreferenceData<String> {

    private String value;

    public FormatPreferenceData(Context context, Identifier identifier, String defaultValue, OnPreferenceChangeListener<String> listener) {
        super(context, identifier, listener);

        String value = PreferenceUtils.getStringPreference(getContext(), identifier.getPreference());
        if (value == null) value = defaultValue;
        this.value = value;
    }

    @Override
    public void onClick(View v) {
        Dialog dialog = new FormatDialog(getContext()).setPreference(value).setListener(new PreferenceDialog.OnPreferenceListener<String>() {
            @Override
            public void onPreference(PreferenceDialog dialog, String format) {
                value = format;

                PreferenceUtils.PreferenceIdentifier identifier = getIdentifier().getPreference();
                if (identifier != null)
                    PreferenceUtils.putPreference(getContext(), getIdentifier().getPreference(), format);
                onPreferenceChange(format);
            }

            @Override
            public void onCancel(PreferenceDialog dialog) {
            }
        });

        dialog.setTitle(getIdentifier().getTitle());
        dialog.show();
    }
}
