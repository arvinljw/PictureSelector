package net.arvin.pictureselector.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.utils.ScreenUtil;

/**
 * created by arvin on 16/9/3 12:11
 * email：1035407623@qq.com
 */
public class FolderLayout extends FrameLayout {
    private final int dividerHeight = ScreenUtil.dp2px(1);
    private final int outerDividerColor = R.color.black_hint;
    private final int innerDividerColor = R.color.black_secondary;
    private Paint paint;


    public FolderLayout(Context context) {
        this(context, null, 0);
    }

    public FolderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        paint = new Paint();
        paint.setStrokeWidth(dividerHeight);
        paint.setAntiAlias(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(outerDividerColor));
        int margin = dividerHeight * 2;
        canvas.drawLine(getWidth() - dividerHeight, margin, getWidth() - dividerHeight, getHeight() - dividerHeight, paint);
        canvas.drawLine(margin, getHeight() - dividerHeight, getWidth() - dividerHeight, getHeight() - dividerHeight, paint);

        margin = dividerHeight * 3;
        paint.setColor(getResources().getColor(innerDividerColor));
        canvas.drawLine(getWidth() - margin, dividerHeight, getWidth() - margin, getHeight() - margin, paint);
        canvas.drawLine(dividerHeight, getHeight() - margin, getWidth() - margin, getHeight() - margin, paint);
    }
}
