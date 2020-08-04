package net.arvin.selector.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;

/**
 * Created by arvinljw on 2020/8/1 17:30
 * Function：
 * Desc：
 */
public class AnimUtil {

    public static final int DURATION_500 = 500;
    public static final int DURATION_200 = 200;
    public static final int DURATION_50 = 50;

    public static void removeChangeAnim(RecyclerView recyclerView) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }

    public static void rotation(final ImageView imgArrow, final boolean isOpen) {
        if (imgArrow.getTag() instanceof ObjectAnimator) {
            ObjectAnimator anim = (ObjectAnimator) imgArrow.getTag();
            anim.cancel();
        }
        float start, end;
        if (isOpen) {
            start = 0;
            end = 180;
        } else {
            start = 180;
            end = 360;
        }

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(imgArrow, "rotation", start, end).setDuration(DURATION_200);
        rotationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isOpen) {
                    imgArrow.setRotation(0);
                }
            }
        });
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.start();
        imgArrow.setTag(rotationAnim);
    }

    public static void folderOpenHide(final View layoutFolder, View folderList, int maxHeight, final boolean isOpen) {
        if (layoutFolder.getTag() instanceof ObjectAnimator) {
            ObjectAnimator anim = (ObjectAnimator) layoutFolder.getTag();
            anim.cancel();
        }
        float start, end;
        int startA, endA;
        if (isOpen) {
            start = -maxHeight;
            end = 0;

            startA = 0x00;
            endA = 0x88;
        } else {
            start = 0;
            end = -maxHeight;
            startA = 0x88;
            endA = 0x00;
        }
        if (isOpen) {
            layoutFolder.setVisibility(View.VISIBLE);
        }
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(folderList, "translationY", start, end);
        translationYAnim.setDuration(DURATION_200);

        ValueAnimator alphaAnim = ValueAnimator.ofInt(startA, endA);
        alphaAnim.setDuration(DURATION_50);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (int) animation.getAnimatedValue();
                int bgColor = Color.argb(alpha, 0, 0, 0);
                layoutFolder.setBackgroundColor(bgColor);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isOpen) {
                    layoutFolder.setVisibility(View.GONE);
                }
            }
        });
        animatorSet.playTogether(translationYAnim, alphaAnim);
        animatorSet.start();

        layoutFolder.setTag(animatorSet);
    }

    public static void fadeShow(final View view) {
        if (view.getAlpha() == 1) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(DURATION_200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public static void fadeHide(final View view) {
        if (view.getAlpha() == 0) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(DURATION_200);
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
            }
        }, DURATION_500);
    }

    public static void fadeHideBar(final View view) {
        if (view.getAlpha() == 0) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(DURATION_200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    public static void fadeShowBar(View view, boolean showNow) {
        view.setVisibility(View.VISIBLE);
        if (showNow) {
            view.setAlpha(1);
            return;
        }
        if (view.getAlpha() == 1) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(DURATION_200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public static void playVideo(Context context, Media media) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(media.getUri(), "video/*");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, SelectorHelper.textEngine.notFoundVideoPlayer(context), Toast.LENGTH_SHORT).show();
        }
    }

    public static void translationY(View target, boolean show, int height, AnimatorListenerAdapter listenerAdapter) {
        float startY;
        float endY;

        if (show) {
            target.setVisibility(View.VISIBLE);
            startY = -height;
            endY = 0;
        } else {
            startY = 0;
            endY = -height;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(target, "translationY", startY, endY).setDuration(DURATION_200);
        anim.setInterpolator(new LinearInterpolator());
        if (listenerAdapter != null) {
            anim.addListener(listenerAdapter);
        }
        anim.start();
    }
}
