package org.opencv.samples.facedetect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * If there is no bug, Created by Mcablylx on 2017-12-28.
 * otherwise, I do not know who create it either
 * If anyone finds out I have bug, I can only kill him quietly
 */

public class MainActivity2 extends Activity {
    private final int MAX_FACES = 5;    //最大可识别的人脸数
    private ImageView im;
    private Bitmap mFaceBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        im = new ImageView(this);

        setContentView(im, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mFaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ccc);
        im.setImageBitmap(mFaceBitmap);

        //因为这是一个耗时的操作，所以放到另一个线程中运行
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
                //格式必须为RGB_565才可以识别
                Bitmap bmp = mFaceBitmap.copy(Bitmap.Config.RGB_565, true);
                //返回识别的人脸数
                int faceCount = new FaceDetector(bmp.getWidth(), bmp.getHeight(), MAX_FACES).findFaces(bmp, faces);
                bmp.recycle();
                bmp = null;
                Log.e("tag", "识别的人脸数:" + faceCount);

                if (faceCount > 0) {
                    final Bitmap bitmap = parseBitmap(faces, faceCount);
                    //显示处理后的图片
                    im.post(new Runnable() {
                        @Override
                        public void run() {
                            im.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 在人脸上画矩形
     */
    private Bitmap parseBitmap(FaceDetector.Face[] faces, int faceCount) {
        Bitmap bitmap = Bitmap.createBitmap(mFaceBitmap.getWidth(), mFaceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawBitmap(mFaceBitmap, 0, 0, mPaint);
        for (int i = 0; i < faceCount; i++) {
            //双眼的中心点
            PointF midPoint = new PointF();
            faces[i].getMidPoint(midPoint);
            //双眼的距离
            float eyeDistance = faces[i].eyesDistance();
            //画矩形
            canvas.drawRect(midPoint.x - eyeDistance, midPoint.y - eyeDistance, midPoint.x + eyeDistance, midPoint.y + eyeDistance, mPaint);
        }

        return bitmap;
    }
}
