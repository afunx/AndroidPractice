package com.afunx.practice;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.afunx.practice.utils.MeasureSpecUtils;

public class FlowLayout extends ViewGroup {

    private static final boolean DEBUG = true;

    private static final String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (DEBUG) {
            Log.d(TAG, "onMeasure() widthMeasureSpec: " + MeasureSpecUtils.toString(widthMeasureSpec) + ", heightMeasureSpec: " + MeasureSpecUtils.toString(heightMeasureSpec));
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            if (DEBUG) {
                Log.i(TAG, "onMeasure() widthMeasureSpec: " + MeasureSpecUtils.toString(widthMeasureSpec) + ", heightMeasureSpec: " + MeasureSpecUtils.toString(heightMeasureSpec));
            }
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int paddingHorizontal = getPaddingLeft() + getPaddingRight();

        if (DEBUG) {
            Log.d(TAG, "onMeasure() paddingHorizontal: " + paddingHorizontal);
        }

        int maxWidth = 0;

        int widthUsed = 0;
        int heightUsed = 0;

        int currentHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
                int childMeasuredWidth = child.getMeasuredWidth();
                int childMeasuredHeight = child.getMeasuredHeight();

                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int widthWithMargins = childMeasuredWidth + lp.leftMargin + lp.rightMargin;
                final int heightWithMargins = childMeasuredHeight + lp.topMargin + lp.bottomMargin;

                if (DEBUG) {
                    Log.d(TAG, "onMeasure() i: " + i
                            + ", childMeasuredWidth: " + MeasureSpecUtils.toString(childMeasuredWidth)
                            + ", childMeasuredHeight: " + MeasureSpecUtils.toString(childMeasuredHeight)
                            + ", widthWithMargins: " + widthWithMargins
                            + ", heightWithMargins: " + heightWithMargins);
                }

                // check whether new line is required
                boolean newline = (heightWithMargins != currentHeight)
                        || (widthUsed != 0 && widthUsed + widthWithMargins + paddingHorizontal > widthSize);

                if (newline) {
                    if (widthUsed > maxWidth) {
                        maxWidth = widthUsed;
                        Log.i(TAG, "onMeasure() i: " + i + ", maxWidth: " + maxWidth);
                    }
                    widthUsed = 0;
                    heightUsed += currentHeight;
                    currentHeight = heightWithMargins;
                }

                // update width used
                widthUsed += widthWithMargins;
            }
        }
        if (widthUsed != 0) {
            heightUsed += currentHeight;
            if (widthUsed > maxWidth) {
                maxWidth = widthUsed;
                Log.i(TAG, "onMeasure() maxWidth: " + maxWidth);
            }
        }

        maxWidth += paddingHorizontal;
        heightUsed += paddingHorizontal;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(widthSize, maxWidth), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(heightSize, heightUsed), MeasureSpec.EXACTLY);

        if (DEBUG) {
            Log.i(TAG, "onMeasure() widthMeasureSpec: " + MeasureSpecUtils.toString(widthMeasureSpec) + ", heightMeasureSpec: " + MeasureSpecUtils.toString(heightMeasureSpec));
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (DEBUG) {
            Log.d(TAG, "onLayout() l: " + l + ", t: " + t + ", r: " + r + ", b: " + b);
        }
        final int parentLeft = l + getPaddingLeft();
        final int parentTop = t + getPaddingTop();
        final int parentRight = r - getPaddingRight();

        int currentLeft = parentLeft;
        int currentTop = parentTop;

        int currentHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int childMeasuredWidth = child.getMeasuredWidth();
                final int childMeasuredHeight = child.getMeasuredHeight();

                final int widthWithMargins = childMeasuredWidth + lp.leftMargin + lp.rightMargin;
                final int heightWithMargins = childMeasuredHeight + lp.topMargin + lp.bottomMargin;

                // check whether new line is required
                boolean newline = (heightWithMargins != currentHeight)
                        || (currentLeft != parentLeft && currentLeft + widthWithMargins > parentRight);

                if (DEBUG) {
                    Log.d(TAG, "onLayout() currentHeight: " + currentHeight + ", heightWithMargins: " + heightWithMargins
                            + ", currentLeft: " + currentHeight + ", parentLeft: " + parentLeft + ", widthWithMargis: " + widthWithMargins
                            + ", newline: " + newline);
                }

                // new line
                if (newline) {
                    currentLeft = parentLeft;
                    currentTop += currentHeight;
                    currentHeight = heightWithMargins;
                    if (DEBUG) {
                        Log.i(TAG, "onLayout() newline currentHeight: " + currentHeight + ", currentLeft: " + currentLeft + ", currentTop: " + currentTop);
                    }
                }

                int childLeft = currentLeft + lp.leftMargin;
                int childTop = currentTop + lp.topMargin;

                if (DEBUG) {
                    Log.d(TAG, "onLayout() childLeft: " + childLeft + ", childTop: " + childTop + ", childMeasuredWidth: " + childMeasuredWidth + ", childMeasuredHeight: " + childMeasuredHeight);
                }
                child.layout(childLeft, childTop, childLeft + childMeasuredWidth, childTop + childMeasuredHeight);

                // update current left
                currentLeft += widthWithMargins;
            }

        }
    }
}
