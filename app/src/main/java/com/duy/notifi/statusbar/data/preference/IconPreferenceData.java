package com.duy.notifi.statusbar.data.preference;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.IconStyleData;
import com.duy.notifi.statusbar.data.icon.ProgressIcon;
import com.duy.notifi.statusbar.dialogs.IconDialog;
import com.duy.notifi.statusbar.dialogs.PreferenceDialog;
import com.duy.notifi.statusbar.views.IconStyleImageView;

public class IconPreferenceData extends PreferenceData<IconStyleData> {

    private IconStyleData iconStyle;
    private ProgressIcon iconData;

    public IconPreferenceData(Context context, Identifier identifier, IconStyleData iconStyle, ProgressIcon iconData, OnPreferenceChangeListener<IconStyleData> listener) {
        super(context, identifier, listener);

        if (iconStyle == null) iconStyle = (IconStyleData) iconData.getIconStyles().get(0);
        this.iconStyle = iconStyle;
        this.iconData = iconData;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_preference_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((IconStyleImageView) holder.v.findViewById(R.id.icon)).setIconStyle(iconStyle);
    }

    @Override
    public void onClick(final View v) {
        Dialog dialog = new IconDialog(getContext(), iconData).setPreference(iconStyle).setListener(new PreferenceDialog.OnPreferenceListener<IconStyleData>() {
            @Override
            public void onPreference(PreferenceDialog dialog, IconStyleData preference) {
                if (preference != null) {
                    ((IconStyleImageView) v.findViewById(R.id.icon)).setIconStyle(preference);

                    IconPreferenceData.this.iconStyle = preference;
                    onPreferenceChange(preference);
                }
            }

            @Override
            public void onCancel(PreferenceDialog dialog) {
            }
        });
        dialog.setTitle(getIdentifier().getTitle());
        dialog.show();
    }
}
