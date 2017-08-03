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

    @Nullable
    public static Boolean getBooleanPreference(Context context, String identifier) {
        if (context == null || identifier == null) return null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(identifier)) {
            try {
                return prefs.getBoolean(identifier, false);
            } catch (ClassCastException e) {
                return null;
            }
        } else return null;
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

    public static Boolean isProgressActive(Context context, int index) {
        return getBooleanPreference(context, "key_progress_activve" + index);
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
        ENABLE_PROCESS_4, TYPE_PROGRESS_4,
    }

    public static class ProgressType {
        public static final int CPU_CLOCK = 0;
        public static final int CPU_TEMP = 1;
        public static final int RAM = 2;
        public static final int BATTERY = 3;
        public static final int INTERNAL_MEMORY = 4;
        public static final int EXTERNAL_MEMORY = 5;
        public static final int NET_UP = 6;
        public static final int NET_DOWN = 7;
    }
}
