package com.example.stmclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;


public class ExtendedImageView  extends androidx.appcompat.widget.AppCompatImageView{
    int point_count = 0;
    Point a, b;
    final static int WIDTH = 3;

    public ExtendedImageView(Context context) {
        super(context);
    }

    public ExtendedImageView(Context context, AttributeSet attrst) {
        super(context, attrst);
    }

    public ExtendedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(WIDTH);
        if(point_count == 1){
            canvas.drawCircle(a.x, a.y, WIDTH, paint);
        }
        else if(point_count == 2){

            canvas.drawRect(a.x, a.y, b.x, b.y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            System.out.println("coordinates: " + x + " " + y);
            System.out.println(this.getDrawable().getIntrinsicWidth() + " " + this.getDrawable().getIntrinsicHeight());
            if(point_count == 0){
                a = new Point(x, y);
            }
            else if(point_count == 1){
                b = new Point(x, y);
            }
            else{
                point_count = 0;
                a = new Point(x, y);
            }
            point_count++;
            invalidate();
        }
        return true;
    }

    class Point{
        float x;
        float y;
        Point(float x_, float y_){
            x = x_;
            y = y_;
        }
    }
}
