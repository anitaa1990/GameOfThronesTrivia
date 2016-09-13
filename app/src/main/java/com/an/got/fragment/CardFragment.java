package com.an.got.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.an.got.R;
import com.an.got.activity.GameOneActivity;
import com.an.got.adapter.CardAdapter;
import com.an.got.base.BaseFragment;
import com.an.got.views.RevealActivity;

import java.util.Arrays;
import java.util.List;


public class CardFragment extends BaseFragment {


    public static CardFragment newInstance(int position) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }

    private CardView mCardView;
    private TextView title;
    private TextView description;

    private int itemPosition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemPosition = getArguments().getInt("pos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_adapter, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);

        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);


        mCardView.setMaxCardElevation(mCardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                mCardView.getLocationOnScreen(location);
                Intent intent = new Intent(mActivity, GameOneActivity.class);
                intent.putExtra(RevealActivity.REVEAL_X, location[0]);
                intent.putExtra(RevealActivity.REVEAL_Y, location[1]);
                startActivity(intent);
            }
        });

        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.titles));
        List<String> descriptions = Arrays.asList(getResources().getStringArray(R.array.descriptions));
        title.setText(titles.get(itemPosition));
        description.setText(descriptions.get(itemPosition));

        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }
}
