package com.supcon.mes.module_main.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author yangfei.cao
 * @ClassName depot
 * @date 2018/8/14
 * ------------- Description -------------
 */
public class PopwinBackView extends View {

    //初始化画笔,
    private Paint p;
    private Path path;

    public PopwinBackView(Context context) {
        super(context);
        initView();
    }

    public PopwinBackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopwinBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PopwinBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        p = new Paint();
        //设置画笔颜色
        p.setColor(getResources().getColor(com.supcon.mes.middleware.R.color.white));
        //初始化路径类，用于点与点之间的连线
        path = new Path();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理,具体读者可以自行查看
        int mWidth = 30;
        int mHeight = 25;

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //先画出一个小的三角形
        path.moveTo(15, 25);//x就是三角形顶点纵坐标
        //三角形右边的点,之所以加上20，是因为我们画的是三角形，右边的点的横坐标肯定大于顶点的横坐标
        path.lineTo(30, 0);
        //三角形左边的点,之所以减去20，是因为我们画的是三角形，左边的点的横坐标肯定小于顶点的横坐标
        path.lineTo(0, 0);
        //使这些点构成封闭的多边形
        path.close();
        //用画笔画出所连接的图形
        canvas.drawPath(path, p);
    }

}
