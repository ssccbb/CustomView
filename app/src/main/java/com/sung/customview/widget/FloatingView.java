package com.sung.customview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sung.customview.R;

/**
 * Created by sung on 2016/9/12
 * 悬浮球，点击弹出菜单
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatingView extends BaseFloatingView {

    private PopupWindow mPopupWindow;
    private long mTapOutsideTime;
    private boolean isShowing = false;
    private final View mIdleVew;
    private final ImageView mIV;

    /**
     * 悬浮球
     *
     * @param context   建议使用application context避免activity泄漏
     * @param viewResId Resid
     */
    public FloatingView(Context context, int viewResId) {
        super(context);
        View.inflate(context, viewResId, this);

        mIdleVew = findViewById(R.id.view_idle);
        mIV = (ImageView) findViewById(R.id.iv_main_icon);
    }

    /**
     * 设置弹出菜单
     *
     * @param id resource id，根据resource id inflate 菜单
     */
    public void setPopupWindow(int id) {
        //this.mPopupWindow = popupWindow;
        mPopupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(getContext()).inflate(id, null);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopupWindow.dismiss();
                    if (mIdleVew != null) {
                        mIdleVew.setVisibility(GONE);
                    }
                    mTapOutsideTime = System.currentTimeMillis();
                    //isPopupWinowShow = false;
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @return popupWindow顶层view
     */
    public View getPopupView() {
        return mPopupWindow.getContentView();
    }

    /**
     * 注册点击回调
     *
     * @param listener onclickListener
     */
    public void setOnPopupItemClickListener(OnClickListener listener) {
        if (mPopupWindow == null)
            return;

        ViewGroup layout = (ViewGroup) mPopupWindow.getContentView();

        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setOnClickListener(listener);
        }
    }

    /**
     * 显示悬浮球
     */
    public void show() {
        if (!isShowing) super.showView(this);
        isShowing = true;
    }

    /**
     * 关闭悬浮球
     */
    public void dismiss() {

        if (isShowing)
            super.hideView();
        isShowing = false;

        //清空listener操作
        ViewGroup layout = (ViewGroup) mPopupWindow.getContentView();

        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setOnClickListener(null);
        }
    }

    /**
     * 单击显示popupWindow
     *
     * @param e motionEvent
     * @return false 本身不对点击事件进行消费
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (null != mPopupWindow)
            mPopupWindow.dismiss();
        //避免单击悬浮球不断出现popupwindows的糟糕体验
        if (!(System.currentTimeMillis() - mTapOutsideTime < 80)) {
            if (mIdleVew != null) {
                mIdleVew.setVisibility(VISIBLE);
            }
            mPopupWindow.showAtLocation(this, Gravity.NO_GRAVITY, mIV.getMeasuredWidth(), mIV.getMeasuredHeight()/8);
        }
        return false;
    }


}
