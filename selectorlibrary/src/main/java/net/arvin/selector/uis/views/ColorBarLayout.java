package net.arvin.selector.uis.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.arvin.selector.R;
import net.arvin.selector.uis.fragments.EditFragment;
import net.arvin.selector.utils.PSScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 18/1/8 16:19
 * Function：
 * Desc：
 */
public class ColorBarLayout extends FrameLayout implements View.OnClickListener {
    private List<Integer> mCircleViewIds;
    private int mSelectedId;
    private OnColorSelectedListener mListener;
    private int mNormalSize;
    private int mSelectedSize;

    public ColorBarLayout(Context context) {
        this(context, null);
    }

    public ColorBarLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCircleViewIds = new ArrayList<>();
        mNormalSize = PSScreenUtil.dp2px(8);
        mSelectedSize = PSScreenUtil.dp2px(10);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_layout_color_bar, this);

        mCircleViewIds.add(R.id.ps_circle_white);
        mCircleViewIds.add(R.id.ps_circle_black);
        mCircleViewIds.add(R.id.ps_circle_red);
        mCircleViewIds.add(R.id.ps_circle_yellow);
        mCircleViewIds.add(R.id.ps_circle_green);
        mCircleViewIds.add(R.id.ps_circle_blue);
        mCircleViewIds.add(R.id.ps_circle_purple);
        mCircleViewIds.add(R.id.ps_circle_pink);
        mSelectedId = R.id.ps_circle_red;

        CircleView lastView = findViewById(mSelectedId);
        lastView.setRadius(mSelectedSize);

        for (Integer circleViewId : mCircleViewIds) {
            findViewById(circleViewId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        CircleView lastView = findViewById(mSelectedId);
        lastView.setRadius(mNormalSize);

        CircleView selectedView = findViewById(v.getId());
        selectedView.setRadius(mSelectedSize);
        mSelectedId = v.getId();

        if (mListener != null) {
            mListener.onColorSelected(v, selectedView.getColor());
        }
    }

    public int getColor() {
        CircleView lastView = findViewById(mSelectedId);
        return lastView.getColor();
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.mListener = listener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(View v, int color);
    }
}
