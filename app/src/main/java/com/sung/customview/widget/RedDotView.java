package com.sung.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
/**
 * 小红点
 */

public class RedDotView extends View {

    private Paint mPaint;
    private Context mContext;
    private float mRadius;

    public RedDotView(Context context) {
        super(context);
        init(context);
    }

    public RedDotView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mRadius = (4.0F * context.getResources().getDisplayMetrics().density);
    }

    public void remove(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.removeView(this);
        }
    }

    public void add(ViewGroup viewGroup, ViewGroup.LayoutParams params) {
        viewGroup.addView(this, params);
    }

    public float getRadius() {
        return mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        canvas.drawCircle(center, center, mRadius, mPaint);
        super.onDraw(canvas);
    }
}

