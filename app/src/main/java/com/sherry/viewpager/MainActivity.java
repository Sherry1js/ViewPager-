package com.sherry.viewpager;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;
import static com.sherry.viewpager.R.drawable.a;
import static com.sherry.viewpager.R.id.viewpager;

public class MainActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private TextView title;
    private LinearLayout ll_point;

    private ArrayList<ImageView> imageViews;
    private int prePosition = 0;

    //判断页面是否滑动
    private boolean isDragging = false;

    // 图片资源ID
    private final int[] imageIds = {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };

    // 图片标题集合
    private final String[] imageDescriptions = {
            "啦啦啦啦啦啦",
            "呀呀呀呀呀呀",
            "咔咔咔咔咔咔",
            "啊啊啊啊啊啊",
            "哇哇哇哇哇哇"
    };

    //自动切换页面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            int item = viewPager.getCurrentItem()+1;
            viewPager.setCurrentItem(item);

            //延迟发消息
            handler.sendEmptyMessageDelayed(0,3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(viewpager);
        title = (TextView) findViewById(R.id.title);
        ll_point = (LinearLayout) findViewById(R.id.ll_point);

        imageViews = new ArrayList<>();
        for(int i = 0; i < imageIds.length; i++) {

            //添加viewpager的图片页面
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            imageViews.add(imageView);

            //添加点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16,16);

            if(i == 0) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
                params.leftMargin = 16;
            }

            point.setLayoutParams(params);
            ll_point.addView(point);
        }

        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(new MyPageChange());

        int item = Integer.MAX_VALUE/2 - Integer.MAX_VALUE/2 % imageViews.size(); //保证是imageViews的整数倍
        //页面默认从中间开始创建
        viewPager.setCurrentItem(item);

        handler.sendEmptyMessageDelayed(0,3000);
    }

    class MyPageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

        }

        /**
         * 当前选中的页面
         * @param position
         */
        @Override
        public void onPageSelected(int position){
            int realPosition = position % imageViews.size();
            title.setText(imageDescriptions[realPosition]); //设置当前页面的标题
            ll_point.getChildAt(prePosition).setEnabled(false); //把上一页的点设为false
            ll_point.getChildAt(realPosition).setEnabled(true); //把当前页面的点设为true
            prePosition = realPosition;
        }

        /**
         当页面滚动状态变化的时候回调这个方法
         静止->滑动、滑动-->静止、静止-->拖拽
         */
        @Override
        public void onPageScrollStateChanged(int state){
            if(state == ViewPager.SCROLL_STATE_DRAGGING){
                isDragging = true;
                handler.removeCallbacksAndMessages(null);
            }else if(state == ViewPager.SCROLL_STATE_SETTLING){

            }else if(state == ViewPager.SCROLL_STATE_IDLE&&isDragging){
                isDragging = false;
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0,4000);
            }

        }
    }

    //设置viewpager的适配器
    class MyAdapter extends PagerAdapter {

        //返回页面总数
        @Override
        public int getCount(){
            return Integer.MAX_VALUE;
        }

        /**
         * 比较view和object是否同一个实例
         * @param view 页面
         * @param object 这个方法instantiateItem返回的结果
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object){
            return view == object;
        }

        /**
         * 相当于getView方法
         * @param container ViewPager自身
         * @param position 当前实例化页面的位置
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position){
            int realPosition = position % imageViews.size();
            ImageView imageView = imageViews.get(realPosition);
            container.addView(imageView);

            //触摸事件，当手指按下或离开时执行的操作
            imageView.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View view, MotionEvent event){
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN: //手指按下
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                        case MotionEvent.ACTION_UP: //手指离开
                            handler.sendEmptyMessageDelayed(0,3000);
                            break;
                    }
                    return false;
                }
            });

            imageView.setTag(position);
            //添加页面的点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag()%imageViews.size();
                    String text = imageDescriptions[position];
                    Toast.makeText(MainActivity.this, "text=="+text, Toast.LENGTH_SHORT).show();

                }
            });

            return imageView;
        }

        /**
         * 释放资源
         * @param container viewpager
         * @param position 要释放的位置
         * @param object 要释放的页面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((View) object);
        }
    }

}
