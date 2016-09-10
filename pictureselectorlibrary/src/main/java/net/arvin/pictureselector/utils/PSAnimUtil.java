package net.arvin.pictureselector.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * created by arvin on 16/9/3 18:39
 * email：1035407623@qq.com
 */
public class PSAnimUtil {
    private static ObjectAnimator downHideAnim;
    private static ObjectAnimator upShowAnim;
    private static ObjectAnimator downShowAnim;
    private static ObjectAnimator upHideAnim;

    public static ObjectAnimator startDownHideAnim(View view) {
        downHideAnim = ObjectAnimator.ofFloat(view, "translationY", 0, ScreenUtil.getScreenHeight() - view.getTop());
        if (upShowAnim != null) {
            upShowAnim.cancel();
        }
        downHideAnim.addListener(new AnimationEnd(downHideAnim));
        downHideAnim.start();
        return downHideAnim;
    }

    public static ObjectAnimator startUpShowAnim(View view) {
        upShowAnim = ObjectAnimator.ofFloat(view, "translationY", 0);
        if (downHideAnim != null) {
            downHideAnim.cancel();
        }
        upShowAnim.addListener(new AnimationEnd(upShowAnim));
        upShowAnim.start();
        return upShowAnim;
    }

    public static ObjectAnimator startDownShowAnim(View view) {
        downShowAnim = ObjectAnimator.ofFloat(view, "translationY", 0);
        if (upHideAnim != null) {
            upHideAnim.cancel();
        }
        downShowAnim.addListener(new AnimationEnd(downShowAnim));
        downShowAnim.start();
        return downShowAnim;
    }

    public static ObjectAnimator startUpHideAnim(View view) {
        upHideAnim = ObjectAnimator.ofFloat(view, "translationY", 0, -view.getHeight());
        if (downShowAnim != null) {
            downShowAnim.cancel();
        }
        upHideAnim.addListener(new AnimationEnd(upHideAnim));
        upHideAnim.start();
        return upHideAnim;
    }

    static class AnimationEnd extends AnimatorListenerAdapter {
        ObjectAnimator animator;

        public AnimationEnd(ObjectAnimator animator) {
            this.animator = animator;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animator = null;
        }
    }

}
