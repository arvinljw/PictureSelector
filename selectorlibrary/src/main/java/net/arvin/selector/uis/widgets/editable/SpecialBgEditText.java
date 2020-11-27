package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import net.arvin.selector.R;
import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/17 10:46
 * Function：
 * Desc：
 */
public class SpecialBgEditText extends AppCompatEditText {
    private Path path;
    private RectF roundRect;
    private RectF lastLinRect;
    private RectF arcRect;
    private Paint bgPaint;
    private float[] radii;
    private float[] lastRadii;
    private float radius;
    private boolean showBg;

    public SpecialBgEditText(@NonNull Context context) {
        this(context, null);
    }

    public SpecialBgEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpecialBgEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
        roundRect = new RectF();
        lastLinRect = new RectF();
        arcRect = new RectF();

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radii = new float[8];
        lastRadii = new float[8];
        radius = UiUtil.dp2px(getContext(), 8);
    }

    public void setBgColor(int bgColor) {
        bgPaint.setColor(bgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (showBg && getText().length() > 0) {
            int lineCount = getLineCount();

            boolean isShowAllLine = calculateRect(lineCount);

            calculateRadii(lineCount, isShowAllLine);

            addPath(lineCount, isShowAllLine);

            canvas.drawPath(path, bgPaint);
        }

        super.onDraw(canvas);
    }

    private boolean calculateRect(int lineCount) {
        roundRect.setEmpty();
        lastLinRect.setEmpty();
        if (lineCount > 0) {
            for (int i = 0; i < lineCount; i++) {
                boolean isLast = i == lineCount - 1;
                if (i == 0) {
                    roundRect.left = getLayout().getLineLeft(0);
                    roundRect.top = getLayout().getLineTop(0);
                    roundRect.right = getPaddingLeft() + getPaddingRight() + getLayout().getLineRight(0);
                    roundRect.bottom = getPaddingTop() + (isLast ? getPaddingBottom() : 0) + getLayout().getLineBottom(0);
                } else if (i < lineCount - 1) {
                    roundRect.bottom = roundRect.bottom + getLayout().getLineBottom(i) - getLayout().getLineTop(i);
                }
                roundRect.right = Math.max(roundRect.right, getPaddingLeft() + getPaddingRight() + getLayout().getLineRight(i));

                if (i == lineCount - 1) {
                    lastLinRect.left = getLayout().getLineLeft(lineCount - 1);
                    lastLinRect.right = getPaddingLeft() + getPaddingRight() + getLayout().getLineRight(lineCount - 1);
                    lastLinRect.top = getPaddingTop() + getLayout().getLineTop(lineCount - 1);
                    lastLinRect.bottom = getHeight();
                }
            }

        }

        boolean isShowAllLine = lastLinRect.width() + radius * 2 >= roundRect.width();
        if (isShowAllLine) {
            roundRect.bottom = getHeight();
        }
        return isShowAllLine;
    }

    private void calculateRadii(int lineCount, boolean isShowAllLine) {
        if (lineCount > 1) {
            if (isShowAllLine) {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = radius;
            } else {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = 0;
            }
        } else if (lineCount == 1) {
            if (isShowAllLine) {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = radius;
            }
        }

        lastRadii[0] = lastRadii[1] = 0;
        lastRadii[2] = lastRadii[3] = 0;
        lastRadii[4] = lastRadii[5] = radius;
        lastRadii[6] = lastRadii[7] = radius;
    }

    private void addPath(int lineCount, boolean isShowAllLine) {
        path.reset();
        if (lineCount > 0) {
            if (lineCount > 1 || isShowAllLine) {
                path.addRoundRect(roundRect, radii, Path.Direction.CW);
            }
            if (!isShowAllLine) {
                path.addRoundRect(lastLinRect, lastRadii, Path.Direction.CW);

                arcRect.left = lastLinRect.right;
                arcRect.top = roundRect.bottom;
                arcRect.right = arcRect.left + radius * 2;
                arcRect.bottom = arcRect.top + radius * 2;
                path.addArc(arcRect, 180, 90);
                path.lineTo(lastLinRect.right, roundRect.bottom);
                path.lineTo(lastLinRect.right, arcRect.bottom);
                path.close();
            }
        }
    }

    public void setShowBg(boolean showBg) {
        this.showBg = showBg;
    }
}
