package cn.guo.onetext.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CardStyleView8 extends LinearLayout {

    private Context mContent;
    private CardShape8 mVShape;
    private StateListDrawable stateListDrawable;

    public CardStyleView8(Context context) {
        super(context);
        mContent = context;
        init();
    }

    public CardStyleView8(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContent = context;
        init();
    }

    public CardStyleView8(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContent = context;
        init();
    }

    public CardStyleView8(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContent = context;
        init();
    }

    private void init(){
        setBackground(getSelectorBackground());
    }

    private StateListDrawable getSelectorBackground(){
        mVShape = new CardShape8(mContent);
        stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{},new ShapeDrawable(mVShape));
        return stateListDrawable;
    }
    class CardShape8 extends Shape {

        private Context mContext;
        private float mHeight;
        private float mWidth;
        private float mPaintWidth = 4.0f;
        private final int margins = 48;
        private final int shapeHeights = 16;
        private final int shapeWidths = 12;

        /**
         * left   48 dp    margin
         * top    48+8 dp  margin
         * right  48+8 dp  margin
         * bottom 48+12 dp margin+shapeWidth
         * @param context
         */

        public CardShape8(Context context){
            mContext=context;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int shapeWidth = dipToPx(mContext,shapeWidths);
            final int shapeHeight = dipToPx(mContext,shapeHeights);
            final int margin = dipToPx(mContext,margins)/2;
            final int swidth = shapeWidth;
            final int sheight = shapeWidth;
            paint.setStrokeWidth(mPaintWidth);
            paint.setColor(0xffe3e3e3);
            //上
            canvas.drawLine(margin-mPaintWidth/2,margin+sheight,mWidth-margin+mPaintWidth/2-swidth,margin+sheight,paint);
            //下
            canvas.drawLine(margin-mPaintWidth/2+shapeWidth,mHeight-margin-shapeHeight,mWidth-margin+mPaintWidth/2-swidth,mHeight-margin-shapeHeight,paint);
            //左
            canvas.drawLine(margin,margin-mPaintWidth/2+sheight,margin,mHeight-margin+mPaintWidth/2,paint);
            //右
            canvas.drawLine(mWidth-margin-swidth,margin-mPaintWidth/2+sheight,mWidth-margin-swidth,mHeight-margin+mPaintWidth/2-shapeHeight,paint);
            //斜
            canvas.drawLine(margin,mHeight-margin,margin+shapeWidth,mHeight-margin-shapeHeight,paint);
            //
            canvas.drawLine(margin-mPaintWidth/2+swidth-mPaintWidth/2,margin,mWidth-margin+mPaintWidth/2,margin,paint);
            canvas.drawLine(mWidth-margin,margin,mWidth-margin,mHeight-margin-shapeHeight-shapeHeight+mPaintWidth/2,paint);
            canvas.drawLine(mWidth-margin,mHeight-margin-shapeHeight-shapeHeight,mWidth-margin-swidth,mHeight-margin-shapeHeight-shapeHeight,paint);
            canvas.drawLine(margin-mPaintWidth/2+swidth,margin,margin-mPaintWidth/2+swidth,margin+sheight,paint);
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            mHeight=height;
            mWidth=width;

        }

        public int dipToPx(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }

}
