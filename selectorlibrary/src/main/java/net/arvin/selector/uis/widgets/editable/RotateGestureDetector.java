package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by arvinljw on 2020/9/17 15:26
 * Function：
 * Desc：
 */
public class RotateGestureDetector {
    public interface OnRotateGestureListener {
        boolean onRotate(float degrees, float focusX, float focusY);
    }

    private static final float RADIAN_TO_DEGREES = (float) (180.0 / Math.PI);
    private OnRotateGestureListener listener;
    private float prevX = 0.0f;
    private float prevY = 0.0f;
    private float prevTan;

    public RotateGestureDetector(OnRotateGestureListener listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (event.getPointerCount() == 2 && event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            float x = event.getX(1) - event.getX(0);
            float y = event.getY(1) - event.getY(0);
            float focusX = (event.getX(1) + event.getX(0)) * 0.5f;
            float focusY = (event.getY(1) + event.getY(0)) * 0.5f;
            float tan = (float) Math.atan2(y, x);

            if (prevX != 0.0f && prevY != 0.0f) {
                result = listener.onRotate((tan - prevTan) * RADIAN_TO_DEGREES, focusX, focusY);
            }

            prevX = x;
            prevY = y;
            prevTan = tan;
        } else {
            prevX = prevY = prevTan = 0.0f;
        }
        return result;
    }
}
