package net.arvin.selector.uis.widgets.editable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

import net.arvin.selector.R;
import net.arvin.selector.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2020/8/9 00:17
 * Function：
 * Desc：
 */
public class PaintingHelper extends BaseEditHelper {
    private List<PaintingPath> historyPaintingPath;
    private PaintingPath currPath;

    private Matrix matrix;
    private Matrix currMatrix;

    private Paint paint;
    private float paintSize;
    private int currColor;
    private int currType;
    private float lastX;
    private float lastY;

    private Shader mscShader;
    private Shader blurShader;
    private int imagePaintSize;

    public PaintingHelper(EditableView targetView) {
        super(targetView);

        historyPaintingPath = new ArrayList<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSize = UiUtil.dp2px(targetView.getContext(), 6);
        paint.setStrokeWidth(paintSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        matrix = new Matrix();
        currMatrix = new Matrix();

        Bitmap mscBitmap = BitmapFactory.decodeResource(targetView.getResources(), R.drawable.ps_img_msc_bg);
        Bitmap blurBitmap = BitmapFactory.decodeResource(targetView.getResources(), R.drawable.ps_img_blur_bg);
        mscShader = new BitmapShader(mscBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        blurShader = new BitmapShader(blurBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        imagePaintSize = mscBitmap.getWidth();
    }

    public void setColor(int color, int type) {
        this.currColor = color;
        this.currType = type;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float scale = targetView.getAttacher().getScale();
                float size = scale > 1 ? (paintSize / scale) : paintSize;
                if (imgMatrix == null) {
                    setImageMatrix();
                }
                imgMatrix.invert(currMatrix);
                currPath = new PaintingPath(currColor, currType, size, currMatrix);
                historyPaintingPath.add(currPath);
                lastX = event.getX();
                lastY = event.getY();
                currPath.path.moveTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float tempX = event.getX();
                float tempY = event.getY();
                currPath.path.quadTo(lastX, lastY, (lastX + tempX) / 2, (lastY + tempY) / 2);
                lastX = tempX;
                lastY = tempY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (PaintingPath path : historyPaintingPath) {
            canvas.save();
            matrix.reset();
            matrix.set(path.matrix);
            matrix.postConcat(imgMatrix);
            canvas.setMatrix(matrix);
            paint.setStrokeWidth(path.paintSize);
            paint.setShader(null);
            if (path.type == PaintColorBarLayout.OnColorSelectListener.TYPE_COLOR) {
                paint.setColor(path.color);
            } else {
                if (path.color == -2) {
                    paint.setStrokeWidth(imagePaintSize);
                    paint.setShader(mscShader);
                } else {
                    paint.setStrokeWidth(imagePaintSize);
                    paint.setShader(blurShader);
                }
            }
            canvas.drawPath(path.path, paint);
            canvas.restore();
        }
    }

    public void rollback() {
        if (historyPaintingPath.size() > 0) {
            historyPaintingPath.remove(historyPaintingPath.size() - 1);
            invalidate();
        }
    }

    public void reset() {
        if (historyPaintingPath.size() > 0) {
            historyPaintingPath.clear();
            invalidate();
        }
    }

    public static class PaintingPath {
        public Path path;
        public float paintSize;
        public int color;
        public int type;
        public Matrix matrix;

        public PaintingPath(int color, int type, float paintSize, Matrix matrix) {
            this.color = color;
            this.type = type;
            this.paintSize = paintSize;
            this.matrix = matrix;
            this.path = new Path();
        }
    }
}
