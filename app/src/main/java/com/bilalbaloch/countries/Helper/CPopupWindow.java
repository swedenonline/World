package com.bilalbaloch.countries.Helper;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.bilalbaloch.countries.R;

/**
 * @author bilalbaloch
 */
public class CPopupWindow extends PopupWindow {

    private View parentView,positionView;

    public CPopupWindow(View v, View parent, View position, int width, int height, boolean flag) {
        super(v, width, height, flag);
        parentView = parent;
        positionView = position;
        init();
    }

    public void init() {
        // make it outside touchable to dismiss the popup window
        setOutsideTouchable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        setAnimationStyle(R.style.fadeInOut);
        showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

}
