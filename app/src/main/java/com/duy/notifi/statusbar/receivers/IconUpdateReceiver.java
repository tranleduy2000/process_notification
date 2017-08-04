package com.duy.notifi.statusbar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.duy.notifi.statusbar.data.icon.ProgressIcon;

import java.lang.ref.SoftReference;

public abstract class IconUpdateReceiver<T extends ProgressIcon> extends BroadcastReceiver {

    private SoftReference<T> reference;

    public IconUpdateReceiver(T iconData) {
        reference = new SoftReference<>(iconData);
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        T icon = reference.get();
        if (icon != null) onReceive(icon, intent);
    }

    public abstract void onReceive(T icon, Intent intent);
}
