package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.arvin.selector.R;
import net.arvin.selector.uis.fragments.EditFragment;
import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/13 18:26
 * Function：
 * Desc：
 */
public class PaintColorBarLayout extends LinearLayout {
    public static final int BAR_STYLE_PAINTING = 0;
    public static final int BAR_STYLE_EDITING = 1;

    private int barStyle;
    private int[] colors;

    private int selectPos;
    private int startSize;
    private LayoutParams params;
    private OnColorSelectListener colorSelectListener;

    public PaintColorBarLayout(Context context) {
        this(context, null);
    }

    public PaintColorBarLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintColorBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PaintColorBarLayout);
        barStyle = typedArray.getInt(R.styleable.PaintColorBarLayout_barStyle, BAR_STYLE_PAINTING);
        typedArray.recycle();
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        initData();
    }

    private void initData() {
        initColors();
        addColorViews();
    }

    private void initColors() {
        colors = new int[7];
        colors[0] = R.color.ps_paint_color_bar_white;
        colors[1] = R.color.ps_paint_color_bar_black;
        colors[2] = R.color.ps_paint_color_bar_red;
        colors[3] = R.color.ps_paint_color_bar_yellow;
        colors[4] = R.color.ps_paint_color_bar_green;
        colors[5] = R.color.ps_paint_color_bar_blue;
        colors[6] = R.color.ps_paint_color_bar_purple;
    }

    private void addColorViews() {
        params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        startSize = 0;
        selectPos = 0;
        if (barStyle == BAR_STYLE_PAINTING) {
            startSize = 2;
            selectPos = 2;
        }

        int size = colors.length + startSize;
        selectPos += startSize;

        float itemSize = UiUtil.dp2px(getContext(), 24);
        float horizontalSpacing = (UiUtil.getScreenWidth(getContext()) - itemSize * size - getPaddingLeft() - getPaddingRight()) / (size * 2);

        if (barStyle == BAR_STYLE_PAINTING) {
            addImages(itemSize, horizontalSpacing);
        }

        for (int i = 0; i < colors.length; i++) {
            addView(getColorView(itemSize, horizontalSpacing, i), params);
        }

        getChildAt(selectPos).setSelected(true);
    }

    private ColorView getColorView(float itemSize, float horizontalSpacing, final int pos) {
        int color = colors[pos];
        ColorView colorView = new ColorView(getContext());
        int padding = (int) horizontalSpacing;
        colorView.setPadding(padding, padding, padding, padding);
        colorView.setItemSize(itemSize + horizontalSpacing * 2);
        colorView.setColorId(color);
        addClickListener(colorView, startSize + pos);

        return colorView;
    }

    private void addImages(float itemSize, float horizontalSpacing) {
        addView(getMscView(itemSize, horizontalSpacing), params);
        addView(getBlurView(itemSize, horizontalSpacing), params);
    }

    private ColorView getMscView(float itemSize, float horizontalSpacing) {
        ColorView mscView = new ColorView(getContext());
        int padding = (int) horizontalSpacing;
        mscView.setPadding(padding, padding, padding, padding);
        mscView.setItemSize(itemSize + horizontalSpacing * 2);
        mscView.setImageId(R.drawable.ps_img_msc);
        addClickListener(mscView, 0);
        return mscView;
    }

    private ColorView getBlurView(float itemSize, float horizontalSpacing) {
        ColorView blurView = new ColorView(getContext());
        int padding = (int) horizontalSpacing;
        blurView.setPadding(padding, padding, padding, padding);
        blurView.setItemSize(itemSize + horizontalSpacing * 2);
        blurView.setImageId(R.drawable.ps_img_blur);
        addClickListener(blurView, 1);
        return blurView;
    }

    private void addClickListener(ColorView mscView, final int pos) {
        mscView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildAt(selectPos).setSelected(false);
                getChildAt(pos).setSelected(true);
                selectPos = pos;
                invalidate();
                if (colorSelectListener != null) {
                    int[] color = getPaintColor();
                    colorSelectListener.onColorSelected(color[0], color[1]);
                }
            }
        });
    }

    public int[] getPaintColor() {
        int index = selectPos;
        if (barStyle == BAR_STYLE_PAINTING) {
            index -= startSize;
        }
        if (index < 0) {
            return new int[]{index, OnColorSelectListener.TYPE_IMAGE};
        }
        if (barStyle == BAR_STYLE_EDITING && index == 0) {
            return new int[]{getResources().getColor(colors[index]), OnColorSelectListener.TYPE_COLOR_WITHE};
        }
        return new int[]{getResources().getColor(colors[index]), OnColorSelectListener.TYPE_COLOR};
    }

    public void setOnColorSelectListener(OnColorSelectListener listener) {
        this.colorSelectListener = listener;
    }

    public interface OnColorSelectListener {
        int TYPE_COLOR = 0;
        int TYPE_IMAGE = 1;
        //是颜色类型，输入文字是白色文字和背景设置的时候要设置为黑的
        int TYPE_COLOR_WITHE = 2;

        void onColorSelected(int color, int type);
    }
}
