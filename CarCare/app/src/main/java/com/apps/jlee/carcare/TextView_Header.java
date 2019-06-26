package com.apps.jlee.carcare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

public class TextView_Header extends AppCompatTextView
{
    public TextView_Header(Context context)
    {
        super(context);
    }

    public TextView_Header(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TextView_Header(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = getPaint();
        //start at the vertical center of the textview
        int top = (getHeight() + getPaddingTop() - getPaddingBottom())/2;
        //start at the left margin
        int left = getPaddingLeft();
        //we draw all the way to the right
        int right = getWidth() - getPaddingRight();
        //we want the line to be 2 pixel thick
        int bottom = top + 2;
        int horizontalCenter = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        int textWidth = (int) paint.measureText(getText().toString());
        int halfTextWidth = textWidth/2;

        int padding = 16;
        int gravity = getGravity();

        if ((gravity & Gravity.LEFT) == Gravity.LEFT)
        {
            canvas.drawRect(left + textWidth + padding, top, right, bottom, paint);
        }
        else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT)
        {
            canvas.drawRect(left, top, right - textWidth - padding, bottom, paint);
        }
        else
        {
            canvas.drawRect(left, top, horizontalCenter - halfTextWidth - padding, bottom, paint);
            canvas.drawRect(horizontalCenter + halfTextWidth + padding, top, right, bottom, paint);
        }
    }
}
