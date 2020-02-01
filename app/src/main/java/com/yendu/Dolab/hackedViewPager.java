package com.yendu.Dolab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class hackedViewPager extends ViewPager {
    public hackedViewPager(@NonNull Context context) {
        super(context);
    }

    public hackedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try{
            return super.onInterceptTouchEvent(ev);
        }catch (Exception e){
            return false;
        }

    }
}
