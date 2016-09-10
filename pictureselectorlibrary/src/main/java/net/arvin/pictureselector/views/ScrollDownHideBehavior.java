package net.arvin.pictureselector.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import net.arvin.pictureselector.utils.ScreenUtil;

/**
 * Created by arvin on 2016/8/29 10:59
 */
public class ScrollDownHideBehavior extends CoordinatorLayout.Behavior {
    private final int MIN_SCROLL_DISTANCE = ScreenUtil.dp2px(8);
    private int offsetTotal = 0;
    private boolean isShow = true;
    private ObjectAnimator downHideAnim = null;
    private ObjectAnimator upShowAnim = null;

    public ScrollDownHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        offset(child, dyConsumed);
    }

    private void offset(View child, int dy) {
        offsetTotal += dy;
        if (Math.abs(offsetTotal) < MIN_SCROLL_DISTANCE) {
            return;
        }
        if (offsetTotal > 0 && isShow) {//上滑
            isShow = false;
            startDownHideAnim(child);
        }
        if (offsetTotal < 0 && !isShow) {//下滑
            isShow = true;
            startUpShowAnim(child);
        }
        offsetTotal = 0;
    }

    private void startDownHideAnim(View child) {
        downHideAnim = ObjectAnimator.ofFloat(child, "translationY", 0, ScreenUtil.getScreenHeight() - child.getTop());
        if (upShowAnim != null) {
            upShowAnim.cancel();
        }
        downHideAnim.addListener(new AnimationEnd(downHideAnim));
        downHideAnim.start();
    }

    private void startUpShowAnim(View child) {
        upShowAnim = ObjectAnimator.ofFloat(child, "translationY", 0);
        if (downHideAnim != null) {
            downHideAnim.cancel();
        }
        upShowAnim.addListener(new AnimationEnd(upShowAnim));
        upShowAnim.start();
    }

    class AnimationEnd extends AnimatorListenerAdapter {
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
