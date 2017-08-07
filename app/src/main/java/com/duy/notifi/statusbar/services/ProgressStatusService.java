package com.duy.notifi.statusbar.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.activities.AppSettingActivity;
import com.duy.notifi.statusbar.activities.MainActivity;
import com.duy.notifi.statusbar.data.AppData;
import com.duy.notifi.statusbar.data.icon.BatteryLevelProgressIcon;
import com.duy.notifi.statusbar.data.icon.BatteryTempProgressIcon;
import com.duy.notifi.statusbar.data.icon.CpuProgressIcon;
import com.duy.notifi.statusbar.data.icon.CpuTempProgressIcon;
import com.duy.notifi.statusbar.data.icon.ExternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.InternalStorageProgressIcon;
import com.duy.notifi.statusbar.data.icon.NetworkProgressIcon;
import com.duy.notifi.statusbar.data.icon.ProgressIcon;
import com.duy.notifi.statusbar.data.icon.RamProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficDownProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficUpDownProgressIcon;
import com.duy.notifi.statusbar.data.icon.TrafficUpProgressIcon;
import com.duy.notifi.statusbar.data.icon.WifiProgressIcon;
import com.duy.notifi.statusbar.data.monitor.CpuUtil;
import com.duy.notifi.statusbar.data.monitor.StorageUtil;
import com.duy.notifi.statusbar.data.monitor.TrafficManager;
import com.duy.notifi.statusbar.receivers.ActivityVisibilitySettingReceiver;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;
import com.duy.notifi.statusbar.views.GroupProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.duy.notifi.statusbar.data.icon.ProgressIcon.COUNT;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.DEF_ENABLE;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.DEF_TYPE;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.PROGRESS_IDS;
import static com.duy.notifi.statusbar.data.icon.ProgressIcon.ProgressType;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.PreferenceIdentifier.STATUS_COLOR_AUTO;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.PreferenceIdentifier.STATUS_PERSISTENT_NOTIFICATION;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.getBooleanPreference;

public class ProgressStatusService extends Service {

    public static final String ACTION_START = "com.duy.notifi.ACTION_START";
    public static final String ACTION_STOP = "com.duy.notifi.ACTION_STOP";
    public static final String ACTION_UPDATE = "com.duy.notifi.ACTION_UPDATE";
    public static final String EXTRA_COLOR = "com.duy.notifi.EXTRA_COLOR";
    public static final String EXTRA_IS_SYSTEM_FULLSCREEN = "com.duy.notifi.EXTRA_IS_SYSTEM_FULLSCREEN";
    public static final String EXTRA_IS_FULLSCREEN = "com.duy.notifi.EXTRA_IS_FULLSCREEN";
    public static final String EXTRA_IS_TRANSPARENT = "com.duy.notifi.EXTRA_IS_TRANSPARENT";
    public static final String EXTRA_PACKAGE = "com.duy.notifi.EXTRA_PACKAGE";
    public static final String EXTRA_ACTIVITY = "com.duy.notifi.EXTRA_ACTIVITY";

    private static final int ID_FOREGROUND = 682;
    private ReaderThread mReadThread;
    private GroupProgressView statusView;
    private View fullscreenView;
    private KeyguardManager keyguardManager;
    private WindowManager windowManager;

    private PackageManager packageManager;
    private String packageName;
    private AppData.ActivityData activityData;


