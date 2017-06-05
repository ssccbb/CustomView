package com.sung.customview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sung.customview.R;

/**
 * Created on 2016/6/6.
 */
public class ScaleView extends View {

    private static float DELTA_SCALE = 0.02f;
    private static float MIN_SCALE = 0.8f;
    private static float MAX_SCALE = 0.9f;

    private Matrix mMatrix = new Matrix();
    private float mCurScale = MAX_SCALE;
    private float mTargetScale = MIN_SCALE;
    private ZoomInRunable mZoomInRunable;
    private ZoomOutRunable mZoomOutRunable;
    private Bitmap mBgBimap;
    private Paint mBitmapPaint;

    public ScaleView(Context context) {
        super(context);
        init(context, null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        int resId = typedArray.getResourceId(R.styleable.ScaleView_scaleview_bg, R.mipmap.ic_launcher);
        typedArray.recycle();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setAlpha(255);

        mBgBimap = BitmapFactory.decodeResource(getResources(), resId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.DST);
        if (mBgBimap != null) {
            float scale = mCurScale * getWidth() / mBgBimap.getWidth();
            mMatrix.reset();
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate((getWidth() - mBgBimap.getWidth() * scale) / 2,
                    (getHeight() - mBgBimap.getHeight() * scale) / 2);
            canvas.drawBitmap(mBgBimap, mMatrix, mBitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (mZoomInRunable != null) {
                    this.removeCallbacks(mZoomInRunable);
                    mZoomInRunable = null;
                }
                if (mZoomOutRunable == null) {
                    mTargetScale = MIN_SCALE;
                    mZoomOutRunable = new ZoomOutRunable();
                    this.post(mZoomOutRunable);
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (mZoomOutRunable != null) {
                    this.removeCallbacks(mZoomOutRunable);
                    mZoomOutRunable = null;
                }
                if (mZoomInRunable == null) {
                    mTargetScale = MAX_SCALE;
                    mZoomInRunable = new ZoomInRunable();
                    this.post(mZoomInRunable);

                    performClick();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    //缩小
    private class ZoomOutRunable implements Runnable{

        @Override
        public void run() {
            if (mCurScale != mTargetScale) {
                mCurScale = mCurScale - DELTA_SCALE;
                if (mCurScale < mTargetScale) {
                    mCurScale = mTargetScale;
                }
                invalidate();
                ScaleView.this.post(this);
            } else {
                if (mZoomOutRunable != null) {
                    ScaleView.this.removeCallbacks(mZoomOutRunable);
                    mZoomOutRunable = null;
                }
            }
        }
    }

    //放大
    private class ZoomInRunable implements Runnable{

        @Override
        public void run() {
            if (mCurScale < mTargetScale) {
                mCurScale = mCurScale + DELTA_SCALE;
                if (mCurScale > mTargetScale) {
                    mCurScale = mTargetScale;
                }
                invalidate();
                ScaleView.this.post(this);
            }else {
                if (mZoomInRunable != null) {
                    ScaleView.this.removeCallbacks(mZoomInRunable);
                    mZoomInRunable = null;
                }
            }
        }
    }

}
