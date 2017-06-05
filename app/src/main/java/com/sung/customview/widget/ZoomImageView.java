package com.sung.customview.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

/**
 * Created by sung on 2017/4/11.
 */

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private boolean mOnce = false;
    //初始化缩放值
    private float mInitScale;
    //双击缩放值
    private float mMidScale;
    //最大缩放值
    private float mMaxScale;

    private Matrix mScaleMatrix;
    //捕获用户多点触控
    private ScaleGestureDetector mScaleGestureDetector;
    //自由移动
    //记录上次多点触控的数量
    private int mLastPointCount;
    private float mLastX;
    private float mLastY;
    private float mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight = false;
    private boolean isCheckTopAndTop = false;

    //双击放大与缩小
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale) {
                    return true;
                }
                float x = e.getX();
                float y = e.getY();
                if (getScale() < mMidScale) {
//                    mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                    isAutoScale = true;
                } else {
//                    mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                    isAutoScale = true;
                }
                return true;
            }
        });
    }

    private class AutoScaleRunnable implements Runnable {

        //缩放的目标值
        private float mTarget;
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tempScale;

        public AutoScaleRunnable(float mTarget, float x, float y) {
            this.mTarget = mTarget;
            this.x = x;
            this.y = y;

            if (getScale() < mTarget) {
                tempScale = BIGGER;
            } else {
                tempScale = SMALL;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            if ((tempScale > 1.0f && currentScale < mTarget) || (tempScale < 1.0f && currentScale > mTarget)) {
                postDelayed(this, 16);
            } else {
                float scale = mTarget / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        //获取ImageView加载完成的图片
        if (!mOnce) {
            //获取控件宽和高
            int width = getWidth();
            int height = getHeight();
            //得到图片和图片的宽高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            //缩放比例计算
            float scale = 1.0f;

            if (intrinsicWidth > width && intrinsicHeight < height) {
                scale = width * 1.0f / intrinsicWidth;
            }

            if (intrinsicHeight > height && intrinsicWidth < width) {
                scale = height * 1.0f / intrinsicHeight;
            }

            if (intrinsicHeight > height && intrinsicWidth > width) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight);
            }

            if (intrinsicHeight < height && intrinsicWidth < width) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight);
            }

            mInitScale = scale;
            mMaxScale = 4 * mInitScale;
            mMidScale = 4 * mInitScale;

            //将图片移动至屏幕中心
            int dx = getWidth() / 2 - intrinsicWidth / 2;
            int dy = getHeight() / 2 - intrinsicHeight / 2;

            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;
        }
    }

    //获取当前图片的缩放值
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    //缩放区间initScale maxScale
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //缩放值
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();
        if (getDrawable() == null) {
            return true;
        }

        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return false;
    }

    //获取图片放大缩小以后的宽和高
    private RectF getMatrixRectF() {
        Matrix matrix = this.mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }

        return rectF;
    }

    /**
     * 在缩放时进行边界控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        //如果控件的宽度和高度小于控件的宽和高，让其居中
        if (rectF.width() < width) {
            deltaX = width / 2 - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2 - rectF.bottom + rectF.height() / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0;
        float y = 0;
        //拿到多点触控的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (pointerCount != mLastPointCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointCount = pointerCount;
        RectF matrixRectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (matrixRectF.width() > getWidth() + 0.01 || matrixRectF.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (matrixRectF.width() > getWidth() + 0.01 || matrixRectF.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (isMoveAction(dx, dy)) {
                    isCanDrag = true;
                }

                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndTop = true;
                        //如果控件小于控件宽度，则不允许使用横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }

                        //如果控件小于纵向高度，则不允许使用
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndTop = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    /**
     * 当移动时，进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndTop) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndTop) {
            deltaY = height - rectF.bottom;
        }

        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