    public static List<ProgressIcon> getIcons(Context context, GroupProgressView statusView) {
        List<ProgressIcon> icons = new ArrayList<>();
        for (int index = 0; index < COUNT; index++) {
            ProgressIcon iconData = null;
            int progressType = PreferenceUtils.getProgressType(context, index, DEF_TYPE[index]);
            switch (progressType) {
                case ProgressType.CPU_CLOCK:
                    iconData = new CpuProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.CPU_TEMP:
                    iconData = new CpuTempProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.RAM:
                    iconData = new RamProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.BATTERY_LEVEL:
                    iconData = new BatteryLevelProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.BATTERY_TEMP:
                    iconData = new BatteryTempProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.INTERNAL_MEMORY:
                    iconData = new InternalStorageProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.EXTERNAL_MEMORY:
                    iconData = new ExternalStorageProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.TRAFFIC_DOWN:
                    iconData = new TrafficDownProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.TRAFFIC_UP_DOWN:
                    iconData = new TrafficUpDownProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.TRAFFIC_UP:
                    iconData = new TrafficUpProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.WIFI:
                    iconData = new WifiProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;
                case ProgressType.NETWORK_SIGN_LENGTH:
                    iconData = new NetworkProgressIcon(context, statusView, PROGRESS_IDS[index]);
                    break;

            }
            if (iconData != null) {
                iconData.setActive(PreferenceUtils.isProgressActive(context, index, DEF_ENABLE[index]));
                icons.add(iconData);
            }
        }
        return icons;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        packageManager = getPackageManager();

        Boolean enabled = getBooleanPreference(ProgressStatusService.this, STATUS_ENABLED);
        if (enabled != null && enabled && StaticUtils.isPermissionsGranted(ProgressStatusService.this)) {
            setUp();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean enabled = getBooleanPreference(ProgressStatusService.this, STATUS_ENABLED);
        if (enabled == null || !enabled || !StaticUtils.isPermissionsGranted(ProgressStatusService.this)) {
            if (statusView != null) {
                if (statusView.getParent() != null) windowManager.removeView(statusView);
                statusView.unregister();
                statusView = null;
            }
            stopSelf();
            return START_NOT_STICKY;
        }

        if (intent == null) return START_STICKY;
        String action = intent.getAction();
        if (action == null) return START_STICKY;
        switch (action) {
            case ACTION_START:
                setUp();
                break;
            case ACTION_STOP:
                windowManager.removeView(statusView);
                statusView = null;
                stopSelf();
                break;
            case ACTION_UPDATE:
                if (statusView != null) {
                    statusView.setLockscreen(keyguardManager.isKeyguardLocked());

                    statusView.setSystemShowing(intent.getBooleanExtra(EXTRA_IS_SYSTEM_FULLSCREEN,
                            statusView.isSystemShowing()));
                    statusView.setFullscreen(intent.getBooleanExtra(EXTRA_IS_FULLSCREEN,
                            isFullscreen()));

//                    if (intent.hasExtra(EXTRA_PACKAGE) && intent.hasExtra(EXTRA_ACTIVITY)) {
//                        Boolean isForeground = getBooleanPreference(ProgressStatusService.this, STATUS_PERSISTENT_NOTIFICATION);
//                        if (isForeground == null || isForeground) {
//                            packageName = intent.getStringExtra(EXTRA_PACKAGE);
//                            activityData = intent.getParcelableExtra(EXTRA_ACTIVITY);
//
//                            startForeground(packageName, activityData);
//                        } else {
//                            stopForeground(true);
//                        }
//                    }
                }
                return START_STICKY;
        }

        Boolean isForeground = getBooleanPreference(ProgressStatusService.this, STATUS_PERSISTENT_NOTIFICATION);
        if (isForeground == null || isForeground) {
            if (packageName != null && activityData != null)
                startForeground(packageName, activityData);
            else {
                Intent contentIntent = new Intent(ProgressStatusService.this, MainActivity.class);
                contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder contentStackBuilder = TaskStackBuilder.create(ProgressStatusService.this);
                contentStackBuilder.addParentStack(MainActivity.class);
                contentStackBuilder.addNextIntent(contentIntent);

                startForeground(ID_FOREGROUND, new NotificationCompat.Builder(ProgressStatusService.this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(ContextCompat.getColor(ProgressStatusService.this, R.color.colorAccent))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentIntent(contentStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build()
                );
            }
        } else {
            stopForeground(true);
        }

        return START_STICKY;
    }

    private void startForeground(String packageName, AppData.ActivityData activityData) {
        if (true) return;
        AppData appData;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appData = new AppData(packageManager, applicationInfo, packageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        Intent contentIntent = new Intent(ProgressStatusService.this, MainActivity.class);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder contentStackBuilder = TaskStackBuilder.create(ProgressStatusService.this);
        contentStackBuilder.addParentStack(MainActivity.class);
        contentStackBuilder.addNextIntent(contentIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ProgressStatusService.this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(ProgressStatusService.this, R.color.colorAccent))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(activityData.name)
                .setSubText(packageName)
                .setContentIntent(contentStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));

        Boolean isColorAuto = getBooleanPreference(ProgressStatusService.this, STATUS_COLOR_AUTO);
        if (isColorAuto == null || isColorAuto) {
            Intent colorIntent = new Intent(ProgressStatusService.this, AppSettingActivity.class);
            colorIntent.putExtra(AppSettingActivity.EXTRA_APP, appData);
            colorIntent.putExtra(AppSettingActivity.EXTRA_ACTIVITY, activityData);
            colorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder colorStackBuilder = TaskStackBuilder.create(ProgressStatusService.this);
            colorStackBuilder.addParentStack(AppSettingActivity.class);
            colorStackBuilder.addNextIntent(colorIntent);

            builder.addAction(R.drawable.ic_notification_color, getString(R.string.action_set_color), colorStackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT));
        }

        Boolean isFullscreen = activityData.getBooleanPreference(ProgressStatusService.this, AppData.PreferenceIdentifier.FULLSCREEN);
        Intent visibleIntent = new Intent(ProgressStatusService.this, ActivityVisibilitySettingReceiver.class);
        visibleIntent.putExtra(ActivityVisibilitySettingReceiver.EXTRA_ACTIVITY, activityData);
        visibleIntent.putExtra(ActivityVisibilitySettingReceiver.EXTRA_VISIBILITY, isFullscreen != null && isFullscreen);

        builder.addAction(R.drawable.ic_notification_visible, getString(isFullscreen != null && isFullscreen ? R.string.action_show_status : R.string.action_hide_status), PendingIntent.getBroadcast(ProgressStatusService.this, 0, visibleIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        Intent settingsIntent = new Intent(ProgressStatusService.this, AppSettingActivity.class);
        settingsIntent.putExtra(AppSettingActivity.EXTRA_APP, appData);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder settingsStackBuilder = TaskStackBuilder.create(ProgressStatusService.this);
        settingsStackBuilder.addParentStack(AppSettingActivity.class);
        settingsStackBuilder.addNextIntent(settingsIntent);

        builder.addAction(R.drawable.ic_notification_settings, getString(R.string.action_app_settings), settingsStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));

        startForeground(ID_FOREGROUND, builder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(ACTION_UPDATE);
            intent.setClass(ProgressStatusService.this, ProgressStatusService.class);

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, pendingIntent);
        } else {
            super.onTaskRemoved(rootIntent);
        }
    }

    public void setUp() {
        if (statusView == null || statusView.getParent() == null) {
            if (statusView != null) windowManager.removeView(statusView);
            statusView = new GroupProgressView(ProgressStatusService.this);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP;

            windowManager.addView(statusView, params);
        } else {
            statusView.unregister();
        }

        statusView.setUp();

        if (fullscreenView == null || fullscreenView.getParent() == null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(1,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSPARENT);
            params.gravity = Gravity.START | Gravity.TOP;
            fullscreenView = new View(ProgressStatusService.this);

            windowManager.addView(fullscreenView, params);

            fullscreenView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (statusView != null && fullscreenView != null) {
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        statusView.setFullscreen(fullscreenView.getMeasuredHeight() == size.y);
                    }
                }
            });
        }

