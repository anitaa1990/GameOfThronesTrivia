package com.an.got.adapter;

import android.support.v7.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    int getCount();
    float getBaseElevation();
    CardView getCardViewAt(int position);
}