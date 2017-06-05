package com.sung.customview.widget.slidesliprecycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by sung on 16/6/12.
 * 带侧滑的RecyclerView
 */
public class SlideSlipRecyclerView extends RecyclerView implements Pullable{

    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private int maxLength;
    private int mStartX = 0;
    private LinearLayout itemLayout;
    private int pos;
    private Rect mTouchFrame;
    private int xDown, xMove, yDown, yMove, mTouchSlop;
    private Scroller mScroller;
    private boolean isFirst = true;
    private addOnItemClickListener addOnItemClickListener;

    public SlideSlipRecyclerView(Context context) {
        this(context, null);
    }

    public SlideSlipRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSlipRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //滑动到最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //滑动的最大距离
        maxLength = ((int) (110 * context.getResources().getDisplayMetrics().density + 0.5f));
        //初始化Scroller
        mScroller = new Scroller(context, new LinearInterpolator(context, null));
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout != null) {
            if (layout instanceof LinearLayoutManager) {
                linearLayoutManager = (LinearLayoutManager) layout;
            }
            if (layout instanceof StaggeredGridLayoutManager) {
                staggeredGridLayoutManager = (StaggeredGridLayoutManager) layout;
            }
        }
        super.setLayoutManager(layout);
    }


    private int dipToPx(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                xDown = x;
                yDown = y;
                //通过点击的坐标计算当前的position
                int mFirstPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                Rect frame = mTouchFrame;
                if (frame == null) {
                    mTouchFrame = new Rect();
                    frame = mTouchFrame;
                }
                int count = getChildCount();
                for (int i = count - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == View.VISIBLE) {
                        child.getHitRect(frame);
                        if (frame.contains(x, y)) {
                            pos = mFirstPosition + i;
                        }
                    }
                }
                //通过position得到item的viewHolder
                View view = getChildAt(pos - mFirstPosition);
                SlideSlipViewHolder viewHolder = (SlideSlipViewHolder) getChildViewHolder(view);
                itemLayout = viewHolder.layout;
                Log.e("ACTION_DOWN", "onTouchEvent: mFirstPosition - "+mFirstPosition+"| pos - "+pos);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;

                if (Math.abs(dy) < mTouchSlop * 2 && Math.abs(dx) > mTouchSlop) {
                    int scrollX = itemLayout.getScrollX();
                    int newScrollX = mStartX - x;
                    if (newScrollX < 0 && scrollX <= 0) {
                        newScrollX = 0;
                    } else if (newScrollX > 0 && scrollX >= maxLength) {
                        newScrollX = 0;
                    }
                    if (scrollX > maxLength / 2) {
//                        imageView.setVisibility(VISIBLE);
                        if (isFirst) {
//                            ObjectAnimator animatorX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.2f, 1f);
//                            ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.2f, 1f);
//                            AnimatorSet animSet = new AnimatorSet();
//                            animSet.play(animatorX).with(animatorY);
//                            animSet.setDuration(800);
//                            animSet.start();
                            isFirst = false;
                        }
                    } else {
//                        imageView.setVisibility(GONE);
                    }
                    itemLayout.scrollBy(newScrollX, 0);

                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                int scrollX = itemLayout.getScrollX();
                if (scrollX > (maxLength - 10) ) {
//                    ((SlideSlipRecyclerAdapter) getAdapter()).removeRecycle(pos);
                    mScroller.startScroll(scrollX, 0, (333 - scrollX), 0);//滑动到固定位置
                } else {
                    mScroller.startScroll(scrollX, 0, -scrollX, 0);
                }
                invalidate();
                isFirst = true;

                //单击判断
                if (x - xDown >= 5 || x - xDown <= -5)
                    break;
                if (y - yDown >= 5 || y - yDown <= -5)
                    break;

                try {
//                    SlideSlipRecyclerAdapter adapter = (SlideSlipRecyclerAdapter) getAdapter();
//                    ResSongInfo bean = adapter.getItem(pos);
//                    if (addOnItemClickListener != null && bean != null) {
//                        addOnItemClickListener.onRootItemClick(bean);
//                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    Log.e("onTouchEvent", "ArrayIndexOutOfBoundsException: "+e.toString());
                }

            }
            break;
        }
        mStartX = x;
        return super.onTouchEvent(event);
    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            itemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean isGetTop() {
        if (linearLayoutManager != null) {
            int count = linearLayoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else if (linearLayoutManager.findFirstVisibleItemPosition() == 0 && getChildAt(0).getTop() >= 0) {
                return true;
            }

        } else if (staggeredGridLayoutManager != null) {
            int count = staggeredGridLayoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else {
                int[] firstVisiblePosition = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                if (firstVisiblePosition[0] < staggeredGridLayoutManager.getChildCount() &&
                        getChildAt(firstVisiblePosition[0]).getTop() >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isGetBottom() {

        if (linearLayoutManager != null) {
            int count = linearLayoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else if (linearLayoutManager.findLastVisibleItemPosition() == count - 1
                    && getChildAt(
                    linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition())
                    .getBottom() <= getMeasuredHeight()) {
                return true;
            }

        } else if (staggeredGridLayoutManager != null
                && staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
            //仅限于每个item高度都固定的
            int count = staggeredGridLayoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else {
                int[] lastVisiblePosition = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                int[] firstVisiblePosition = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);

                boolean isLastItemShow = false;
                for (int i = 0; i < staggeredGridLayoutManager.getSpanCount(); i++) {
                    if (lastVisiblePosition[i] == staggeredGridLayoutManager.getItemCount() - 1) {
                        isLastItemShow = true;
                    }
                }
                if (isLastItemShow) {
                    for (int i = 0; i < staggeredGridLayoutManager.getSpanCount(); i++) {
                        if (lastVisiblePosition[i] >= firstVisiblePosition[0] &&
                                lastVisiblePosition[i] - firstVisiblePosition[0] < staggeredGridLayoutManager.getChildCount() &&
                                getChildAt(lastVisiblePosition[i] - firstVisiblePosition[0]).getBottom()
                                        <= getMeasuredHeight()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public interface addOnItemClickListener{
//        void onRootItemClick(ResSongInfo resSongInfo);
    }

    public void addOnItemClickListener(addOnItemClickListener listener){
        addOnItemClickListener = listener;
    }
}
