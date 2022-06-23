package cn.guo.onetext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.guo.onetext.R;

public class RippleLayout extends LinearLayout {

    private Paint mPaint;
    private Random mRandom;
    private List<Circle> mCircles;
    private int mWidth;
    private int mHeight;

    private Random random = new Random();
    private int[] colorList = new int[]{
            0xFF8AD297,
            0xFFF9A883,
            0xFF88CFCC,
            0xFFF19C99,
            0xFFF7C56B,
            0xFFD2A596,
            0xFF67BDDE,
            0xFF9CCF5A,
            0xFF9AB4CF,
            0xFFE593AD,
            0xFFE2C38A,
            0xFFB29FD2,
            0xFFE2C490,
            0xFFE2C490,
    };

    private int[] darkColorList = new int[]{
            0xFF5ABF6C,
            0xFFF79060,
            0xFF63C0BD,
            0xFFED837F,
            0xFFF5B94E,
            0xFFCA9483,
            0xFF31A6D3,
            0xFF8BC73D,
            0xFF87A6C6,
            0xFFDF7999,
            0xFFD6A858,
            0xFF997FC3,
            0xFFDDB97B,
            0xFFd3dEe5};

    /**
     * 圆圈的个数
     */
    private int mCircleMax = 20;
    private int backgroundColor = 0x20ffffff;

    public RippleLayout(Context context) {
        super(context);
        init();
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RippleLayout);
        mCircleMax = array.getInteger(R.styleable.RippleLayout_count, mCircleMax);
        backgroundColor = array.getInteger(R.styleable.RippleLayout_background_color, backgroundColor);
        array.recycle();
    }

    private void init() {
        mRandom = new Random();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(getDarkRandomColor());
        setBackgroundColor(0x20ffffff);
    }

    public int getDarkRandomColor() {
        return darkColorList[random.nextInt(20) % colorList.length];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        initCircle();
    }

    private void initCircle() {
        if (mCircles == null) mCircles = new ArrayList<>();
        else mCircles.clear();
        for (int i = 0; i < mCircleMax; i++)
            mCircles.add(resetCircle(new Circle(),true));
    }

    @Override
    public void draw(Canvas canvas) {
        for (Circle circle : mCircles) {
            mPaint.setColor(circle.color | (((int) (0xFF * (1 - circle.currRadius * 1f / circle.maxRadius))) << 24));
            //canvas.drawColor(backgroundColor);
            canvas.drawCircle(circle.x, circle.y, circle.currRadius, mPaint);
            circle.currRadius += 1;
            if (circle.currRadius > circle.maxRadius)
                resetCircle(circle, false);
        }
        super.draw(canvas);
        postDelayed(action, 16);
    }

    private Circle resetCircle(Circle circle, boolean init) {
        circle.currRadius = 0;
        circle.x = mRandom.nextInt(mWidth);
        circle.y = mRandom.nextInt(mHeight);
        circle.maxRadius = mRandom.nextInt(120) + 50;
        circle.setColor(getDarkRandomColor());

        if(init) circle.currRadius = mRandom.nextInt(circle.maxRadius);
        return circle;
    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public void setCirclesCount(int count){
        mCircleMax = count;
        init();
        initCircle();
    }

    static class Circle {
        int x;
        int y;
        int maxRadius;
        int currRadius = 0;
        int color;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color & 0xFFFFFF;
        }
    }
}
