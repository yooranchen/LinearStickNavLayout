#	LinearStickNavLayout
##	实现Tab悬停
***
###	致谢
本控件修改自 `张鸿洋` 发布的[StickNavLayout](http://blog.csdn.net/lmj623565791/article/details/43649913),在此对张大神表示感谢
***
##	1.概述
外层自定义控件继承自LinearLayout，纵向布局，控件中涵盖三个控件
* 	Header
*	Tab
*	ViewPager

###	下面来看效果图：

![效果图](http://yooranchen.qiniudn.com/sticknavlayout.gif)
##	2.使用方式
1.	自定义id资源文件
````
	<resources>
 	   <item name="innerscrollview" type="id"/>
	</resources>
````
2.	主页面布局文件
````
<org.yooranchen.linearsticknavlayout.view.LinearStickNavLayout
        android:id="@+id/stickNavLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        >

        <TextView
            android:layout_width="match_parent"
            android:layout_height="300dp"
            android:gravity="center"
            android:text="@string/head_string"
            />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="#c0c0c0"
            >

           ...

        </LinearLayout>
		
        <android.support.v4.view.ViewPager
            android:id="@+id/pager"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />
    </org.yooranchen.linearsticknavlayout.view.LinearStickNavLayout>
````
最外层为自定义LinearStickNavLayout,内部包含控件为顶部内容,Tab,ViewPager,按照效果图去写就Ok了,此处ID不做限制,在LinearStickNavLayout中通过获取子控件来计算.	
3.	MainActivity
````
	//1.片段数组
    private Fragment[] fragments = new Fragment[3];

	//2.设置滚动监听
	LinearStickNavLayout layout = (LinearStickNavLayout) findViewById(R.id.stickNavLayout);
    layout.setOnScrollChangeListener(//设置滚动监听
        layout.setOnScrollChangeListener(new LinearStickNavLayout.OnScrollChangeListener() {
            /**
             * @param y      隐藏部分
             * @param offset 隐藏部分与整个头部高度的比例
             */
            @Override
            public void onScrollChange(int y, float offset) {
				//TODO,此处做逻辑操作
            }
      });
	//3.ViewPager适配器可使用FragmentPagerAdapter ,FragmentPagerStateAdapter 
	class MyFragmentAdapter extends FragmentPagerAdapter {
        ...
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
	...
    }
````

需要注意的就是适配器中的getItem 不能 return `new Fragment()`,其他的没有需要注意的
4.	 片段中的布局文件
````
	 <ListView
        android:id="@+id/innerscrollview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="none"
        >
    </ListView>
	
	//使用ScrollView
	<ScrollView
        android:id="@+id/innerscrollview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="none"
        >
		...
    
	</ScrollView>
````

布局文件中ListView或ScrollView的`ID` 必须使用`innerscrollview`,否则将会抛出异常

*	TODO 此处ScrollView必须为根元素,不知道为什么,欢迎大神讲解
***

##	欧了,以上就是使用介绍,如果有bug欢迎提出 <yooranchen@gmail.com>