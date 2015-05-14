package org.yooranchen.linearsticknavlayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import org.yooranchen.linearsticknavlayout.ui.fragment.ListViewFragment;
import org.yooranchen.linearsticknavlayout.ui.fragment.ScrollViewFragment;
import org.yooranchen.linearsticknavlayout.view.LinearStickNavLayout;

/**
 * yooranchen
 */
public class MainActivity extends ActionBarActivity implements
        LinearStickNavLayout.OnScrollChangeListener {

    //片段列表
    private Fragment[] fragments = new Fragment[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearStickNavLayout layout = (LinearStickNavLayout) findViewById(R.id.stickNavLayout);
        //设置滚动监听
        layout.setOnScrollChangeListener(new LinearStickNavLayout.OnScrollChangeListener() {
            /**
             * @param y      隐藏部分
             * @param offset 隐藏部分与整个头部高度的比例
             */
            @Override
            public void onScrollChange(int y, float offset) {

            }
        });
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyFragmentAdapter adapter = new MyFragmentAdapter(
                getSupportFragmentManager());
        pager.setAdapter(adapter);
        // 必须设置当前位置为0
        pager.setCurrentItem(0);
        fragments[0] = new ListViewFragment();
        fragments[1] = new ScrollViewFragment();
        fragments[2] = new ListViewFragment();
    }

    class MyFragmentAdapter extends FragmentPagerAdapter {
        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 此处不能return new Fragment();否则，布局中无法通过getItem()获取已加在的片段
         *
         * @param arg0
         * @return
         */
        @Override
        public Fragment getItem(int arg0) {
            return fragments[arg0];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    /**
     * 布局滚动监听
     *
     * @param y      隐藏部分
     * @param offset 隐藏部分与整个头部高度的比例
     */
    @Override
    public void onScrollChange(int y, float offset) {
        Log.e("onScrollChange", "y>>>" + y + ">>> offset>>>" + offset);
    }
}
