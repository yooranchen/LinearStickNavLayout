package org.yooranchen.linearsticknavlayout.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import org.yooranchen.linearsticknavlayout.R;
import org.yooranchen.linearsticknavlayout.exception.UnExpectIdException;

/**
 * @author yooranchen
 */
public class LinearStickNavLayout extends LinearLayout {

    // 布局内部的3个元素
    private View mTop;
    private View mNav;
    private ViewPager mViewPager;

    // 头部高度
    private int mTopViewHeight;

    // 片段内部的ListView或者ScrollView
    private ViewGroup mInnerScrollView;

    private OverScroller mScroller;
    // 检测滑动速度
    private VelocityTracker mVelocityTracker;
    // 最小滑动距离
    private int mTouchSlop;
    // 最大和最小的滑动速率
    private int mMaximumVelocity, mMinimumVelocity;

    // 记录最后一次点击的Y轴
    private float mLastY;
    // 标记是否在拖拽
    private boolean mDragging;
    // 头部是否隐藏
    private boolean isTopHidden = false;

    public LinearStickNavLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearStickNavLayout(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    public LinearStickNavLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 3) {
            mTop = getChildAt(0);
            mNav = getChildAt(1);
            mViewPager = (ViewPager) getChildAt(2);
        } else {
            // 如果布局内的元素没按照要求，则抛出异常
            throw new IllegalStateException(
                    "StickNavLayout must contain three child,the third child must be ViewPager");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                // 获取当前的listView或者ScrollView
                getCurrentScrollView();
                // ListView,ScrollView的事件分发
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                    // 片段内的滑动控件为ScrollView
                    if (mInnerScrollView instanceof ScrollView) {
                        if (!isTopHidden
                                || (mInnerScrollView.getScrollY() == 0
                                && isTopHidden && dy > 0)) {
                            return true;
                        }
                    } else if (mInnerScrollView instanceof ListView) {
                        // 片段内的滑动控件为ListView
                        ListView lv = (ListView) mInnerScrollView;
                        // 获取第一个可见元素
                        View c = lv.getChildAt(lv.getFirstVisiblePosition());
                        if (!isTopHidden
                                || (c != null && c.getTop() == 0 && isTopHidden && dy > 0)) {
                            // 拦截listView事件
                            return true;
                        }
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 获取当前VirwPager子Item内部的滑动控件
     */
    private void getCurrentScrollView() throws UnExpectIdException {
        if (mViewPager == null)
            return;
        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter a = mViewPager.getAdapter();
        if (a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
            Fragment item = fadapter.getItem(currentItem);
            mInnerScrollView = (ViewGroup) (item.getView()
                    .findViewById(R.id.innerscrollview));
            if (mInnerScrollView == null) {
                //内部
                throw new UnExpectIdException("the ListView/ScrollView id must bu called innerscrollview");
            }
        } else if (a instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
            Fragment item = fsAdapter.getItem(currentItem);
            mInnerScrollView = (ViewGroup) (item.getView()
                    .findViewById(R.id.innerscrollview));
            if (mInnerScrollView == null) {
                throw new UnExpectIdException("the ListView/ScrollView  id must bu called innerscrollview");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                //开始拖拽
                if (mDragging) {
                    //滚动
                    scrollBy(0, (int) -dy);
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                // 大于最小拖拽速率时，fling
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                mVelocityTracker.clear();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 推拽，根据力度，滚动一定距离
     */
    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        // 限制成y 最小为top的高度
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != mTopViewHeight) {
            mOnScrollChangeListener.onScrollChange(y, (float) y
                    / mTopViewHeight);
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

        isTopHidden = getScrollY() == mTopViewHeight;
    }

    /**
     * OverScroll完成后执行
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 头部滚动y指和比例
     */
    public interface OnScrollChangeListener {
        /**
         * @param y      隐藏部分
         * @param offset 隐藏部分与整个头部高度的比例
         */
        void onScrollChange(int y, float offset);
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(
            OnScrollChangeListener scrollChangeListener) {
        mOnScrollChangeListener = scrollChangeListener;
    }

}
