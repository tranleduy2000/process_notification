package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.duy.notifi.statusbar.views.StatusView;

import java.lang.ref.SoftReference;

import james.signalstrengthslib.SignalStrengths;

public class NetworkProgressIcon extends ProgressIcon {

    private TelephonyManager telephonyManager;
    private NetworkListener networkListener;
    private boolean isRegistered;

    public NetworkProgressIcon(Context context, StatusView statusView, int progressId) {
        super(context, statusView, progressId);
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void register() {
        if (networkListener == null) {
            networkListener = new NetworkListener(this);
            telephonyManager.listen(networkListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
        isRegistered = true;
    }

    @Override
    public void unregister() {
        isRegistered = false;
    }


    private static class NetworkListener extends PhoneStateListener {

        private SoftReference<NetworkProgressIcon> reference;

        private NetworkListener(NetworkProgressIcon iconData) {
            reference = new SoftReference<>(iconData);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            NetworkProgressIcon icon = null;
            if (reference != null) icon = reference.get();
            if (icon != null && icon.isRegistered) {
                icon.onProcessUpdate((int) Math.round(SignalStrengths.getFirstValid(signalStrength)), 4);
            }
        }
    }
}
