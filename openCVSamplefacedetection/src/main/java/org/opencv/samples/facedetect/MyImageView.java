package org.opencv.samples.facedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import org.opencv.core.Rect;

/**
 * If there is no bug, Created by Mcablylx on 2017/12/29.
 * otherwise, I do not know who create it either
 * If anyone finds out I have bug, I can only kill him quietly
 */

public class MyImageView extends ImageView {

    private Rect rect;

    public void setRect(Rect rect) {
        this.rect = rect;
        invalidate();
        ((View)this.getParent()).invalidate();
    }

    public MyImageView(Context context) {
        super(context);

    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint mPaint = new Paint();

        int x = rect.x;
        int y = rect.y;
        int height = rect.height;
        int width = rect.width;

        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_cap);
        assert drawable != null;
        Bitmap bitmap = drawable.getBitmap();
        int height1 = bitmap.getHeight();
        int width1 = bitmap.getWidth();
        Matrix matrix = new Matrix();
        float sx = (float) width / (float) width1;
        float sy = (float) height/(float)height1;
        matrix.postScale(sx, sy);      //缩放,按比例
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width1, height1, matrix, true);
        canvas.drawBitmap(newBitmap, x, y-newBitmap.getHeight(), mPaint);

    }
}
