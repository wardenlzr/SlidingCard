package com.bg.slidingcard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bg.slidingcard.aop_test.TestAnnotation;
import com.bg.slidingcard.databinding.ActivityMainBinding;
import com.bg.slidingcard.weight.RecyclePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding dataBinding;
    private Context mContext;
    private RecyclePagerAdapter sortPagerAdapter;
    private List list = new ArrayList();
    private int indicatorWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_main, null);
        dataBinding = DataBindingUtil.bind(view);
        setContentView(view);
        mContext = this;
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        ViewPager vpCircle = dataBinding.vp;
        vpCircle.post(() -> indicatorWidth = dataBinding.llIndicator.getWidth() / (list.size() + 1));
        //处理viewpager显示的上一个或者下一个页面一部分无法滑动
        dataBinding.clVpager.setOnTouchListener((v, event) -> dataBinding.vp.dispatchTouchEvent(event));

        vpCircle.setAdapter(sortPagerAdapter = new RecyclePagerAdapter<RecyclePagerAdapter.PagerViewHolder>() {
            @Override
            public int getItemCount() {
                return list.size();
            }

            @Override
            public PagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new PagerViewHolder(View.inflate(mContext, R.layout.fragment_circle_sort, null));
            }

            @Override
            public void onBindViewHolder(PagerViewHolder holder, int position) {
                holder.setText(R.id.tv_sort_name, list.get(position) + "生活");
            }
        });
        vpCircle.addOnPageChangeListener(new CirclePageChangeListener());
        vpCircle.setOffscreenPageLimit(3);//确保不是首位时能看到左右的卡片
    }

    @TestAnnotation
    public void test(View view) {
        System.out.println("Hello, I am test");
    }

    private class CirclePageChangeListener implements ViewPager.OnPageChangeListener {
        private int lastValue = -1;
        private boolean isLeft = true;
        private int startLocation = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset != 0) {
                if (lastValue >= positionOffsetPixels) {
                    //右滑
                    isLeft = false;
                } else if (lastValue < positionOffsetPixels) {
                    //左滑
                    isLeft = true;
                }
            }
            lastValue = positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            try {
                //放大动画
                RecyclePagerAdapter.PagerViewHolder holdercCurrent = sortPagerAdapter.getItemView(position);
                View viewCurrent = holdercCurrent.itemView;
                View riv_bgCurrent = viewCurrent.findViewById(R.id.riv_bg);
                riv_bgCurrent.setAlpha(1f);
                ObjectAnimator
                        .ofFloat(viewCurrent, "scaleX", 0.91f, 1f)
                        .setDuration(400)
                        .start();
                ObjectAnimator
                        .ofFloat(viewCurrent, "scaleY", 0.91f, 1f)
                        .setDuration(400)
                        .start();
                //需要缩小的position
                List<Integer> needNarrowPos = new ArrayList<>();
                if (position == 0) {
                    needNarrowPos.add(position + 1);
                } else if (position == list.size() - 1) {
                    needNarrowPos.add(position - 1);
                } else {
                    needNarrowPos.add(position - 1);
                    needNarrowPos.add(position + 1);
                }
                for (Integer needNarrowPo : needNarrowPos) {
                    //缩小动画
                    RecyclePagerAdapter.PagerViewHolder holder = sortPagerAdapter.getItemView(needNarrowPo);
                    View view = holder.itemView;
                    View riv_bg = view.findViewById(R.id.riv_bg);
                    riv_bg.setAlpha(0.5f);
                    ObjectAnimator
                            .ofFloat(view, "scaleX", 1f, 0.91f)
                            .setDuration(400)
                            .start();
                    ObjectAnimator
                            .ofFloat(view, "scaleY", 1f, 0.91f)
                            .setDuration(400)
                            .start();
                }

                if (isLeft) {//左滑 向右平移 ，反之...
                    if (position == 0) {
                        return;
                    }
                    ObjectAnimator.
                            ofFloat(dataBinding.vIndicator, "TranslationX", startLocation, startLocation += indicatorWidth)
                            .setDuration(400)
                            .start();
                } else {
                    ObjectAnimator.
                            ofFloat(dataBinding.vIndicator, "TranslationX", startLocation, startLocation -= indicatorWidth)
                            .setDuration(400)
                            .start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
