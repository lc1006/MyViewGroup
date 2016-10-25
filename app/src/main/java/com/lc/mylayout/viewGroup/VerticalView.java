package com.lc.mylayout.viewGroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lc on 16-10-21.
 */

public class VerticalView extends ViewGroup {

    public VerticalView(Context context) {
        super(context);
    }

    public VerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        if (p instanceof MarginLayoutParams)
            return p;
        else
            return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        //不要写成注释那样，否则一些属性得不到
       // return new MarginLayoutParams(super.generateLayoutParams(attrs));
        return new MarginLayoutParams(getContext(), attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0, width = 0;
        int maxChildHeight = 0;//当前行控件的最大高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int childCount = getChildCount();
        View childView;
        int rowWidth = 0;//当前行的宽度
        if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.AT_MOST) {
            for (int i = 0; i < childCount; i++) {
                childView = getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //   Log.d("verticalView","measure initial width="+childView.getMeasuredWidth()+"  ,initheight="+childView.getMeasuredHeight());
                //同样，不要写成new MarginLayoutParams marginLayoutParams = new MarginLayoutParams(childView.getLayoutParams());
                //否则，无法获取对应的margin值
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                //   Log.d("verticalView","marginTop = "+marginLayoutParams.topMargin+"  ,bottomMargin="+marginLayoutParams.bottomMargin);
                //不要写成childView.getHeight(),注意两者区别
                int childHeight = childView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                int childWidth = childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                  //   Log.d("verticalView","child["+i+"] rowWidth= "+rowWidth+" ,childWidth="+childWidth+" ,parentWidth="+parentWidth);
                if (rowWidth + childWidth > parentWidth) {//宽度占满父布局,换行
                    width = Math.max(width, Math.max(rowWidth, childWidth));
                    height += maxChildHeight;//换行后重新计算高度和宽度
                    //    Log.d("verticalView","child["+i+"] height = "+height+"换行");
                    maxChildHeight = childHeight;
                    rowWidth = childWidth;
                } else {
                    rowWidth += childWidth;
                    maxChildHeight = Math.max(maxChildHeight, childHeight);
                       // Log.d("verticalView","child["+i+"]max height = "+maxChildHeight+" ,childHeight="+childHeight);
                }
            }
            height += maxChildHeight;//最后一行的最大高度必须加上
            width = (width == 0) ? rowWidth : width;
           // Log.d("verticalView", "measure width=" + width + "  ,height=" + height);
            setMeasuredDimension(width, height);
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            //   Log.d("verticalView", "measure parentWidth=" + parentWidth + "  ,parentHeight=" + parentHeight);
            setMeasuredDimension(parentWidth, parentHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
      //  Log.d("verticalView", "origin layout l=" + l + " ,t=" + t + " ,r=" + r + " ,b=" + b);
        int parentWidth = getWidth();
        int childCount = getChildCount();
        int lineWidth = 0;
        b-=t;//critical 子控件的位置都是相对父布局设置的，所以这里必须修改
        int bottomHeight = b;//当前行最低部坐标值
        int lineHeight = 0;//当前行的最大高度,由于从底部开始，所以坐标值越小越高
        View childView;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            //   Log.d("verticalView","childView["+i+"] height ="+chileView.getMeasuredHeight()+" ,width="+chileView.getMeasuredWidth());
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
           // MarginLayoutParams marginLayoutParams = new MarginLayoutParams(chileView.getLayoutParams());
            if (i == 0) {//最底层布局
                l = marginLayoutParams.leftMargin;
                b = b - marginLayoutParams.bottomMargin ;
                t = b - childView.getMeasuredHeight();
                r = l + childView.getMeasuredWidth();
                lineWidth = r + marginLayoutParams.rightMargin;
                lineHeight = t - marginLayoutParams.topMargin;
               // Log.d("verticalView","layout["+i+"] lineWidth="+lineWidth+"  ,lineHeight="+lineHeight);
            } else {

                if (lineWidth + marginLayoutParams.leftMargin + childView.getMeasuredWidth() + marginLayoutParams.rightMargin > parentWidth) {
                    l = marginLayoutParams.leftMargin;
                    b = lineHeight - marginLayoutParams.bottomMargin;
                    r = l + childView.getMeasuredWidth();
                    t = b - childView.getMeasuredHeight();
                    bottomHeight = lineHeight;
                    lineHeight = t - marginLayoutParams.topMargin;
                    lineWidth = r + marginLayoutParams.rightMargin;
                 //   Log.d("verticalView","layout["+i+"] lineWidth="+lineWidth+"  ,lineHeight="+lineHeight);
                } else {
                    l = lineWidth + marginLayoutParams.leftMargin;
                    b = bottomHeight - marginLayoutParams.bottomMargin;
                    r = l + childView.getMeasuredWidth();
                    t = b - childView.getMeasuredHeight();
                    lineHeight = Math.min(lineHeight, t - marginLayoutParams.topMargin);
                    //这里要小心，不要写成lineWidth+ =r+marginLayoutParams.rightMargin,因为r中已经加过了
                    lineWidth=r+marginLayoutParams.rightMargin;
                  //  Log.d("verticalView","layout["+i+"] lineWidth="+lineWidth+"  ,lineHeight="+lineHeight);
                }
            }
            //     Log.d("verticalView","layout["+i+"] l="+l+"  ,t="+t +" ,r="+r+" ,b="+b);
            childView.layout(l, t, r, b);
        }
    }
}
