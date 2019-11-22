package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View {

    private final BitmapFactory.Options mOptions;
    private Rect mRect;
    private int mImageWidth;
    private int mImageHeight;
    private BitmapRegionDecoder mDecoder;
    private int mViewWidth;
    private int mViewHeigh;
    private int mViewWid;
    private float scale;
    private Bitmap bitmap;

    public BigView(Context context) {
        this(context, null, 0);
    }

    public BigView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //指定要加载的矩形区域
        mRect = new Rect();
        //解码图片的配置
        mOptions = new BitmapFactory.Options();
    }

    /**
     * 输入一张图片的输入流
     *
     * @param inputStream
     */
    public void setImage(InputStream inputStream) {
        //先读取原图片的宽高
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        //复用
        mOptions.inMutable = true;
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;
        //创建区域解码器 用于区域解码图片
        try {
            mDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得测量的View的宽高
        mViewWidth = getMeasuredWidth();
        mViewHeigh = getMeasuredHeight();

        if (null == mDecoder) {
            return;
        }

        //确定要加载的图片的区域
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
//          mViewWidth          mViewHeigh
//          mImageWidth         mImageHeight
        //视图的宽  除于  图片的宽
        //因为这里只是对宽进行对齐，所以可以计算出宽度的缩放因子
        scale = mViewWidth / (float) mImageWidth;
        mRect.bottom = (int) (mViewHeigh / scale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mDecoder) {
            return;
        }
        //复用上一张bitmap
        mOptions.inBitmap = bitmap;
        //解码指定区域
        bitmap = mDecoder.decodeRegion(mRect, mOptions);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        //画出来
        canvas.drawBitmap(bitmap, matrix, null);
    }
}
















