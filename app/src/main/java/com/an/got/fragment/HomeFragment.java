package com.an.got.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.activity.GameFourActivity;
import com.an.got.activity.GameOneActivity;
import com.an.got.activity.GameThreeActivity;
import com.an.got.activity.GameTwoActivity;
import com.an.got.adapter.CardAdapter;
import com.an.got.base.BaseFragment;
import com.an.got.databinding.HomeFragmentBinding;

import java.util.Arrays;
import java.util.List;


public class HomeFragment extends BaseFragment implements View.OnClickListener, GOTConstants {

    private int itemPosition;
    private HomeFragmentBinding homeFragmentBinding;

    public static HomeFragment newInstance(int position) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemPosition = getArguments().getInt("pos");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = homeFragmentBinding.getRoot();
        view.setOnClickListener(this);

        homeFragmentBinding.cardView.setMaxCardElevation(homeFragmentBinding.cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.titles));
        List<String> descriptions = Arrays.asList(getResources().getStringArray(R.array.descriptions));

        homeFragmentBinding.title.setText(titles.get(itemPosition));
        homeFragmentBinding.description.setText(descriptions.get(itemPosition));


        return view;
    }



    public CardView getCardView() {
        return homeFragmentBinding.cardView;
    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (itemPosition) {
            case 0:
                intent.setClass(mActivity, GameOneActivity.class).putExtra("pos", RAW_ONE);
                break;
            case 1:
                intent.setClass(mActivity, GameTwoActivity.class).putExtra("pos", RAW_TWO);
                break;
            case 2:
                intent.setClass(mActivity, GameFourActivity.class).putExtra("pos", RAW_FOUR);
                break;
            case 3:
                intent.setClass(mActivity, GameThreeActivity.class).putExtra("pos", RAW_THREE);
                break;
            case 4:
                break;
        }
        startActivity(intent);
    }
}