        statusView.setIcons(getIcons(ProgressStatusService.this, statusView));
        statusView.register();

        if (StaticUtils.isAccessibilityServiceRunning(ProgressStatusService.this)) {
            Intent intent = new Intent(AccessibilityService.ACTION_GET_COLOR);
            intent.setClass(ProgressStatusService.this, AccessibilityService.class);
            startService(intent);
        }

        if (mReadThread != null) {
            mReadThread.setRunning(false);
            mReadThread.interrupt();
        }
        mReadThread = new ReaderThread();
        mReadThread.start();
    }

    public boolean isFullscreen() {
        if (statusView != null && fullscreenView != null) {
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            return fullscreenView.getMeasuredHeight() == size.y;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        if (fullscreenView != null) {
            if (fullscreenView.getParent() != null) windowManager.removeView(fullscreenView);
            fullscreenView = null;
        }

        if (statusView != null) {
            if (statusView.isRegistered()) statusView.unregister();
            if (statusView.getParent() != null) windowManager.removeView(statusView);
            statusView = null;
        }

        if (mReadThread != null) {
            mReadThread.setRunning(false);
            mReadThread.interrupt();
            mReadThread = null;
        }
        super.onDestroy();
    }

    private class ReaderThread extends Thread {
        private static final String TAG = "ReaderThread";
        private int intervalRead = 1000; //1s
        private TrafficManager mTrafficManager;
        private ActivityManager mActivityManager;
        private boolean running = true;

        public ReaderThread() {
            mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            mTrafficManager = new TrafficManager();
        }

        public void setIntervalRead(int intervalRead) {
            this.intervalRead = intervalRead;
        }

        private void collectData() {
            try {
                readRamUsage();
                readCpuUsage();
                readCpuTemp();
                readInternalState();
                readExternalState();
                readNetUpDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            while (running) {
                collectData();
                Log.d(TAG, "run() called");
                try {
                    Thread.sleep(intervalRead);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void readCpuTemp() {
            try {
                float currentTemp = CpuUtil.readTemp();
                int percent = (int) (currentTemp / PreferenceUtils.getMaxCpuTemp(ProgressStatusService.this) * 100f);
                Intent intent = new Intent(CpuTempProgressIcon.ACTION_UPDATE_CPU_TEMP);
                intent.putExtra(CpuTempProgressIcon.EXTRA_PERCENT, percent);
                sendBroadcast(intent);
            } catch (Exception e) {

            }
        }

        private void readNetUpDown() {
            if (mTrafficManager.canRead()) {
                float trafficDown = mTrafficManager.getTrafficDown();
                long max = PreferenceUtils.getMaxNetDown(ProgressStatusService.this);
                int percent = (int) (trafficDown / max * 100f);
                Intent intent = new Intent(TrafficDownProgressIcon.ACTION_UPDATE_TRAFFIC_DOWN);
                intent.putExtra(ExternalStorageProgressIcon.EXTRA_PERCENT, percent);
                sendBroadcast(intent);

                float trafficUp = mTrafficManager.getTrafficUpLoad();
                max = PreferenceUtils.getMaxNetUp(ProgressStatusService.this);
                percent = (int) (trafficUp / max * 100f);
                intent = new Intent(TrafficUpProgressIcon.ACTION_UPDATE_TRAFFIC_UP);
                intent.putExtra(TrafficUpProgressIcon.EXTRA_PERCENT, percent);
                sendBroadcast(intent);

                max = Math.max(PreferenceUtils.getMaxNetUp(ProgressStatusService.this), PreferenceUtils.getMaxNetDown(ProgressStatusService.this));
                percent = (int) ((trafficUp + trafficDown) / max * 100f);
                intent = new Intent(TrafficUpDownProgressIcon.ACTION_UPDATE_TRAFFIC_UP_DOWN);
                intent.putExtra(TrafficUpDownProgressIcon.EXTRA_PERCENT, percent);
                sendBroadcast(intent);
            }
        }

        private void readExternalState() {
            if (StorageUtil.hasExternalMemory()) {
                StorageUtil.Storage storage = StorageUtil.getExternalMemory();
                int percent = storage.getPercent();
                Intent intent = new Intent(ExternalStorageProgressIcon.ACTION_UPDATE_EXTERNAL_STORAGE);
                intent.putExtra(ExternalStorageProgressIcon.EXTRA_PERCENT, percent);
                sendBroadcast(intent);
            }
        }

        private void readInternalState() {
            StorageUtil.Storage storage = StorageUtil.getInternalMemory();
            int percent = storage.getPercent();
            Intent intent = new Intent(InternalStorageProgressIcon.ACTION_UPDATE_INTERNAL_STORAGE);
            intent.putExtra(InternalStorageProgressIcon.EXTRA_PERCENT, percent);
            sendBroadcast(intent);
        }


        private void readCpuUsage() throws IOException {
            try {
                int percentage = (int) (CpuUtil.readUsage() * 100);
                Intent intent = new Intent(CpuProgressIcon.ACTION_UPDATE_CPU);
                intent.putExtra(CpuProgressIcon.EXTRA_PERCENT, percentage);
                intent.putExtra(CpuProgressIcon.EXTRA_CPU_INDEX, 0);
                sendBroadcast(intent);
            } catch (Exception e) {
            }
        }

        private void readRamUsage() {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            mActivityManager.getMemoryInfo(memoryInfo);

            //update ram notification
            Intent intent = new Intent(RamProgressIcon.ACTION_UPDATE_RAM);
            intent.putExtra(RamProgressIcon.EXTRA_INFO, memoryInfo);
            sendBroadcast(intent);
        }


        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}
