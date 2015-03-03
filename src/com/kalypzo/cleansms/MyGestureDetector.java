package com.kalypzo.cleansms;

import sms.kalypzo.cleansms.R;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MyGestureDetector extends SimpleOnGestureListener {
    private static final float SWIPE_MIN_DISTANCE = 4;
	private static final float SWIPE_THRESHOLD_VELOCITY = 2;
	private ListView list;

    public MyGestureDetector(ListView list) {
        this.list = list;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if ((e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY))
            if (showDeleteButton(e1))
                return true;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    private boolean showDeleteButton(MotionEvent e1) {
        int pos = list.pointToPosition((int)e1.getX(), (int)e1.getY());
        return showDeleteButton(pos);
    }

    private boolean showDeleteButton(int pos) {
        View child = list.getChildAt(pos);
        if (child != null){
            Button delete = (Button)child.findViewById(R.drawable.logo);
            if (delete != null)
                if (delete.getVisibility() == View.INVISIBLE)
                    delete.setVisibility(View.VISIBLE);
                else
                    delete.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }
}