package com.duy.notifi.statusbar.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duy.notifi.R;
import com.duy.notifi.statusbar.data.monitor.ProgressIcon;
import com.duy.notifi.statusbar.utils.ColorUtils;
import com.duy.notifi.statusbar.utils.ImageUtils;
import com.duy.notifi.statusbar.utils.StaticUtils;

import java.util.ArrayList;
import java.util.List;

import static com.duy.notifi.statusbar.utils.PreferenceUtils.PreferenceIdentifier;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.getBooleanPreference;
import static com.duy.notifi.statusbar.utils.PreferenceUtils.getIntegerPreference;

public class StatusView extends FrameLayout {

    private LinearLayout statusView;
    private float x, y;
    private int burnInOffsetX, burnInOffsetY;
    @ColorInt
    private Integer color, iconColor = Color.WHITE;
    private boolean isSystemShowing;
    private boolean isFullscreen;
    private boolean isAnimations;
    private boolean isRegistered;
    private List<ProgressIcon> icons;
    private Handler handler;
    private Runnable burnInRunnable = new Runnable() {
        @Override
        public void run() {
            if (statusView != null && statusView.getParent() != null) {
                ViewGroup.LayoutParams layoutParams = statusView.getLayoutParams();

                switch (burnInOffsetX) {
                    case 0:
                        statusView.setX(x - 1);
                        burnInOffsetX++;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        statusView.setX(x + 1);
                        burnInOffsetX++;
                        break;
                    case 6:
                        statusView.setX(x - 1);
                        burnInOffsetX++;
                        break;
                    case 7:
                        statusView.setX(x - 1);
                        burnInOffsetX = 0;
                        break;
                    default:
                        statusView.setX(x);
                        burnInOffsetX++;
                }

                switch (burnInOffsetY) {
                    case 0:
                    case 1:
                    case 2:
                        statusView.setY(y + 1);
                        burnInOffsetY++;
                        break;
                    case 4:
                    case 5:
                    case 6:
                        statusView.setY(y - 1);
                        burnInOffsetY++;
                        break;
                    case 7:
                        statusView.setY(y);
                        burnInOffsetY = 0;
                        break;
                    default:
                        statusView.setY(y);
                        burnInOffsetY++;
                }

                statusView.setLayoutParams(layoutParams);
            }

            handler.postDelayed(this, 2000);
        }
    };

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler();
    }

    public LinearLayout getStatusView() {
        return statusView;
    }

    public void setUp() {
        if (statusView != null && statusView.getParent() != null) removeView(statusView);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_status, this, false);
        statusView = v.findViewById(R.id.status);
        statusView.getLayoutParams().height = StaticUtils.getStatusBarHeight(getContext());

        Boolean isAnimations = getBooleanPreference(getContext(), PreferenceIdentifier.STATUS_BACKGROUND_ANIMATIONS);
        this.isAnimations = isAnimations != null ? isAnimations : true;

        Boolean isContrastIcons = getBooleanPreference(getContext(), PreferenceIdentifier.STATUS_DARK_ICONS);

        addView(v);
        statusView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                x = statusView.getX();
                y = statusView.getY();

                Boolean isBurnInProtection = getBooleanPreference(getContext(), PreferenceIdentifier.STATUS_BURNIN_PROTECTION);
                if (isBurnInProtection != null && isBurnInProtection)
                    handler.post(burnInRunnable);

                statusView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Boolean isStatusColorAuto = getBooleanPreference(getContext(), PreferenceIdentifier.STATUS_COLOR_AUTO);
        if (isStatusColorAuto != null && !isStatusColorAuto) {
            Integer statusBarColor = getIntegerPreference(getContext(), PreferenceIdentifier.STATUS_COLOR);
            if (statusBarColor != null) setColor(statusBarColor);
        } else if (color != null) setColor(color);
        else setColor(Color.BLACK);

        Integer defaultIconColor = getIntegerPreference(getContext(), PreferenceIdentifier.STATUS_ICON_COLOR);
        if (defaultIconColor != null) iconColor = defaultIconColor;
    }

    public List<ProgressIcon> getIcons() {
        if (icons == null) icons = new ArrayList<>();
        return icons;
    }

    public void setIcons(List<ProgressIcon> icons) {
     /*   for (int i = (leftLayout.getChildCount() - 1); i >= 0; i--) {
            View child = leftLayout.getChildAt(i);
            Object tag = child.getTag();

            if (tag != null && tag instanceof IconData) {
                ((IconData) tag).unregister();
                leftLayout.removeViewAt(i);
            }
        }

        for (int i = (centerLayout.getChildCount() - 1); i >= 0; i--) {
            View child = centerLayout.getChildAt(i);
            Object tag = child.getTag();

            if (tag != null && tag instanceof IconData) {
                ((IconData) tag).unregister();
                centerLayout.removeViewAt(i);
            }
        }

        for (int i = (rightLayout.getChildCount() - 1); i >= 0; i--) {
            View child = rightLayout.getChildAt(i);
            Object tag = child.getTag();

            if (tag != null && tag instanceof IconData) {
                ((IconData) tag).unregister();
                rightLayout.removeViewAt(i);
            }
        }*/

        this.icons = icons;

        for (final ProgressIcon iconData : icons) {
            if (!iconData.isVisible()) continue;

            final View item = iconData.getIconView();
            if (!iconData.isActive()) {
                item.setVisibility(View.INVISIBLE);
            } else {
                item.setVisibility(View.VISIBLE);
            }
            iconData.setDrawableListener(new ProgressIcon.DrawableListener() {
                @Override
                public void onUpdate(@Nullable Drawable drawable) {
                    CustomImageView iconView = item.findViewById(R.id.icon);

                    if (drawable != null && iconView != null)
                        iconView.setImageDrawable(drawable, iconColor);
                    else if (iconView == null || !iconView.getParent().equals(item))
                        setIconTint(item, iconColor);
                }
            });

          /*  switch (iconData.getGravity()) {
                case IconData.LEFT_GRAVITY:
                    leftLayout.addView(item, 0);
                    break;
                case IconData.CENTER_GRAVITY:
                    centerLayout.addView(item, 0);
                    break;
                case IconData.RIGHT_GRAVITY:
                    rightLayout.addView(item, 0);
                    break;
            }*/
        }
    }

    public void register() {
        if (icons != null && !isRegistered()) {
            for (ProgressIcon icon : icons) {
                icon.register();
            }
            isRegistered = true;
        }
    }

    public void unregister() {
        if (icons != null && isRegistered()) {
            for (ProgressIcon icon : icons) {
                icon.unregister();
            }
            isRegistered = false;
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean isSystemShowing() {
        return isSystemShowing;
    }

    public void setSystemShowing(boolean isSystemShowing) {
        if ((this.isFullscreen != isSystemShowing || this.isSystemShowing != isSystemShowing) && isSystemShowing)
            setStatusBarVisibility(false);
        this.isSystemShowing = isSystemShowing;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean isFullscreen) {
        if (((getVisibility() == View.GONE) != isFullscreen) && !isSystemShowing) {
            setStatusBarVisibility(!isFullscreen);
        }

        this.isFullscreen = isFullscreen;
    }

    private void setStatusBarVisibility(final boolean visible) {
        if (isAnimations) {
            ValueAnimator animator = ValueAnimator.ofFloat(getY(), visible ? 0 : -StaticUtils.getStatusBarHeight(getContext()));
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float y = (float) valueAnimator.getAnimatedValue();
                    setY(y);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    if (visible) setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!visible) setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            animator.start();
        } else {
            if (visible) setVisibility(View.VISIBLE);
            else setVisibility(View.GONE);
        }
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        if (this.color == null) this.color = Color.BLACK;
        color = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
        this.color = color;
    }

    @ColorInt
    private int getDefaultColor() {
        Integer color = getIntegerPreference(getContext(), PreferenceIdentifier.STATUS_COLOR);
        if (color == null) color = Color.BLACK;
        return color;
    }

    @ColorInt
    private int getDefaultIconColor() {
        Integer color = getIntegerPreference(getContext(), PreferenceIdentifier.STATUS_ICON_COLOR);
        if (color == null) color = Color.WHITE;
        return color;
    }


    private void setIconTint(View view, @ColorInt int color) {
        for (ProgressIcon icon : getIcons()) {
            icon.setColor(color);
        }

        if (view instanceof LinearLayout) {
            for (int i = 0; i < ((LinearLayout) view).getChildCount(); i++) {
                setIconTint(((LinearLayout) view).getChildAt(i), color);
            }
        } else if (view instanceof TextView) {
            if (view.getTag() == null) {
                ((TextView) view).setTextColor(color);
            }
        } else if (view instanceof CustomImageView) {
            CustomImageView imageView = (CustomImageView) view;
            if (imageView.getDrawable() != null) {
                imageView.setColorFilter(color);
            }
        }
    }


    public void setLockscreen(boolean lockscreen) {
        Boolean expand = getBooleanPreference(getContext(), PreferenceIdentifier.STATUS_LOCKSCREEN_EXPAND);
        if (expand != null && expand)
            statusView.getLayoutParams().height = StaticUtils.getStatusBarHeight(getContext()) * (lockscreen ? 3 : 1);

        if (lockscreen) {
            Palette.from(ImageUtils.drawableToBitmap(WallpaperManager.getInstance(getContext()).getFastDrawable())).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setColor(palette.getDarkVibrantColor(ColorUtils.darkColor(palette.getVibrantColor(Color.BLACK))));
                }
            });
        }
    }
}
