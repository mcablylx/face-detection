package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * If there is no bug, Created by Mcablylx on 2017-12-26.
 * otherwise, I do not know who create it either
 * If anyone finds out I have bug, I can only kill him quietly
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private ViewGroup rootView;
    private static final int PICK_CODE = 1;
    //选个照片
    private Button get_image;
    //加个帽子
    private Button detect;
    //相片区域
    private ImageView id_photo;
    //照片地址
    private String ImagePath = null;
    //照片的bitmap对象
    private Bitmap myBitmapImage;

    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;
    ImageView imageView;
    private List<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.static_img_detection);
        initView();
        initEvent();
    }

    private void initEvent() {
        get_image.setOnClickListener(this);
        detect.setOnClickListener(this);
    }

    private void initView() {
        get_image = (Button) findViewById(R.id.get_image);
        detect = (Button) findViewById(R.id.detect);
        id_photo = (ImageView) findViewById(R.id.id_photo);
        rootView = (ViewGroup) findViewById(R.id.rlRootView);
        imageViews = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_image:
                //获取系统选择图片intent
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //开启选择图片功能响应码为PICK_CODE
                startActivityForResult(intent, PICK_CODE);
                break;

            case R.id.detect:
                try {
                    InputStream is = this.getResources().openRawResource(R.raw.lbpcascade_frontalface);
                    File cascadeDir = this.getDir("cascade", Context.MODE_PRIVATE);
                    File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                    FileOutputStream os = new FileOutputStream(cascadeFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();

                    mJavaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
                    if (mJavaDetector.empty()) {
                        mJavaDetector = null;
                    } else
                        cascadeFile.delete();

//                    mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                    cascadeDir.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Mat testmat = new Mat();
                if (myBitmapImage == null){
                    Toast.makeText(MainActivity.this, "选个照片啊",Toast.LENGTH_LONG).show();
                    break;
                }
                Utils.bitmapToMat(myBitmapImage, testmat);
                MatOfRect facedetect = new MatOfRect();

                mJavaDetector.detectMultiScale(testmat, facedetect);

                for (Rect rect : facedetect.toArray()) {
                    Imgproc.rectangle(testmat, new Point(rect.x, rect.y), new Point(
                            rect.x + rect.width, rect.y + rect.height), new Scalar(
                            255, 0, 0), 3);

                    imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(R.drawable.ic_cap);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(rect.width, rect.height);
                    imageView.setLayoutParams(lp);
                    imageView.setX(rect.x + rect.width);
                    imageView.setY(rect.y);
                    rootView.addView(imageView);
                    imageViews.add(imageView);
                }
                Utils.matToBitmap(testmat, myBitmapImage);
                id_photo.setImageBitmap(myBitmapImage);

                break;

        }
    }
    private Bitmap parseBitmap(Bitmap bitmap2, int faceCount) {
        Bitmap bitmap = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);

       canvas.drawBitmap(bitmap2, 0, 0, mPaint);
//        for (int i = 0; i < faceCount; i++) {
//            //双眼的中心点
//            PointF midPoint = new PointF();
//            faces[i].getMidPoint(midPoint);
//            //双眼的距离
//            float eyeDistance = faces[i].eyesDistance();
//            //画矩形
//            canvas.drawRect(midPoint.x - eyeDistance, midPoint.y - eyeDistance, midPoint.x + eyeDistance, midPoint.y + eyeDistance, mPaint);
//        }

        return bitmap;
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //初始化opencv成功的回调
                    break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("FUCK", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("FUCK", "OpenCV library found inside package. Using it!");
        }
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_CODE) {
            if (intent != null) {
                //获取图片路径
                //获取所有图片资源
                Uri uri = intent.getData();
                //设置指针获得一个ContentResolver的实例
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                //返回索引项位置
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                //返回索引项路径
                ImagePath = cursor.getString(index);
                cursor.close();
                //这个jar包要求请求的图片大小不得超过3m所以要进行一个压缩图片操作
                resizePhoto();
                id_photo.setImageBitmap(myBitmapImage);

                if (imageViews.size() > 0) {
                    for (ImageView imageView : imageViews) {
                        rootView.removeView(imageView);
                    }
                    imageViews.clear();
                }

            }
        }
    }

    private void resizePhoto() {
        //得到BitmapFactory的操作权
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 如果设置为 true ，不获取图片，不分配内存，但会返回图片的高宽度信息。
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(ImagePath, options);
        //计算宽高要尽可能小于1024
        double ratio = Math.max(options.outWidth * 1.0d / 1024f, options.outHeight * 1.0d / 1024f);
        //设置图片缩放的倍数。假如设为 4 ，则宽和高都为原来的 1/4 ，则图是原来的 1/16 。
        options.inSampleSize = (int) Math.ceil(ratio);
        //我们这里并想让他显示图片所以这里要置为false
        options.inJustDecodeBounds = false;
        //利用Options的这些值就可以高效的得到一幅缩略图。
        myBitmapImage = BitmapFactory.decodeFile(ImagePath, options);
    }
}
