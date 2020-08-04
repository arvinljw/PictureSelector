package net.arvin.selector.uis.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by arvinljw on 2020/7/17 17:12
 * Function：
 * Desc：
 */
public class GridDivider extends RecyclerView.ItemDecoration {

    private int dividerHeight;
    private Paint dividerPaint;

    public GridDivider(int dividerHeight) {
        this.dividerHeight = dividerHeight;
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() == null) {
            return;
        }
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view);
        int spanCount = layoutManager.getSpanCount();
        int column = position % spanCount + 1;//第几列
        int totalCount = parent.getAdapter().getItemCount();

        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            outRect.top = 0;
            outRect.bottom = dividerHeight;
            outRect.left = (column - 1) * dividerHeight / spanCount; //左侧为(当前条目数-1)/总条目数*divider宽度
            outRect.right = (spanCount - column) * dividerHeight / spanCount;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
        } else {
            column = position / spanCount + 1;//第几列
            int totalColumn = totalCount / spanCount + (totalCount % spanCount == 0 ? 0 : 1);//总列数
            outRect.top = 0;
            outRect.bottom = dividerHeight;
            outRect.left = (column - 1) * dividerHeight / totalColumn; //左侧为(当前条目数-1)/总条目数*divider宽度
            outRect.right = (totalColumn - column) * dividerHeight / totalColumn;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            drawGridVertical(c, parent);
        } else {
            drawGridHorizontal(c, parent);
        }
    }

    private void drawGridVertical(Canvas c, RecyclerView parent) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        float left, top, right, bottom;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int spanCount = layoutManager.getSpanCount();
            int position = parent.getChildAdapterPosition(child);
            int column = position % spanCount + 1;//第几列

            //绘制下边
            left = child.getLeft() - layoutParams.leftMargin;
            right = child.getRight() + layoutParams.rightMargin + dividerHeight;
            top = child.getBottom() + layoutParams.bottomMargin;
            bottom = top + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);

            //绘制左边，第一列的都不绘制左边
            int dividerLeft = (column - 1) * dividerHeight / spanCount;
            right = child.getLeft() - layoutParams.leftMargin;
            left = right - dividerLeft;
            top = child.getTop() - layoutParams.topMargin;
            bottom = child.getBottom() + layoutParams.bottomMargin;
            c.drawRect(left, top, right, bottom, dividerPaint);

            //绘制右边，最后一列都不绘制右边
            left = child.getRight() + layoutParams.rightMargin;
            right = left + (spanCount - column) * dividerHeight / spanCount;
            top = child.getTop() - layoutParams.topMargin;
            bottom = child.getBottom() + layoutParams.bottomMargin;
            if (position == parent.getAdapter().getItemCount() - 1 && spanCount != column) {
                right = left + dividerHeight;
            }
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }

    private void drawGridHorizontal(Canvas c, RecyclerView parent) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        float left, top, right, bottom;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int spanCount = layoutManager.getSpanCount();
            int position = parent.getChildAdapterPosition(child);
            int column = position / spanCount + 1;//第几列
            int totalCount = parent.getAdapter().getItemCount();
            int totalColumn = totalCount / spanCount + (totalCount % spanCount == 0 ? 0 : 1);//总列数

            //绘制下边
            left = child.getLeft() - layoutParams.leftMargin;
            right = child.getRight() + layoutParams.rightMargin + dividerHeight;
            top = child.getBottom() + layoutParams.bottomMargin;
            bottom = top + dividerHeight;
            if (column != 1) {
                left = left - (column - 1) * dividerHeight / totalColumn;//避免view被重用是，左边被回收
            }
            c.drawRect(left, top, right, bottom, dividerPaint);

            //绘制左边，第一列不画
            right = child.getLeft() - layoutParams.leftMargin;
            left = right - (column - 1) * dividerHeight / totalColumn;
            top = child.getTop() - layoutParams.topMargin;
            bottom = child.getBottom() + layoutParams.bottomMargin;
            c.drawRect(left, top, right, bottom, dividerPaint);

            //绘制右边，最后一列不画
            left = child.getRight() + layoutParams.rightMargin;
            right = left + (totalColumn - column) * dividerHeight / totalColumn;
            top = child.getTop() - layoutParams.topMargin;
            bottom = child.getBottom() + layoutParams.bottomMargin;
            if (column == totalColumn - 1 && position + spanCount > totalCount - 1) {
                right = left + dividerHeight;
            }
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }
}
