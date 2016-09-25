package com.an.got.views;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.an.got.R;
import com.an.got.model.Answer;
import com.an.got.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectionPicker extends LinearLayout {

    public static final int LAYOUT_WIDTH_OFFSET = 3;

    private ViewTreeObserver mViewTreeObserver;
    private LayoutInflater mInflater;

    private List<Answer> mItems = new ArrayList<>();
    private LinearLayout mRow;
    private HashMap<String, Object> mCheckedItems = new HashMap<>();
    private OnItemClickListener mClickListener;
    private int mWidth;
    private int mItemMargin = 10;
    private int textPaddingLeft = 0;
    private int textPaddingRight = 0;
    private int textPaddingTop = 0;
    private int texPaddingBottom = 0;
    private int mAddIcon = android.R.drawable.ic_menu_add;
    private int mCancelIcon = android.R.drawable.ic_menu_close_clear_cancel;
    private int mLayoutBackgroundColorNormal = android.R.color.black;
    private int mLayoutBackgroundColorPressed = android.R.color.holo_red_dark;
    private int mLayoutBackgroundColorPressedCorrect = R.color.bg_button_green;
    private int mTextColor = R.color.button_green_text_color;
    private int mRadius = 10;
    private boolean mInitialized;

    private boolean simplifiedTags;


    public CollectionPicker(Context context) {
        this(context, null);
    }

    public CollectionPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CollectionPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray typeArray = context
                .obtainStyledAttributes(attrs, R.styleable.CollectionPicker);
        this.mItemMargin = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_itemMargin,
                        Utils.dp2Px(mItemMargin));
        this.textPaddingLeft = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingLeft,
                        Utils.dp2Px(textPaddingLeft));
        this.textPaddingRight = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingRight,
                        Utils.dp2Px(textPaddingRight));
        this.textPaddingTop = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingTop,
                        Utils.dp2Px(textPaddingTop));
        this.texPaddingBottom = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingBottom,
                        Utils.dp2Px(texPaddingBottom));
        this.mAddIcon = typeArray.getResourceId(R.styleable.CollectionPicker_cp_addIcon, mAddIcon);
        this.mCancelIcon = typeArray.getResourceId(R.styleable.CollectionPicker_cp_cancelIcon,
                mCancelIcon);
        this.mLayoutBackgroundColorNormal = typeArray.getColor(
                R.styleable.CollectionPicker_cp_itemBackgroundNormal,
                mLayoutBackgroundColorNormal);
        this.mLayoutBackgroundColorPressed = typeArray.getColor(
                R.styleable.CollectionPicker_cp_itemBackgroundPressed,
                mLayoutBackgroundColorPressed);
        this.mLayoutBackgroundColorPressedCorrect = typeArray.getColor(
                R.styleable.CollectionPicker_cp_itemBackgroundPressedCorrect,
                mLayoutBackgroundColorPressedCorrect);
        this.mRadius = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_itemRadius, mRadius);
        this.mTextColor = typeArray
                .getColor(R.styleable.CollectionPicker_cp_itemTextColor, mTextColor);
        this.simplifiedTags = typeArray
                .getBoolean(R.styleable.CollectionPicker_cp_simplified, false);
        typeArray.recycle();

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawItemView();
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    /**
     * Selected flags
     */
    public void setCheckedItems(HashMap<String, Object> checkedItems) {
        mCheckedItems = checkedItems;
    }

    public HashMap<String, Object> getCheckedItems() {
        return mCheckedItems;
    }

    public void drawItemView() {
        if (!mInitialized) {
            return;
        }

        clearUi();

        float totalPadding = getPaddingLeft() + getPaddingRight();
        int indexFrontView = 0;

        LayoutParams itemParams = getItemLayoutParams();

        for (int i = 0; i < mItems.size(); i++) {
            final Answer item = mItems.get(i);
            if (mCheckedItems != null && mCheckedItems.containsKey(item.getId())) {
                item.setSelected(true);
            }

            final int position = i;
            final View itemLayout = createItemView(item);
            final TextView itemTextView = (TextView) itemLayout.findViewById(R.id.answerTxt);
            itemTextView.setText(item.getAnswerDesc());
            itemTextView.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
            itemTextView.setTextColor(Color.parseColor("#48682c"));
            float itemWidth = itemTextView.getPaint().measureText(item.getAnswerDesc()) + textPaddingLeft + textPaddingRight;
            itemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateView(v);
                    item.setSelected(!item.isSelected());
                    if (item.isSelected()) {
                        mCheckedItems.put(String.valueOf(item.getId()), item);
                    } else {
                        mCheckedItems.remove(String.valueOf(item.getId()));
                    }

                    if (isJellyBeanAndAbove()) {
                        itemLayout.setBackground(getSelector(item));
                    } else {
                        itemLayout.setBackgroundDrawable(getSelector(item));
                    }
                    if(!item.isCorrectAnswer()) {
                        itemTextView.setTextColor(getResources().getColor(R.color.button_red_shadow_text_color));
                    }

                    if(item.isSelected()) {
                        itemLayout.setEnabled(false);
                        itemLayout.setClickable(false);
                    }
                    if (mClickListener != null) {
                        mClickListener.onClick(item, position);
                    }
                }
            });

            itemWidth += Utils.dp2Px(30) + textPaddingLeft + textPaddingRight;

            if (mWidth <= totalPadding + itemWidth + Utils
                    .dp2Px(LAYOUT_WIDTH_OFFSET)) {
                totalPadding = getPaddingLeft() + getPaddingRight();
                indexFrontView = i;
                addItemView(itemLayout, itemParams, true, i);
            } else {
                if (i != indexFrontView) {
                    itemParams.leftMargin = mItemMargin;
                    totalPadding += mItemMargin;
                }
                addItemView(itemLayout, itemParams, false, i);
            }
            totalPadding += itemWidth;
        }
        // }
    }

    private View createItemView(Answer item) {
        View view = mInflater.inflate(R.layout.tag_item, this, false);
        if (isJellyBeanAndAbove()) {
            view.setBackground(getSelector(item));
        } else {
            view.setBackgroundDrawable(getSelector(item));
        }

        return view;
    }

    private LayoutParams getItemLayoutParams() {
        LayoutParams itemParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        itemParams.bottomMargin = mItemMargin / 2;
        itemParams.topMargin = mItemMargin / 2;

        return itemParams;
    }

    private int getItemIcon(Boolean isSelected) {
        return isSelected ? mCancelIcon : mAddIcon;
    }

    private void clearUi() {
        removeAllViews();
        mRow = null;
    }

    private void addItemView(View itemView, ViewGroup.LayoutParams chipParams, boolean newLine,
                             int position) {
        if (mRow == null || newLine) {
            mRow = new LinearLayout(getContext());
            mRow.setGravity(Gravity.CENTER);
            mRow.setOrientation(HORIZONTAL);

            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            mRow.setLayoutParams(params);

            addView(mRow);
        }

        mRow.addView(itemView, chipParams);
        animateItemView(itemView, position);
    }

    private StateListDrawable getSelector(Answer item) {
        return item.isSelected() ? getSelectorSelected(item) : getSelectorNormal(item);
    }

    private StateListDrawable getSelectorNormal(Answer answer) {
        StateListDrawable states = new StateListDrawable();

        GradientDrawable gradientDrawable = new GradientDrawable();
        int color = !answer.isCorrectAnswer() ? mLayoutBackgroundColorPressed :
                mLayoutBackgroundColorPressedCorrect;
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorNormal);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{}, gradientDrawable);

        return states;
    }

    private StateListDrawable getSelectorSelected(Answer answer) {
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorNormal);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);

        gradientDrawable = new GradientDrawable();
        int color = !answer.isCorrectAnswer() ? mLayoutBackgroundColorPressed :
                mLayoutBackgroundColorPressedCorrect;
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{}, gradientDrawable);

        return states;
    }

    public List<Answer> getItems() {
        return mItems;
    }

    public void setItems(List<Answer> items) {
        mItems = items;
    }

    public void clearItems() {
        mItems.clear();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    private boolean isJellyBeanAndAbove() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    private void animateView(final View view) {
        view.setScaleY(1f);
        view.setScaleX(1f);

        view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .setStartDelay(0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reverseAnimation(view);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void reverseAnimation(View view) {
        view.setScaleY(1.2f);
        view.setScaleX(1.2f);

        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .setListener(null)
                .start();
    }

    private void animateItemView(View view, int position) {
        long animationDelay = 600;

        animationDelay += position * 30;

        view.setScaleY(0);
        view.setScaleX(0);
        view.animate()
                .scaleY(1)
                .scaleX(1)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null)
                .setStartDelay(animationDelay)
                .start();
    }

    public interface OnItemClickListener {
        void onClick(Answer item, int position);
    }
}