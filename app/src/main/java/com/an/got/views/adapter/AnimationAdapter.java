package com.an.got.views.adapter;

import android.animation.Animator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public abstract class AnimationAdapter <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Adapter<VH> mAdapter;
    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private boolean isFirstOnly = true;
    private int mCounter;
    private boolean mAnimsInitialized;

    public AnimationAdapter(Adapter<VH> adapter) {
        this.mAdapter = adapter;
    }

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return this.mAdapter.onCreateViewHolder(parent, viewType);
    }

    public void onBindViewHolder(VH holder, int position) {
        this.mAdapter.onBindViewHolder(holder, position);
        if(this.isFirstOnly && position <= this.mLastPosition) {
            MyViewHelper.clear(holder.itemView);
        } else {
            Animator[] animators = this.getAnimators(holder.itemView);
            int animArrLength = animators.length;

            LinearLayoutManager layoutManager = ((LinearLayoutManager)this.getRecyclerView().getLayoutManager());
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();

            if (mAnimsInitialized == false &&  firstVisiblePosition == 0)
                for (int i = 0; i < animArrLength; i++) {
                    final Animator anim = animators[i];
                    anim.setDuration((long) this.mDuration);
                    anim.setInterpolator(this.mInterpolator);
                    increaseCounter();

                    anim.setStartDelay(75 * position);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override  public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (decreaseCounter())
                                mAnimsInitialized = true;
                        }
                        @Override public void onAnimationCancel(Animator animation) {}
                        @Override public void onAnimationRepeat(Animator animation) {}
                    });
                    anim.start();
                }
            else
                holder.itemView.setAlpha(1);

            this.mLastPosition = position;
        }
    }

    protected synchronized boolean decreaseCounter() {
        mCounter--;
        return mCounter == 0;
    }

    public synchronized int getCounter() {
        return mCounter;
    }

    public synchronized void increaseCounter() {
        mCounter++;
    }

    public int getItemCount() {
        return this.mAdapter.getItemCount();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        this.mLastPosition = start;
    }

    protected abstract Animator[] getAnimators(View var1);

    protected abstract RecyclerView getRecyclerView();

    public void setFirstOnly(boolean firstOnly) {
        this.isFirstOnly = firstOnly;
    }

    public int getItemViewType(int position) {
        return this.mAdapter.getItemViewType(position);
    }

    public Adapter<VH> getWrappedAdapter() {
        return this.mAdapter;
    }



    public static class MyViewHelper {

        public static void clear(View v) {
            ViewCompat.setAlpha(v, 1.0F);
            ViewCompat.setScaleY(v, 1.0F);
            ViewCompat.setScaleX(v, 1.0F);
            ViewCompat.setTranslationY(v, 0.0F);
            ViewCompat.setTranslationX(v, 0.0F);
            ViewCompat.setRotation(v, 0.0F);
            ViewCompat.setRotationY(v, 0.0F);
            ViewCompat.setRotationX(v, 0.0F);
            v.setPivotY((float)(v.getMeasuredHeight() / 2));
            ViewCompat.setPivotX(v, (float)(v.getMeasuredWidth() / 2));
            ViewCompat.animate(v).setInterpolator((Interpolator)null);
        }
    }
}