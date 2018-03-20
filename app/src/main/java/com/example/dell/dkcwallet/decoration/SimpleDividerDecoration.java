package com.example.dell.dkcwallet.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 *
 * @author yiyang
 */
public class SimpleDividerDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;
    private Paint dividerPaint;

    public SimpleDividerDecoration(Context context, int padding) {
        dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#cccccc"));
        dividerHeight = dp2px(context, 1);
        this.padding = padding;
    }

    public SimpleDividerDecoration(Context context){
        this(context, 0);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        parent.getLayoutManager();
        outRect.bottom = dividerHeight;
    }

    private int padding;
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            dividerPaint.setColor(Color.parseColor("#ffffff"));
            c.drawRect(left, top, right, bottom, dividerPaint);
            dividerPaint.setColor(Color.parseColor("#cccccc"));
            c.drawRect(left+padding, top, right-padding, bottom, dividerPaint);
        }
    }
    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
