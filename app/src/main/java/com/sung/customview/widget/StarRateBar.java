package com.sung.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sung.customview.R;

/**
 * Created by sung on 2017/4/25.
 * 星星评分条
 */

public class StarRateBar extends View {

    private static final String INSTANCE = "instance";
    private static final String TOTAL_STAR = "total_star";
    private static final String FULL_STAR = "full_star";
    private Bitmap mEmptyStar;
    private Bitmap mFullStar;
    private int mViewWidth;
    private int mStarSize;
    private int mTotalCount = 5;
    private int mFullCount = 3;
    private int mEmptyCount = 2;
    private boolean isTouchAble = true;
    private int mViewHeight;
    private float mEachStar;

    public StarRateBar(Context context) {
        this(context, null);
    }

    public StarRateBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarRateBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.StarRateBar);
        mTotalCount = typedArray.getInteger(R.styleable.StarRateBar_totalStar, 5);
        mFullCount = typedArray.getInteger(R.styleable.StarRateBar_starFull, 1);
        isTouchAble = typedArray.getBoolean(R.styleable.StarRateBar_touchAble, true);
        typedArray.recycle();
        mEmptyStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_empty);
        mFullStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_full);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewWidth = w;
        mViewHeight = h;

        mEachStar = (mViewWidth) / ((float) mTotalCount);

        mStarSize = (int) (Math.min(mEachStar * 0.618f, h));

        mFullStar = Bitmap.createScaledBitmap(mFullStar, mStarSize, mStarSize, false);
        mEmptyStar = Bitmap.createScaledBitmap(mEmptyStar, mStarSize, mStarSize, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawStars(canvas);
    }

    private void drawStars(Canvas canvas) {
        for (int i = 0; i < mTotalCount; i++) {
            if (i < mFullCount) {
                canvas.drawBitmap(mFullStar, mEachStar * i + (mEachStar - mStarSize) / 2, (mViewHeight - mStarSize) / 2, null);
            } else {
                canvas.drawBitmap(mEmptyStar, mEachStar * i + (mEachStar - mStarSize) / 2, (mViewHeight - mStarSize) / 2, null);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putInt(TOTAL_STAR, mTotalCount);
        bundle.putInt(FULL_STAR, mFullCount);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mFullCount = bundle.getInt(FULL_STAR);
            mTotalCount = bundle.getInt(TOTAL_STAR);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchAble) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                mFullCount = (int) Math.ceil(((float) x) / mEachStar);
                invalidate();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setFull(int full) {
        mFullCount = full;
        invalidate();
    }

    public int getFull() {
        return mFullCount;
    }

    public void setTouchAble(boolean able) {
        isTouchAble = able;
    }
}
