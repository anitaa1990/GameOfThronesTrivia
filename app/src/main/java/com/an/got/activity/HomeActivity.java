package com.an.got.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.an.got.R;
import com.an.got.adapter.CardFragmentPagerAdapter;
import com.an.got.base.BaseActivity;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.RequestTask;
import com.an.got.views.ShadowTransformer;

public class HomeActivity extends BaseActivity {

    private View contentPanel;
    private ViewPager mViewPager;

    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        contentPanel = findViewById(R.id.contentPanel);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);

        /* Enable this in order to scale the selected fragment(make it bigger) */
        // mFragmentCardShadowTransformer.enableScaling(b);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setVisibility(View.GONE);

        showContent();
        getGames();
    }

    private void showContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mViewPager.getVisibility() != View.VISIBLE)
                    displayContent(null);
            }
        }, 12000);
    }

    public void displayContent(View view) {
        mViewPager.setVisibility(View.VISIBLE);
        contentPanel.setEnabled(false);
        AnimationUtils.getInstance().slideFromBottom(mViewPager);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    private void getGames() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RequestTask(HomeActivity.this).execute();
            }
        }).start();

    }
}