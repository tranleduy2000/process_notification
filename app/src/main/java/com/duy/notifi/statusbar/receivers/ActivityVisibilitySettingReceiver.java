package com.duy.notifi.statusbar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.duy.notifi.statusbar.data.AppData;
import com.duy.notifi.statusbar.utils.StaticUtils;

public class ActivityVisibilitySettingReceiver extends BroadcastReceiver {

    public static final String EXTRA_ACTIVITY = "com.duy.notifi.EXTRA_ACTIVITY";
    public static final String EXTRA_VISIBILITY = "com.duy.notifi.EXTRA_VISIBILITY";
    private static final String TAG = "ActivityVisibilitySetti";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");

        if (intent.hasExtra(EXTRA_ACTIVITY) && intent.hasExtra(EXTRA_VISIBILITY)) {
            AppData.ActivityData activity = intent.getParcelableExtra(EXTRA_ACTIVITY);
            activity.putPreference(context, AppData.PreferenceIdentifier.FULLSCREEN,
                    !intent.getBooleanExtra(EXTRA_VISIBILITY, true));
            StaticUtils.updateStatusService(context);
        }
    }
}
