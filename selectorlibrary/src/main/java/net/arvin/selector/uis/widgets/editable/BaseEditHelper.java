package net.arvin.selector.uis.widgets.editable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import net.arvin.selector.uis.widgets.photoview.OnMatrixChangedListener;

/**
 * Created by arvinljw on 2020/9/11 16:02
 * Function：
 * Desc：
 */
public abstract class BaseEditHelper implements View.OnTouchListener, OnMatrixChangedListener {

    protected EditableView targetView;

    protected Matrix imgMatrix;
    protected float imgScale;

    public BaseEditHelper(EditableView targetView) {
        this.targetView = targetView;
        this.targetView.addOnMatrixChangeListener(this);

        imgMatrix = new Matrix();
    }

    @Override
    public void onMatrixChanged(RectF rect) {
        setImageMatrix();
        invalidate();
    }

    protected void setImageMatrix() {
        targetView.getAttacher().getSuppMatrix(imgMatrix);
        imgScale = targetView.getAttacher().getScale();
    }

    protected void invalidate() {
        targetView.invalidate();
    }

    protected abstract void onDraw(Canvas canvas);
}
