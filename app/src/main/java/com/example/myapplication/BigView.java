package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private final BitmapFactory.Options mOptions;
    private final GestureDetector gestureDetector;
    private final Scroller scroller;
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

        gestureDetector = new GestureDetector(context, this);
        setOnTouchListener(this);

        scroller = new Scroller(context);
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

    /**
     * 手指按下屏幕
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {

        if (!scroller.isFinished()) {
            scroller.forceFinished(true);
        }

        //继续接收后续事件
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    /**
     * 手指不离开屏幕 拖动
     *
     * @param motionEvent  手指按下去的事件
     * @param motionEvent1 当前手指的事件
     * @param x            x方向移动的距离
     * @param y            y方向移动的距离
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {
        //手指从下往上，图片也要往上 y是负数
        //改变加载图片的区域
        mRect.offset(0, (int) y);

        //bottom 大于 图片的高时，要做处理
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int) (mViewHeigh / scale);
        }
        //top 小于 0时，也要做处理
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeigh / scale);
        }
        //重绘
        invalidate();
        return false;
    }

    /**
     * 手指离开屏幕 滑动
     *
     * @param motionEvent
     * @param motionEvent1
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {
        scroller.fling(0, mRect.top, 0, (int) -y, 0, 0, 0, mImageHeight - (int) (mViewHeigh / scale));
        return false;
    }

    //获取计算结果并重绘
    @Override
    public void computeScroll() {
        if (scroller.isFinished()) {
            return;
        }

        //true 表示当前动画未结束
        if (scroller.computeScrollOffset()) {
            mRect.top = scroller.getCurrY();
            mRect.bottom = mRect.top + (int) (mViewHeigh / scale);
            invalidate();
        }


    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //交给手势处理
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
















