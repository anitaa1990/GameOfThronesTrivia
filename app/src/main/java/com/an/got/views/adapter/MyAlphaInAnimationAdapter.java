package com.an.got.views.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyAlphaInAnimationAdapter extends AnimationAdapter {
    private static final float DEFAULT_ALPHA_FROM = 0.0F;
    private final float mFrom;
    private RecyclerView recyclerView;

    public MyAlphaInAnimationAdapter(RecyclerView.Adapter adapter) {
        this(adapter, 0.0F);
    }

    public MyAlphaInAnimationAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        this.mFrom = from;
    }

    protected Animator[] getAnimators(View view) {
        view.setAlpha(0);
        return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", new float[]{this.mFrom, 1.0F})};
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}