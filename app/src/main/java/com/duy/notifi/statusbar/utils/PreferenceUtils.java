package com.duy.notifi.statusbar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class PreferenceUtils {

    public static final String SHOW_IN_FULL_SCREEN = "SHOW_IN_FULL_SCREEN";
    public static final String PROGRESS_ACTIVE = "PROGRESS_ACTIVE";
    public static final String MAX_CPU_TEMP = "MAX_CPU_TEMP";
    public static final String MAX_BATTERY_TEMP = "MAX_BATTERY_TEMP";
    public static final String MAX_UPLOAD_SPEED = "MAX_UPLOAD_SPEED";
    public static final String MAX_DOWN_SPEED = "MAX_DOWN_SPEED";
    public static final String CPU_TEMP_PATH = "CPU_TEMP_PATH";

    private static final int DEF_MAX_CPU_TEMP = 70; //C
    private static final int DEF_MAX_BATTERY_TEMP = 60; //C
    private static final long DEF_MAX_UP_DOWN = 2 * 1024 * 1024; //2MB/s
    private static final String DEF_CPU_TEMP_PATH = "/sys/class/thermal/thermal_zone0/temp";
    ;

    public static int getMaxCpuTemp(Context context) {
        return getPref(context).getInt(MAX_CPU_TEMP, DEF_MAX_CPU_TEMP);
    }

    public static int getMaxBatteryTemp(Context context) {
        return getPref(context).getInt(MAX_BATTERY_TEMP, DEF_MAX_BATTERY_TEMP);
    }

    public static void setMaxCpuTemp(Context context, int temp) {
        edit(context).putInt(MAX_CPU_TEMP, temp).apply();
    }

    public static void setMaxBatteryTemp(Context context, int temp) {
        edit(context).putInt(MAX_BATTERY_TEMP, temp).apply();
    }

    public static long getMaxNetUp(Context context) {
        return getPref(context).getLong(MAX_UPLOAD_SPEED, DEF_MAX_UP_DOWN);
    }

    /**
     *
     * @param context
     * @return download speed in byte/s
     */
    public static long getMaxNetDown(Context context) {
        return getPref(context).getLong(MAX_DOWN_SPEED, DEF_MAX_UP_DOWN);
    }

    /**
     *
     * @param context
     * @param maxUploadSpeed - KB/s
     */
    public static void setMaxUploadSpeed(Context context, long maxUploadSpeed) {
        edit(context).putLong(MAX_UPLOAD_SPEED, maxUploadSpeed).apply();
    }

    public static void setMaxDownloadSpeed(Context context, long maxDownSpeed) {
        edit(context).putLong(MAX_DOWN_SPEED, maxDownSpeed).apply();
    }

    public static String getCpuTempPath(Context context) {
        return getPref(context).getString(CPU_TEMP_PATH, DEF_CPU_TEMP_PATH);
    }

    public static void setCpuTempPath(Context context, String path) {
        edit(context).putString(CPU_TEMP_PATH, path).apply();
    }

    @Nullable
    public static Object getPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString()))
            return prefs.getAll().get(identifier.toString());
        else
            return null;
    }

    @Nullable
    public static Boolean getBooleanPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString())) {
            try {
                return prefs.getBoolean(identifier.toString(), false);
            } catch (ClassCastException e) {
                return null;
            }
        } else return null;
    }

    public static boolean getBooleanPreference(Context context, String identifier, boolean def) {
        if (context == null || identifier == null) return def;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier)) {
            try {
                return prefs.getBoolean(identifier, def);
            } catch (ClassCastException e) {
                return def;
            }
        } else {
            return def;
        }
    }

    @Nullable
    public static Integer getIntegerPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString())) {
            try {
                return prefs.getInt(identifier.toString(), 0);
            } catch (ClassCastException e) {
                return null;
            }
        } else return null;
    }

    @Nullable
    public static Integer getIntegerPreference(Context context, String identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier)) {
            try {
                return prefs.getInt(identifier, 0);
            } catch (ClassCastException e) {
                return null;
            }
        } else return null;
    }

    @Nullable
    public static String getStringPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString())) {
            if (prefs.contains(identifier.toString())) {
                try {
                    return prefs.getString(identifier.toString(), null);
                } catch (ClassCastException e) {
                    return null;
                }
            } else return null;
        } else return null;
    }

    @Nullable
    public static Float getFloatPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString())) {
            if (prefs.contains(identifier.toString())) {
                try {
                    return prefs.getFloat(identifier.toString(), 0);
                } catch (ClassCastException e) {
                    return null;
                }
            } else return null;
        } else return null;
    }

    @Nullable
    public static Long getLongPreference(Context context, PreferenceIdentifier identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier.toString())) {
            if (prefs.contains(identifier.toString())) {
                try {
                    return prefs.getLong(identifier.toString(), 0);
                } catch (ClassCastException e) {
                    return null;
                }
            } else return null;
        } else return null;
    }

    public static void putPreference(Context context, PreferenceIdentifier identifier, boolean object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(identifier.toString(), object).apply();
    }

    public static void putPreference(Context context, PreferenceIdentifier identifier, int object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(identifier.toString(), object).apply();
    }

    public static void putPreference(Context context, PreferenceIdentifier identifier, String object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(identifier.toString(), object).apply();
    }

    public static void putPreference(Context context, PreferenceIdentifier identifier, float object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(identifier.toString(), object).apply();
    }

    public static void putPreference(Context context, PreferenceIdentifier identifier, long object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(identifier.toString(), object).apply();
    }

    public static boolean toFile(Context context, File file) {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(context).getAll();

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(new Gson().toJson(prefs).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        } else return false;

        return file.exists();
    }

    public static boolean fromFile(Context context, File file) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        byte[] bytes = new byte[(int) file.length()];

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            stream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        String contents = new String(bytes);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            Map<String, ?> map = new Gson().fromJson(contents, Map.class);
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value instanceof Boolean)
                    editor.putBoolean(key, (Boolean) value);
                else if (value instanceof Float)
                    editor.putFloat(key, (Float) value);
                else if (value instanceof Integer)
                    editor.putInt(key, (Integer) value);
                else if (value instanceof Long)
                    editor.putLong(key, (Long) value);
                else if (value instanceof String)
                    editor.putString(key, (String) value);
                else if (value instanceof Set)
                    editor.putStringSet(key, (Set) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return editor.commit();
    }

    public static String getBackupsDir() {
        return Environment.getExternalStorageDirectory() + "/status/backups";
    }

    public static Integer getProgressType(Context context, int index) {
        return getIntegerPreference(context, "key_progress_type" + index);
    }

    public static int getProgressType(Context context, int index, int def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt("key_progress_type" + index, def);
    }

    public static void setProgressType(Context context, int index, int type) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt("key_progress_type" + index, type).apply();
    }

    public static boolean isProgressActive(Context context, int index, boolean def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PROGRESS_ACTIVE + index, def);
    }

    public static void setProgressActive(Context context, int index, boolean isActive) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PROGRESS_ACTIVE + index, isActive).apply();
    }

    public static void setShowInFullScreen(Context context, boolean isChecked) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(SHOW_IN_FULL_SCREEN, isChecked).apply();
    }

    public static boolean isShowInFullScreen(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHOW_IN_FULL_SCREEN, false);
    }

    private static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }




    public enum PreferenceIdentifier {
        STATUS_ENABLED,
        STATUS_NOTIFICATIONS_COMPAT,
        STATUS_NOTIFICATIONS_HEADS_UP,
        STATUS_COLOR_AUTO,
        STATUS_COLOR,
        STATUS_HOME_TRANSPARENT,
        STATUS_ICON_COLOR,
        STATUS_DARK_ICONS,
        STATUS_TINTED_ICONS,
        STATUS_LOCKSCREEN_EXPAND,
        STATUS_HEADS_UP_DURATION,
        STATUS_BACKGROUND_ANIMATIONS,
        STATUS_ICON_ANIMATIONS,
        STATUS_HEADS_UP_LAYOUT,
        STATUS_HIDE_ON_VOLUME,
        STATUS_PERSISTENT_NOTIFICATION,
        STATUS_BURNIN_PROTECTION,
        STATUS_DEBUG,

        ENABLE_PROCESS_1, TYPE_PROGRESS_1,
        ENABLE_PROCESS_2, TYPE_PROGRESS_2,
        ENABLE_PROCESS_3, TYPE_PROGRESS_3,
        ENABLE_PROCESS_4, TYPE_PROGRESS_4, SHOW_IN_FULL_SCREEN,
    }


}
