package com.dnake.security.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dnake.security.BaseLabel;
import com.dnake.security.R;
import com.dnake.security.fragment.ZoneFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

public class ZoneActivity extends BaseLabel {
    private ImageView btnBack;
    private ViewPager vpMain;
    private MagicIndicator dotsIndicator;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideBottomUIMenu();
            }
        });
        setContentView(R.layout.activity_zone);
        initView();
    }

    private void initView() {
        layoutMain = (FrameLayout) findViewById(R.id.layout_main);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        vpMain = (ViewPager) findViewById(R.id.vp_main);
        dotsIndicator = (MagicIndicator) findViewById(R.id.dots_indicator);

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(pagerAdapter);
        CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(2);
        circleNavigator.setRadius(5);
        circleNavigator.setCircleSpacing(20);
        circleNavigator.setCircleColor(Color.WHITE);
        circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                vpMain.setCurrentItem(index);
            }
        });
        dotsIndicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(dotsIndicator, vpMain);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ZoneFragment.newInstance(4, 0);
            } else {
                return ZoneFragment.newInstance(4, 4);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}