package com.whinc.widget.lsystem;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.whinc.widget.lsystem.display.Display;

/**
 * Created by Administrator on 2015/11/20.
 */
public class LSystemView extends View{
    public static final String TAG = LSystemView.class.getSimpleName();

    private Display mDisplay = null;
    private float mOldX = 0.0f;
    private float mOldY = 0.0f;

    public LSystemView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public LSystemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public LSystemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LSystemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LSystemView);

        // Instantiate Display
        String className = typedArray.getString(R.styleable.LSystemView_ls_display_class);
            try {
                if (TextUtils.isEmpty(className)) {
                    mDisplay = PythagorasTreeDisplay.class.newInstance();
                } else {
                    mDisplay = (Display) Class.forName(className).newInstance();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        /* Display properties */
        mDisplay.setDirection(typedArray.getFloat(R.styleable.LSystemView_ls_direction, mDisplay.getDirection()));
        mDisplay.setAngle(typedArray.getFloat(R.styleable.LSystemView_ls_angle, mDisplay.getAngle()));
        mDisplay.setStep(typedArray.getFloat(R.styleable.LSystemView_ls_step, mDisplay.getStep()));
        mDisplay.setIterations(typedArray.getInteger(R.styleable.LSystemView_ls_iterations, mDisplay.getIterations()));
        mDisplay.setPercentX(typedArray.getFraction(R.styleable.LSystemView_ls_position_x, 1, 1, mDisplay.getPercentX()));
        mDisplay.setPercentY(typedArray.getFraction(R.styleable.LSystemView_ls_position_y, 1, 1, mDisplay.getPercentY()));
        mDisplay.setColor(typedArray.getColor(R.styleable.LSystemView_ls_paint_color, mDisplay.getColor()));

        typedArray.recycle();

    }

    public void setDisplay(@NonNull Display display) {
        if (mDisplay != display) {
            mDisplay = display;
            invalidate();
        }
    }

    public Display display() {
        return mDisplay;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDisplay != null) {
            mDisplay.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOldX = event.getX();
                mOldY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float x = mDisplay.getPercentX() * getMeasuredWidth();
                float y = mDisplay.getPercentY() * getMeasuredHeight();
                float deltaX = event.getX() - mOldX;
                float deltaY = event.getY() - mOldY;
                mDisplay.setPercentX((x + deltaX) / getMeasuredWidth());
                mDisplay.setPercentY((y + deltaY) / getMeasuredHeight());
                invalidate();
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
