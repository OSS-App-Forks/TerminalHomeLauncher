package com.bencarlisle15.terminalhomelauncher.tuils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class OutlineEditText extends AppCompatEditText {

    private int drawTimes = -1;

    public OutlineEditText(Context context) {
        super(context);
    }

    public OutlineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawTimes == -1) {
            drawTimes = getTag() == null ? 1 : OutlineTextView.redrawTimes;
        }

        for (int c = 0; c < drawTimes; c++) super.draw(canvas);
    }
}
