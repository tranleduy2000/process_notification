package com.duy.notifi.statusbar.data.monitor;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.IconStyleData;
import com.duy.notifi.statusbar.data.preference.BooleanPreferenceData;
import com.duy.notifi.statusbar.data.preference.ColorPreferenceData;
import com.duy.notifi.statusbar.data.preference.IconPreferenceData;
import com.duy.notifi.statusbar.data.preference.IntegerPreferenceData;
import com.duy.notifi.statusbar.data.preference.ListPreferenceData;
import com.duy.notifi.statusbar.data.preference.PreferenceData;
import com.duy.notifi.statusbar.receivers.IconUpdateReceiver;
import com.duy.notifi.statusbar.utils.ColorUtils;
import com.duy.notifi.statusbar.utils.PreferenceUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;
import com.duy.notifi.statusbar.views.CustomImageView;
import com.duy.notifi.statusbar.views.StatusView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ProgressIcon<T extends IconUpdateReceiver> {

    public static final int LEFT_GRAVITY = -1, CENTER_GRAVITY = 0, RIGHT_GRAVITY = 1;
    public static final int[] PROGRESS_IDS = {
            R.id.progress_1,
            R.id.progress_2,
            R.id.progress_3,
            R.id.progress_4};
    public static final int[] ENABLE_IDS = {
            R.id.enable_progress_1,
            R.id.enable_progress_2,
            R.id.enable_progress_3,
            R.id.enable_progress_4};
    public static final int[] SPINNER_IDS = {
            R.id.spinner_type_1,
            R.id.spinner_type_2,
            R.id.spinner_type_3,
            R.id.spinner_type_4};
    public static final int[] DEF_TYPE = {
            ProgressType.CPU_TEMP,
            ProgressType.CPU_CLOCK,
            ProgressType.RAM,
            ProgressType.BATTERY};

    public static final boolean[] DEF_ENABLE = {false, true, true, true};
    private static final String TAG = "ProgressIcon";
    protected View view;
    protected int progressId;
    protected Boolean active;
    private StatusView statusView;
    private Context context;
    private DrawableListener drawableListener;
    private TextListener textListener;
    private IconStyleData style;
    private T receiver;
    private Drawable drawable;
    private String text;
    private int color;
    private boolean isRegister;

    public ProgressIcon(Context context, StatusView statusView, int progressId) {
        this.context = context;
        color = ColorUtils.getDefaultColor(context);
        this.statusView = statusView;
        this.progressId = progressId;

        String name = getStringPreference(PreferenceIdentifier.ICON_STYLE);
        List<IconStyleData> styles = getIconStyles();
        if (styles.size() > 0) {
            if (name != null) {
                for (IconStyleData style : styles) {
                    if (style.name.equals(name)) {
                        this.style = style;
                        break;
                    }
                }
            }

            if (style == null) style = styles.get(0);
        }
    }

    public final Context getContext() {
        return context;
    }

    @ColorInt
    public final int getColor() {
        return color;
    }

    public final void setColor(@ColorInt int color) {
        this.color = color;
    }

    public final boolean hasDrawableListener() {
        return drawableListener != null;
    }

    public final DrawableListener getDrawableListener() {
        return drawableListener;
    }

    public final void setDrawableListener(DrawableListener drawableListener) {
        this.drawableListener = drawableListener;
    }

    public final boolean hasTextListener() {
        return textListener != null;
    }

    public final TextListener getTextListener() {
        return textListener;
    }

    public final void setTextListener(TextListener textListener) {
        this.textListener = textListener;
    }

    public final void onDrawableUpdate(int level) {
        if (hasDrawable()) {
            drawable = style.getDrawable(context, level);

            if (view != null) {
                CustomImageView iconView = view.findViewById(R.id.icon);

                if (iconView != null) {
                    if (drawable != null) {
                        view.setVisibility(View.VISIBLE);
                        iconView.setVisibility(View.VISIBLE);

                        ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                        if (layoutParams != null)
                            layoutParams.height = (int) StaticUtils.getPixelsFromDp(getIconScale());
                        else
                            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) StaticUtils.getPixelsFromDp(getIconScale()));

                        iconView.setLayoutParams(layoutParams);
                        iconView.setImageDrawable(drawable);
                    } else {
                        iconView.setVisibility(View.GONE);
                        if (canHazText() && getText() == null)
                            view.setVisibility(View.GONE);
                    }
                }
            }
        }

        if (hasDrawableListener()) getDrawableListener().onUpdate(drawable);
    }

    public final void onTextUpdate(@Nullable String text) {
        if (hasText()) {
            if (view != null) {
                TextView textView = (TextView) view.findViewById(R.id.text);

                if (text != null) {
                    view.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                    Integer color = getTextColor();
                    Boolean isContrast = PreferenceUtils.getBooleanPreference(getContext(), PreferenceUtils.PreferenceIdentifier.STATUS_DARK_ICONS);

                    if (color != null && !((isContrast == null || isContrast) && (color == Color.WHITE || color == Color.BLACK))) {
                        textView.setTextColor(color);
                        textView.setTag(color);
                    } else textView.setTag(null);

                    textView.setText(text);
                } else {
                    textView.setVisibility(View.GONE);
                    if (canHazDrawable() && getDrawable() == null)
                        view.setVisibility(View.GONE);
                }
            }

            if (hasTextListener()) getTextListener().onUpdate(text);
            this.text = text;
        }
    }

    public boolean isVisible() {
        Boolean isVisible = getBooleanPreference(PreferenceIdentifier.VISIBILITY);
        return isVisible == null || isVisible;
    }

    public boolean canHazDrawable() {
        //i can haz drawable resource
        return true;
    }

    public boolean hasDrawable() {
        Boolean hasDrawable = getBooleanPreference(PreferenceIdentifier.ICON_VISIBILITY);
        return canHazDrawable() && (hasDrawable == null || hasDrawable) && style != null;
    }

    public boolean canHazText() {
        //u can not haz text tho
        return false;
    }

    public boolean hasText() {
        Boolean hasText = getBooleanPreference(PreferenceIdentifier.TEXT_VISIBILITY);
        return canHazText() && (hasText != null && hasText);
    }

    public T getReceiver() {
        return null;
    }

    public IntentFilter getIntentFilter() {
        return new IntentFilter();
    }

    public void register() {
        isRegister = true;
        if (receiver == null) receiver = getReceiver();
        if (receiver != null) getContext().registerReceiver(receiver, getIntentFilter());
        onDrawableUpdate(-1);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void unregister() {
        if (receiver != null) {
            getContext().unregisterReceiver(receiver);
            isRegister = false;
        }
    }

    public final int getIconPadding() {
        Integer padding = getIntegerPreference(PreferenceIdentifier.ICON_PADDING);
        if (padding == null) padding = 2;
        return padding;
    }

    public final int getIconScale() {
        Integer scale = getIntegerPreference(PreferenceIdentifier.ICON_SCALE);
        if (scale == null) scale = 24;
        return scale;
    }

    public final float getTextSize() {
        Integer size = getIntegerPreference(PreferenceIdentifier.TEXT_SIZE);
        if (size == null) size = 14;
        return size;
    }

    @Nullable
    @ColorInt
    public final Integer getTextColor() {
        return getIntegerPreference(PreferenceIdentifier.TEXT_COLOR);
    }

    public final int getPosition() {
        Integer position = getIntegerPreference(PreferenceIdentifier.POSITION);
        if (position == null) position = 0;
        return position;
    }

    public int getDefaultGravity() {
        return RIGHT_GRAVITY;
    }

    public final int getGravity() {
        Integer gravity = getIntegerPreference(PreferenceIdentifier.GRAVITY);
        if (gravity == null) gravity = getDefaultGravity();
        return gravity;
    }

    @Nullable
    public Drawable getDrawable() {
        if (hasDrawable()) return drawable;
        else return null;
    }

    @Nullable
    public String getText() {
        if (hasText()) return text;
        else return null;
    }

    public String getTitle() {
        return getClass().getSimpleName();
    }

    @LayoutRes
    public int getIconLayout() {
        return R.layout.item_icon;
    }

    public View getIconView() {
        if (statusView != null && view == null) {
            view = statusView.findViewById(progressId);
            if (view == null) {
                LinearLayout child = this.statusView.getStatusView();
                view = child.findViewById(progressId);

            }
        }
        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        return view;
    }

    public List<PreferenceData> getPreferences() {
        List<PreferenceData> preferences = new ArrayList<>();

        if (canHazDrawable() && (hasText() || !hasDrawable())) {
            preferences.add(new BooleanPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            getContext().getString(R.string.preference_show_drawable)
                    ),
                    hasDrawable(),
                    new PreferenceData.OnPreferenceChangeListener<Boolean>() {
                        @Override
                        public void onPreferenceChange(Boolean preference) {
                            putPreference(PreferenceIdentifier.ICON_VISIBILITY, preference);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));
        }

        if (canHazText() && (hasDrawable() || !hasText())) {
            preferences.add(new BooleanPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            getContext().getString(R.string.preference_show_text)
                    ),
                    hasText(),
                    new PreferenceData.OnPreferenceChangeListener<Boolean>() {
                        @Override
                        public void onPreferenceChange(Boolean preference) {
                            putPreference(PreferenceIdentifier.TEXT_VISIBILITY, preference);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));
        }

        preferences.addAll(Arrays.asList(
                new ListPreferenceData(
                        getContext(),
                        new PreferenceData.Identifier(
                                getContext().getString(R.string.preference_gravity)
                        ),
                        new PreferenceData.OnPreferenceChangeListener<Integer>() {
                            @Override
                            public void onPreferenceChange(Integer preference) {
                                putPreference(PreferenceIdentifier.GRAVITY, preference);
                                StaticUtils.updateStatusService(getContext());
                            }
                        },
                        getGravity(),
                        new ListPreferenceData.ListPreference(
                                getContext().getString(R.string.gravity_left),
                                LEFT_GRAVITY
                        ),
                        new ListPreferenceData.ListPreference(
                                getContext().getString(R.string.gravity_center),
                                CENTER_GRAVITY
                        ),
                        new ListPreferenceData.ListPreference(
                                getContext().getString(R.string.gravity_right),
                                RIGHT_GRAVITY
                        )
                ),
                new IntegerPreferenceData(
                        getContext(),
                        new PreferenceData.Identifier(
                                getContext().getString(R.string.preference_icon_padding)
                        ),
                        getIconPadding(),
                        getContext().getString(R.string.unit_dp),
                        null,
                        null,
                        new PreferenceData.OnPreferenceChangeListener<Integer>() {
                            @Override
                            public void onPreferenceChange(Integer preference) {
                                putPreference(PreferenceIdentifier.ICON_PADDING, preference);
                                StaticUtils.updateStatusService(getContext());
                            }
                        }
                )
        ));

        if (hasDrawable()) {
            preferences.add(new IntegerPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            getContext().getString(R.string.preference_icon_scale)
                    ),
                    getIconScale(),
                    getContext().getString(R.string.unit_dp),
                    0,
                    null,
                    new PreferenceData.OnPreferenceChangeListener<Integer>() {
                        @Override
                        public void onPreferenceChange(Integer preference) {
                            putPreference(PreferenceIdentifier.ICON_SCALE, preference);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));
        }

        if (hasText()) {
            preferences.add(new IntegerPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            getContext().getString(R.string.preference_text_size)
                    ),
                    (int) getTextSize(),
                    getContext().getString(R.string.unit_sp),
                    0,
                    null,
                    new PreferenceData.OnPreferenceChangeListener<Integer>() {
                        @Override
                        public void onPreferenceChange(Integer preference) {
                            putPreference(PreferenceIdentifier.TEXT_SIZE, preference);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));

            Integer color = getTextColor();
            preferences.add(new ColorPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            "Text Color"
                    ),
                    color != null ? color : Color.WHITE,
                    new PreferenceData.OnPreferenceChangeListener<Integer>() {
                        @Override
                        public void onPreferenceChange(Integer preference) {
                            putPreference(PreferenceIdentifier.TEXT_COLOR, preference);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));
        }

        if (hasDrawable()) {
            preferences.add(new IconPreferenceData(
                    getContext(),
                    new PreferenceData.Identifier(
                            getContext().getString(R.string.preference_icon_style)
                    ),
                    style,
                    this,
                    new PreferenceData.OnPreferenceChangeListener<IconStyleData>() {
                        @Override
                        public void onPreferenceChange(IconStyleData preference) {
                            style = preference;
                            putPreference(PreferenceIdentifier.ICON_STYLE, preference.name);
                            StaticUtils.updateStatusService(getContext());
                        }
                    }
            ));
        }

        return preferences;
    }

    public int getIconStyleSize() {
        return 0;
    }

    public List<IconStyleData> getIconStyles() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        List<IconStyleData> styles = new ArrayList<>();
        String[] names = getStringArrayPreference(PreferenceIdentifier.ICON_STYLE_NAMES);
        if (names != null) {
            for (String name : names) {
                IconStyleData style = IconStyleData.fromSharedPreferences(prefs, getClass().getName(), name);
                if (style != null) styles.add(style);
            }
        }

        return styles;
    }

    public final void addIconStyle(IconStyleData style) {
        if (style.getSize() == getIconStyleSize()) {
            String[] names = getStringArrayPreference(PreferenceIdentifier.ICON_STYLE_NAMES);
            List<String> list = new ArrayList<>();
            if (names != null) list.addAll(Arrays.asList(names));

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            style.writeToSharedPreferences(editor, getClass().getName());
            editor.apply();

            list.add(style.name);
            putPreference(PreferenceIdentifier.ICON_STYLE_NAMES, list.toArray(new String[list.size()]));
        }
    }

    public final void removeIconStyle(IconStyleData style) {
        String[] names = getStringArrayPreference(PreferenceIdentifier.ICON_STYLE_NAMES);
        List<String> list = new ArrayList<>();
        if (names != null) list.addAll(Arrays.asList(names));

        list.remove(style.name);
        putPreference(PreferenceIdentifier.ICON_STYLE_NAMES, list.toArray(new String[list.size()]));
    }

    @Nullable
    public final Boolean getBooleanPreference(PreferenceIdentifier identifier) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(getIdentifierString(identifier))) {
            try {
                return prefs.getBoolean(getIdentifierString(identifier), false);
            } catch (ClassCastException e) {
                return null;
            }
        } else
            return null;
    }

    @Nullable
    public final Integer getIntegerPreference(PreferenceIdentifier identifier) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(getIdentifierString(identifier))) {
            try {
                return prefs.getInt(getIdentifierString(identifier), 0);
            } catch (ClassCastException e) {
                return null;
            }
        } else
            return null;
    }

    @Nullable
    public final String getStringPreference(PreferenceIdentifier identifier) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(getIdentifierString(identifier))) {
            try {
                return prefs.getString(getIdentifierString(identifier), null);
            } catch (ClassCastException e) {
                return null;
            }
        } else
            return null;
    }

    public final String[] getStringArrayPreference(PreferenceIdentifier identifier) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(getIdentifierString(identifier) + "-length")) {
            String[] array = new String[prefs.getInt(getIdentifierString(identifier) + "-length", 0)];
            for (int i = 0; i < array.length; i++) {
                array[i] = prefs.getString(getIdentifierString(identifier) + "-" + i, null);
            }

            return array;
        } else return null;
    }

    public final void putPreference(PreferenceIdentifier identifier, boolean object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(getIdentifierString(identifier), object).apply();
    }

    public final void putPreference(PreferenceIdentifier identifier, int object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getIdentifierString(identifier), object).apply();
    }

    public final void putPreference(PreferenceIdentifier identifier, String object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(getIdentifierString(identifier), object).apply();
    }

    public final void putPreference(PreferenceIdentifier identifier, int[] object) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();

        prefs.edit().putInt(getIdentifierString(identifier) + "-length", object.length).apply();

        for (int i = 0; i < object.length; i++) {
            prefs.edit().putString(getIdentifierString(identifier) + "-" + i, resources.getResourceEntryName(object[i])).apply();
        }
    }

    public final void putPreference(PreferenceIdentifier identifier, String[] object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putInt(getIdentifierString(identifier) + "-length", object.length);
        for (int i = 0; i < object.length; i++) {
            editor.putString(getIdentifierString(identifier) + "-" + i, object[i]);
        }

        editor.apply();
    }

    private String getIdentifierString(PreferenceIdentifier identifier) {
        return getClass().getName() + "/" + identifier.toString();
    }

    public void onProcessUpdate(int current, int max) {
        if (!isActive()) return;
        Log.d(TAG, "onProcessUpdate() called with: current = [" + current + "], max = [" + max + "]");
        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.setMax(max);
            progressBar.setProgress(current);
        }
    }

    public enum PreferenceIdentifier {
        VISIBILITY,
        POSITION,
        GRAVITY,
        TEXT_VISIBILITY,
        TEXT_FORMAT,
        TEXT_SIZE,
        TEXT_COLOR,
        ICON_VISIBILITY,
        ICON_STYLE,
        ICON_STYLE_NAMES,
        ICON_PADDING,
        ICON_SCALE
    }

    public interface DrawableListener {
        void onUpdate(@Nullable Drawable drawable);
    }

    public interface TextListener {
        void onUpdate(@Nullable String text);
    }

    public static class ProgressType {
        public static final int CPU_CLOCK = 0;
        public static final int CPU_TEMP = 1;
        public static final int RAM = 2;
        public static final int BATTERY = 3;
        public static final int INTERNAL_MEMORY = 4;
        public static final int EXTERNAL_MEMORY = 5;
        public static final int INTERNET_UP = 6;
        public static final int INTERNET_DOWN = 7;
        public static final int WIFI = 8;
        public static final int NETWORK_SIGN = 9;
    }
}
