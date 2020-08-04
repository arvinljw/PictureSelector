package net.arvin.selector.uis.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;

/**
 * Created by arvinljw on 2020/7/23 18:24
 * Function：
 * Desc：
 */
public class FolderRecyclerView extends RecyclerView {

    public FolderRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FolderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FolderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWillNotDraw(false);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(true);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int dp12 = getResources().getDimensionPixelSize(R.dimen.dp_12);
                    outline.setRoundRect(0, 0, getWidth(), getHeight(), dp12);
                }
            });
        }
    }
}
