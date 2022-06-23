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
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import cn.guo.onetext.R;

public class StrokeView extends RelativeLayout {

    private Context mContent;
    private ViewShape mViewShape;
    private StateListDrawable stateListDrawable;

    public StrokeView(Context context) {
        super(context);
        mContent = context;
        init();
    }

    public StrokeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContent = context;
        init();
    }

    public StrokeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContent = context;
        init();
    }

    public StrokeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContent = context;
        init();
    }

    private void init(){
        setBackground(getSelectorBackground());
    }

    private StateListDrawable getSelectorBackground(){
        mViewShape = new ViewShape(mContent);
        stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{},new ShapeDrawable(mViewShape));
        return stateListDrawable;
    }

    class ViewShape extends Shape {

        private Context mContent;
        private float mHeight;
        private float mWidth;
        private float paintWidth = 4.0f;
        private int paintColor = 0xFFE3E3E3;

        public ViewShape(Context context){
            mContent = context;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            int margin = dipToPx(mContent,32);
            int iconSize = dipToPx(mContent,48);
            int iconRight = dipToPx(mContent,16);
            paint.setColor(paintColor);
            paint.setStrokeWidth(paintWidth);
            canvas.drawLine(margin/2,margin/2-paintWidth/2,margin/2,mHeight-margin/2+paintWidth/2,paint);
            canvas.drawLine(mWidth-margin/2,margin/2-paintWidth/2,mWidth-margin/2,mHeight-margin/2+paintWidth/2,paint);

            canvas.drawLine(margin/2-paintWidth/2,margin/2,mWidth-margin/2-iconRight-iconSize+paintWidth/2,margin/2,paint);
            canvas.drawLine(mWidth-iconRight-margin/2-paintWidth/2,margin/2,mWidth-margin/2+paintWidth/2,margin/2,paint);

            Bitmap bitmap = getBitmap(mContent, R.drawable.ic_comma);
            Paint paintBitmap = new Paint();
            paintBitmap.setColorFilter( new PorterDuffColorFilter(paintColor, PorterDuff.Mode.SRC_IN)) ;
            canvas.drawBitmap(bitmap,mWidth-margin/2-iconRight-iconSize/2-bitmap.getWidth()/2,margin/2-bitmap.getHeight()/2,paintBitmap);
            bitmap.recycle();
            canvas.drawLine(margin/2-paintWidth/2,mHeight-margin/2,mWidth-margin/2+paintWidth/2,mHeight-margin/2,paint);
            System.gc();
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            mHeight = height;
            mWidth = width;
        }

        public int dipToPx(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
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
            }else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
            }
            return bitmap;
        }
    }
}
