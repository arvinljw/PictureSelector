package net.arvin.selector.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.uis.fragments.EditFragment;
import net.arvin.selector.uis.fragments.PreviewFragment;
import net.arvin.selector.uis.widgets.editable.SpecialBgEditText;

/**
 * Created by arvinljw on 2020/8/1 17:33
 * Function：
 * Desc：
 */
public class UiUtil {

    public static float dp2px(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density * dp;
    }

    public static float sp2px(Context context, int sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.scaledDensity * sp;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static void dealEnsureText(TextView tvEnsure, int choseCount, int maxCount) {
        if (maxCount == 1) {
            tvEnsure.setVisibility(View.GONE);
            return;
        }
        tvEnsure.setEnabled(choseCount > 0);
        tvEnsure.setText(SelectorHelper.textEngine.ensureChoose(tvEnsure.getContext(), choseCount, maxCount));
    }

    public static void dealPreviewEnsureText(TextView tvEnsure, int choseCount, int maxCount) {
        tvEnsure.setText(SelectorHelper.textEngine.ensureChoose(tvEnsure.getContext(), choseCount, maxCount));
    }

    public static void dealPreviewText(TextView tvPreview, int choseCount) {
        tvPreview.setEnabled(choseCount != 0);
        tvPreview.setText(SelectorHelper.textEngine.previewCount(tvPreview.getContext(), choseCount));
    }

    public static void setDrawableLeft(TextView textView, int drawableLeftResId) {
        Drawable drawable = textView.getResources().getDrawable(drawableLeftResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    public static void showPreviewFragment(FragmentActivity activity, boolean previewAll, int pos) {
        PreviewFragment previewFragment;
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        String tag = PreviewFragment.class.getName();
        Fragment cacheFragment = supportFragmentManager.findFragmentByTag(tag);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PreviewFragment.KEY_PREVIEW_ALL, previewAll);
        bundle.putInt(PreviewFragment.KEY_PREVIEW_POS, pos);
        transaction.setCustomAnimations(R.anim.ps_fade_in, R.anim.ps_fade_out);
        if (cacheFragment == null) {
            previewFragment = new PreviewFragment();
            previewFragment.setArguments(bundle);
            transaction.add(R.id.ps_layout_container, previewFragment, tag);
        } else {
            previewFragment = (PreviewFragment) cacheFragment;
            previewFragment.update(bundle);
            transaction.show(previewFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    public static void hideFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.ps_fade_in, R.anim.ps_fade_out)
                .hide(fragment).commitAllowingStateLoss();
    }

    public static void removeFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.ps_fade_in, R.anim.ps_fade_out)
                .remove(fragment).commitAllowingStateLoss();
    }

    public static boolean isShowPreviewFragment(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(PreviewFragment.class.getName());
        if (fragment != null && !fragment.isHidden()) {
            hideFragment(activity, fragment);
            return true;
        }
        return false;
    }

    public static void addEditFragment(FragmentActivity activity, Media media) {
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.ps_fade_in, R.anim.ps_fade_out);
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EditFragment.KEY_MEDIA, media);
        fragment.setArguments(bundle);
        transaction.add(R.id.ps_layout_container, fragment, EditFragment.class.getName());
        transaction.commitAllowingStateLoss();
    }

    public static boolean hasEditFragment(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        EditFragment fragment = (EditFragment) fragmentManager.findFragmentByTag(EditFragment.class.getName());
        if (fragment == null) {
            return false;
        }
        if (fragment.hideEditView()) {
            return true;
        }
        removeFragment(activity, fragment);
        return true;
    }


    public static void setTint(ImageView imgPencil, Drawable drawable, int color) {
        if (drawable != null) {
            Drawable drawableWrap = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawableWrap, color);
            imgPencil.setImageDrawable(drawableWrap);
        }
    }

    public static void closeKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static float getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
