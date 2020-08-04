package net.arvin.selector.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.uis.fragments.PreviewFragment;

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

    public static boolean isShowPreviewFragment(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(PreviewFragment.class.getName());
        if (fragment != null && !fragment.isHidden()) {
            hideFragment(activity, fragment);
            return true;
        }
        return false;
    }

}
