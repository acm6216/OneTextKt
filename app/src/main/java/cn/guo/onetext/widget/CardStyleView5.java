package cn.guo.onetext.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.guo.onetext.R;

public class CardStyleView5 extends View {

    /**
     * @author:Guo.CN
     * @since:2020/2/26
     */

    private Paint mPaint;
    private Context mContent;
    private CardShape5 cardShape5;
    private StateListDrawable stateListDrawable;

    public CardStyleView5(Context context) {
        super(context);
        mContent=context;
        init();
    }

    public CardStyleView5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContent=context;
        init();
    }

    public CardStyleView5(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContent=context;
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        this.setBackground(getSelectorBackground());
    }

    private StateListDrawable getSelectorBackground(){
        cardShape5 = new CardShape5(mContent);
        stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{},new ShapeDrawable(cardShape5));
        return stateListDrawable;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //防止阻塞线程，不在这里绘制
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                //精确值模式，当控件的layout_width和layout_height属性指定为具体数值或match_parent时。
                break;
            case MeasureSpec.AT_MOST:
                //最大值模式，当空间的宽高设置为wrap_content时。
                break;
            case MeasureSpec.UNSPECIFIED:
                //未指定模式，View想多大就多大，通常在绘制自定义View时才会用。
                break;
        }
        setMeasuredDimension(widthSize,heightSize);
    }

    class CardShape5 extends Shape {

        private Context mContent;
        private float mWidth;
        private float mHeight;

        public CardShape5(Context context){
            mContent = context;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            float width = mWidth;
            int radius = 140;
            float dwidth = 15f;
            int dradius[]={28,25,25,23,31,30,25,32,24,29,26,31,23,24};
            float d = (float) dip2px(mContent,48);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xff2c2a2d);
            for(int temp:dradius) {
                paint.setStrokeWidth(dwidth);
                canvas.drawCircle(width-d*4/5, d*4/5, radius, paint);
                radius+=temp;
                dwidth--;
            }
            paint.setColor(0xffbe2219);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width-d*4/5,d*4/5,70,paint);
            Bitmap bitmap = getBitmap(mContent, R.drawable.ic_wy);
            Paint paintBitmap = new Paint();
            paintBitmap.setColorFilter( new PorterDuffColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN)) ;
            canvas.drawBitmap(bitmap,width-d*4/5-bitmap.getWidth()/2,d*4/5-bitmap.getHeight()/2,paintBitmap);
            bitmap.recycle();
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            mWidth = width;
            mHeight = height;

        }

        private Bitmap getBitmap(Context context, int vectorDrawableId) {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
                Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
                bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
                vectorDrawable.setTint(0xffffffff);
            }else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
            }
            return bitmap;
        }

        private int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }

}
