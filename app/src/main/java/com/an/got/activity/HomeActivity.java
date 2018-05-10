package com.an.got.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.an.got.R;
import com.an.got.adapter.HomeFragmentPagerAdapter;
import com.an.got.base.BaseActivity;
import com.an.got.databinding.HomeActivityBinding;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.RequestTask;
import com.an.got.utils.Utils;
import com.an.got.views.ShadowTransformer;

public class HomeActivity extends BaseActivity {

    private HomeActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        HomeFragmentPagerAdapter mFragmentCardAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), Utils.dpToPixels(2, this));
        binding.viewPager.setAdapter(mFragmentCardAdapter);
        binding.viewPager.setPageTransformer(false, new ShadowTransformer(binding.viewPager, mFragmentCardAdapter));

        /* Enable this in order to scale the selected fragment(make it bigger) */
        // mFragmentCardShadowTransformer.enableScaling(b);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setVisibility(View.GONE);

        showContent();
        getGames();
    }


    /*
     * Show the list of game options after 12000 seconds
     * */
    private void showContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(binding.viewPager.getVisibility() != View.VISIBLE)
                    displayContent(null);
            }
        }, getResources().getInteger(R.integer.default_timer_for_displaying_games));
    }


    /*
     * Get list of questions from the backend & store locally.
     * This is so that we get updated version of the questions without asking the users to update the app
     * */
    private void getGames() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RequestTask(HomeActivity.this).execute();
            }
        }).start();

    }


    /*
     * OnClick event handler for CoordinatorLayout
     * i.e. binding.contentPanel.
     * We are delaying displaying the contents for 12000 seconds.
     * But if the user clicks on the user, they can see the contents
     * */
    public void displayContent(View view) {
        binding.viewPager.setVisibility(View.VISIBLE);
        binding.contentPanel.setEnabled(false);
        AnimationUtils.getInstance().slideFromBottom(binding.viewPager);
    }
}