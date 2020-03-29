package com.afunx.practice.utils;

import android.view.View;

public class MeasureSpecUtils {
    public static String toString(int measureSpec) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int mode = View.MeasureSpec.getMode(measureSpec);
        int size = View.MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case View.MeasureSpec.AT_MOST:
                sb.append("AT_MOST");
                break;
            case View.MeasureSpec.UNSPECIFIED:
                sb.append("UNSPECIFIED");
                break;
            case View.MeasureSpec.EXACTLY:
                sb.append("EXACTLY");
                break;
            default:
                throw new IllegalArgumentException();
        }
        sb.append(" ").append(size).append("}");
        return sb.toString();
    }

}
