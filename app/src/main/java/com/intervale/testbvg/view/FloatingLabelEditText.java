package com.intervale.testbvg.view;
/*
 * Copyright (c) 2015. Intervale, CJSC.  All rights reserved.
 * PROPRIETARY/CONFIDENTIAL
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intervale.testbvg.R;

/*
    Created by vmoiseenko on 27.04.2015.
 */
public class FloatingLabelEditText extends RelativeLayout {

    public static final int ANIMATION_DURATION = 300;
    public static final String DEFAULT_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private TextView captionView;
    private EditTextFormatted editText;
    private TextView errorView;
    private String hint;

    private AnimatorSet moveCaptionUpAnimator;
    private AnimatorSet moveCaptionUpFocusedAnimator;
    private AnimatorSet moveCaptionDownAnimator;
    private Animator currentAnimation;

    private int captionTextColor;
    private int captionSelectedTextColor;

    private boolean isCaptionUp = false;
    private boolean isCaptionAlwaysOnTop = false;

    private OnFocusChangeListener onFocusChangeListener;

    public FloatingLabelEditText(Context context) {
        super(context);
    }

    public FloatingLabelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent(attrs);
    }

    public FloatingLabelEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent(attrs);
    }

    @TargetApi(21)
    public FloatingLabelEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initComponent(attrs);
    }

    private void initComponent(AttributeSet attrs) {
        inflateView();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingLabelEditText);

        captionView = (TextView) findViewById(R.id.caption_view);
        captionTextColor = typedArray.getColor(R.styleable.FloatingLabelEditText_fl_captionColor, -1);
        captionSelectedTextColor = typedArray.getColor(R.styleable.FloatingLabelEditText_fl_captionSelectedColor, -1);
        captionView.setTextColor(captionTextColor);
        captionView.setText(typedArray.getString(R.styleable.FloatingLabelEditText_fl_caption));
        captionView.setPivotX(0);
        captionView.setPivotY(0);

        editText = new EditTextFormatted(getContext());
        editText.setBackgroundColor(0);
        setEditTextDefaultLine();
        editText.setPadding(0, editText.getPaddingBottom(), 0, editText.getPaddingTop());
        editText.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.spacing_small));
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setTextSize(16);
        int maxLength = initMaxLength(attrs);
        int inputType = attrs.getAttributeIntValue(DEFAULT_NAMESPACE, "inputType", -1);
        if (inputType != -1) {
            editText.setInputType(inputType);
        }

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setText(typedArray.getString(R.styleable.FloatingLabelEditText_fl_text));
        editText.setTextColor(typedArray.getColor(R.styleable.FloatingLabelEditText_fl_textColor, -1));
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                boolean isTextEmpty = editText.getText().length() == 0;
                if (hasFocus) {
                    if (isTextEmpty) moveCaptionUp();
                    else captionView.setTextColor(captionSelectedTextColor);
                } else {
                    if (isTextEmpty) moveCaptionDown();
                    else captionView.setTextColor(captionTextColor);
                }
                if (onFocusChangeListener != null) {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0 && after > 0) {
                    moveCaptionUp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                errorView.setVisibility(View.GONE);
                setEditTextDefaultLine();
            }
        });

        errorView = (TextView) findViewById(R.id.error_view);

        LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ((ViewGroup) findViewById(R.id.editTextLayout)).addView(editText, params);

        hint = typedArray.getString(R.styleable.FloatingLabelEditText_fl_hint);
        int hintTextColor = typedArray.getColor(R.styleable.FloatingLabelEditText_fl_hintColor, -1);

        createAnimations();

        if (editText.getText().length() > 0) {
            moveCaptionUp();
            captionView.setTextColor(captionTextColor);
            if (!TextUtils.isEmpty(hint)) {
                editText.setHint(hint);
            }
        } else {
            captionView.setTextColor(hintTextColor);
        }
        typedArray.recycle();
    }

    private int initMaxLength(AttributeSet attrs) {
        int maxLength = attrs.getAttributeIntValue(DEFAULT_NAMESPACE, "maxLength", -1);
        if (maxLength == -1) {
            int resId = attrs.getAttributeResourceValue(DEFAULT_NAMESPACE, "maxLength", -1);
            if (resId != -1) {
                maxLength = getResources().getInteger(resId);
            }
        }
        if (maxLength == -1) maxLength = 25;
        return maxLength;
    }

    protected void inflateView() {
        LayoutInflater.from(getContext()).inflate(R.layout.floating_label_edit_text, this, true);
    }

    private void moveCaptionDown() {
        if (isCaptionAlwaysOnTop) {
            captionView.setTextColor(captionTextColor);
            return;
        }
        if (!isCaptionUp) return;
        startAnimation(moveCaptionDownAnimator);
        isCaptionUp = false;
    }

    private void moveCaptionUp() {
        if (isCaptionAlwaysOnTop) {
            captionView.setTextColor(captionSelectedTextColor);
            return;
        }
        if (isCaptionUp) return;
        if (editText.isFocused()) {
            startAnimation(moveCaptionUpFocusedAnimator);
        } else {
            startAnimation(moveCaptionUpAnimator);
        }
        isCaptionUp = true;
    }

    private void startAnimation(Animator animator) {
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        animator.start();
    }

    private void createAnimations() {
        float dy = dpToPx(10) + spToPx(12);

        Animator.AnimatorListener animatorListener = new SimpleAnimatorListener() {
            public void onAnimationStart(Animator animation) {
                currentAnimation = animation;
            }

            public void onAnimationEnd(Animator animation) {
                currentAnimation = null;
            }

            public void onAnimationCancel(Animator animation) {
                currentAnimation = null;
            }
        };

        ObjectAnimator titleUpY = ObjectAnimator.ofFloat(captionView, "translationY", 0, -dy);
        ObjectAnimator titleDownY = ObjectAnimator.ofFloat(captionView, "translationY", -dy, 0);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(captionView, "scaleY", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(captionView, "scaleY", 0.78f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(captionView, "scaleX", 1.0f);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(captionView, "scaleX", 0.78f);
        ObjectAnimator pivotY = ObjectAnimator.ofFloat(captionView, "pivotY", 0);
        ObjectAnimator pivotX = ObjectAnimator.ofFloat(captionView, "pivotY", 0);

        ObjectAnimator colorDownAnimator = ObjectAnimator.ofInt(captionView, "textColor", captionSelectedTextColor, captionTextColor);
        colorDownAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator colorUpAnimator = ObjectAnimator.ofInt(captionView, "textColor", captionTextColor, captionSelectedTextColor);
        colorUpAnimator.setEvaluator(new ArgbEvaluator());

        //Анимация подсказки в заголовок
        moveCaptionUpAnimator = new AnimatorSet();
        moveCaptionUpAnimator.playTogether(pivotX, pivotY, titleUpY, scaleUpX, scaleUpY);
        moveCaptionUpAnimator.setInterpolator(new LinearInterpolator());
        moveCaptionUpAnimator.setDuration(ANIMATION_DURATION);
        moveCaptionUpAnimator.addListener(animatorListener);
        moveCaptionUpAnimator.addListener(new SimpleAnimatorListener() {
            public void onAnimationEnd(Animator animation) {
                if (!TextUtils.isEmpty(hint)) {
                    editText.setHint(hint);
                }
            }
        });

        moveCaptionUpFocusedAnimator = new AnimatorSet();
        moveCaptionUpFocusedAnimator.playTogether(pivotX, pivotY, titleUpY, scaleUpX, scaleUpY, colorUpAnimator);
        moveCaptionUpFocusedAnimator.setInterpolator(new LinearInterpolator());
        moveCaptionUpFocusedAnimator.setDuration(ANIMATION_DURATION);
        moveCaptionUpFocusedAnimator.addListener(animatorListener);
        moveCaptionUpFocusedAnimator.addListener(new SimpleAnimatorListener() {
            public void onAnimationEnd(Animator animation) {
                if (!TextUtils.isEmpty(hint)) {
                    editText.setHint(hint);
                }
            }
        });

        //Анимация подсказки в заголовок
        moveCaptionDownAnimator = new AnimatorSet();
        moveCaptionDownAnimator.playTogether(pivotX, pivotY, titleDownY, scaleDownX, scaleDownY, colorDownAnimator);
        moveCaptionDownAnimator.setInterpolator(new LinearInterpolator());
        moveCaptionDownAnimator.setDuration(ANIMATION_DURATION);
        moveCaptionDownAnimator.addListener(animatorListener);
        moveCaptionDownAnimator.addListener(new SimpleAnimatorListener() {
            public void onAnimationStart(Animator animation) {
                editText.setHint(null);
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }

    public void setCaption(String title) {
        this.captionView.setText(title);
    }

    public void setTitleColor(int color) {
        this.captionView.setTextColor(color);
    }

    public String getCaption() {
        return this.captionView.getText().toString();
    }

    public void setText(String text) {
        this.editText.setText(text);
    }

    public void setTextColor(int color) {
        this.editText.setTextColor(color);
    }

    public String getText() {
        return this.editText.getText().toString();
    }

    protected float dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (float) Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    protected float spToPx(int sp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return sp * displayMetrics.scaledDensity;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        onFocusChangeListener = listener;
    }

    public void clearFocus() {
        editText.clearFocus();
    }

    public void setInputType(int inputType) {
        editText.setInputType(inputType);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public EditTextFormatted getEditText() {
        return editText;
    }

    public void setError(String errorMessage) {
        if (errorMessage == null) {
            errorView.setVisibility(View.GONE);
            setEditTextDefaultLine();
        } else {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText(errorMessage);
            editText.requestFocus();
            setEditTextErrorLine();
        }
    }

    private void setEditTextErrorLine() {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_line_error);
    }

    private void setEditTextDefaultLine() {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_line);
    }

    public void clearText() {
        editText.getText().clear();
    }

    public void setCaptionAlwaysOnTop() {
        moveCaptionUp();
        isCaptionAlwaysOnTop = true;
    }
}
