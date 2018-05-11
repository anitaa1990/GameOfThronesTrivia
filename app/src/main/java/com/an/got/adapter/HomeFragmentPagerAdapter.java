package com.an.got.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.an.got.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<HomeFragment> mFragments;
    private float mBaseElevation;

    public HomeFragmentPagerAdapter(FragmentManager fm, float baseElevation) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;

        for (int i = 0; i < 4; i++) {
            addCardFragment(HomeFragment.newInstance(i));
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (HomeFragment) fragment);
        return fragment;
    }

    public void addCardFragment(HomeFragment fragment) {
        mFragments.add(fragment);
    }

}
