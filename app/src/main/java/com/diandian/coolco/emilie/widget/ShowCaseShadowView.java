package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.diandian.coolco.emilie.R;

public class ShowCaseShadowView extends View{

    private Paint clearPaint;
    private Rect emptyRect;
    private Rect entireRect;
    private Paint shadowPaint;
    private Region shadowRegion;
    private int shadowColor;

    public ShowCaseShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ShowCaseShadowView(Context context) {
        super(context);
        init();
    }

    public ShowCaseShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        shadowColor = getResources().getColor(R.color.shadow);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (emptyRect != null) {
            canvas.clipRect(emptyRect, Region.Op.DIFFERENCE);
            canvas.drawColor(shadowColor);
        }
//        canvas.restore();

//        canvas.drawColor());
//        canvas.drawRect(entireRect, shadowPaint);
//        canvas.drawRect(emptyRect, shadowPaint);
//        super.dispatchDraw(canvas);
    }

    public void setEmptyRect(Rect emptyRect) {
        this.emptyRect = emptyRect;
        invalidate();
    }

    /*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    public void init(){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        entireRect = new Rect(0, 0, width, height);
        int emptyBoxWidth = width/2;
        int emptyBoxHeight = height/2;
        emptyRect = new Rect(0, 0, emptyBoxWidth, emptyBoxHeight);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.parseColor("#80000000"));

        shadowRegion = new Region(entireRect);
        shadowRegion.op(emptyRect, Region.Op.DIFFERENCE);
    }

*/

}
