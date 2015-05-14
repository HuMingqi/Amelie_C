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
import android.widget.ProgressBar;

import com.diandian.coolco.emilie.utility.Dimension;

import java.util.ArrayList;
import java.util.List;

public class BoxView extends View {

    private final static int STROKE_WIDTH_IN_DP = 3;
    private final static int EDGE_LEN_IN_DP = 18;
    private final static int TEXT_SIZE_IN_SP = 14;

    private Rect previewRect;
    private Rect boxRect;
    private Paint shadowPaint;
    private Paint clearPaint;
    private Paint pathPaint;
    private Path cornerPath;
    private List<Float> pts;
    private float halfStrokeWidth;
    private float[] pts2;
    private Paint thinLinePaint;
    private Paint textPaint;
    private Rect textRect;
    private String text;
    private float textWidth;
    private float textHeight;
    private float strokWidth;

    public BoxView(Context context) {
        super(context);
        init();
    }

    public BoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.parseColor("#66000000"));
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        strokWidth = Dimension.dp2px(getContext(), STROKE_WIDTH_IN_DP);
        halfStrokeWidth = strokWidth * 0.5f;

        pathPaint = new Paint();
//        pathPaint.setColor(Color.parseColor("#ccff00ff"));
        pathPaint.setColor(Color.parseColor("#cce7e7e7"));
        pathPaint.setStrokeWidth(strokWidth);

        thinLinePaint = new Paint();
        thinLinePaint.setColor(Color.parseColor("#88e7e7e7"));
        thinLinePaint.setStrokeWidth(halfStrokeWidth);

        textPaint = new Paint();
//        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Dimension.sp2px(getContext(), TEXT_SIZE_IN_SP));
        textPaint.setColor(Color.parseColor("#e7e7e7"));

        text = "请将衣服放入框内";
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawRect(boxRect, clearPaint);
        canvas.save();
        canvas.clipRect(boxRect, Region.Op.DIFFERENCE);
        canvas.drawRect(previewRect, shadowPaint);
        canvas.restore();
//        canvas.drawPath(cornerPath, pathPaint);
//        canvas.drawLines(((float[]) pts.toArray(new Float[0])), pathPaint);
        for (int i = 0; i < pts.size(); i += 4) {
            canvas.drawLine(pts.get(i), pts.get(i + 1), pts.get(i + 2), pts.get(i + 3), pathPaint);
        }
        canvas.drawLines(pts2, thinLinePaint);
        canvas.drawText(text, textRect.centerX()-textWidth*0.5f, textRect.top+textHeight, textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        previewRect = new Rect(0, 0, w, h);
        h = (int) (h - Dimension.dp2px(getContext(), 50));
        boxRect = new Rect(w / 4, h / 3, w * 3 / 4, h * 2 / 3);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        textWidth = textPaint.measureText(text);
        textHeight = textBounds.height();

        textRect = new Rect(0, ((int) (h * 2 / 3 + Dimension.dp2px(getContext(), 8))), w, h);

        int len = (int) Dimension.dp2px(getContext(), EDGE_LEN_IN_DP);
        cornerPath = new Path();

        cornerPath.lineTo(len, len);
//        cornerPath.moveTo(boxRect.left, boxRect.top);
//        cornerPath.lineTo(boxRect.left + len, boxRect.top);
//        cornerPath.moveTo(boxRect.right - len, boxRect.top);
//        cornerPath.lineTo(boxRect.right, boxRect.top);
//
//        cornerPath.moveTo(boxRect.right, boxRect.top);
//        cornerPath.lineTo(boxRect.right, boxRect.top + len);
//        cornerPath.moveTo(boxRect.right, boxRect.bottom - len);
//        cornerPath.lineTo(boxRect.right, boxRect.bottom);
//
//        cornerPath.moveTo(boxRect.right, boxRect.bottom);
//        cornerPath.lineTo(boxRect.right - len, boxRect.bottom);
//        cornerPath.moveTo(boxRect.left + len, boxRect.bottom);
//        cornerPath.lineTo(boxRect.left, boxRect.bottom);
//
//        cornerPath.moveTo(boxRect.left, boxRect.bottom);
//        cornerPath.lineTo(boxRect.left, boxRect.bottom - len);
//        cornerPath.moveTo(boxRect.left, boxRect.top - len);
//        cornerPath.lineTo(boxRect.left, boxRect.top);
//
//        cornerPath.close();
        pts = new ArrayList<>();

//        pts2 = new float[32];
        float offsetB = this.halfStrokeWidth * 0.5f;
        float offsetA = strokWidth * 0.75f;
        pts.add((float) boxRect.left - offsetA);
        pts.add((float) boxRect.top - offsetB);
        pts.add((float) (boxRect.left + len));
        pts.add((float) boxRect.top - offsetB);

        pts.add((float) (boxRect.right - len));
        pts.add((float) boxRect.top - offsetB);
        pts.add((float) boxRect.right + offsetA);
        pts.add((float) boxRect.top - offsetB);

        pts.add((float) boxRect.right+ offsetB);
        pts.add((float) boxRect.top - offsetA);
        pts.add((float) boxRect.right+ offsetB);
        pts.add((float) (boxRect.top + len));

        pts.add((float) boxRect.right + offsetB);
        pts.add((float) (boxRect.bottom - len));
        pts.add((float) boxRect.right + offsetB);
        pts.add((float) boxRect.bottom + offsetA);

        pts.add((float) boxRect.right + offsetA);
        pts.add((float) boxRect.bottom + offsetB);
        pts.add((float) (boxRect.right - len));
        pts.add((float) boxRect.bottom + offsetB);

        pts.add((float) (boxRect.left + len));
        pts.add((float) boxRect.bottom + offsetB);
        pts.add((float) boxRect.left - offsetA);
        pts.add((float) boxRect.bottom + offsetB);

        pts.add((float) boxRect.left - offsetB);
        pts.add((float) boxRect.bottom + offsetA);
        pts.add((float) boxRect.left - offsetB);
        pts.add((float) (boxRect.bottom - len));

        pts.add((float) boxRect.left - offsetB);
        pts.add((float) (boxRect.top + len));
        pts.add((float) boxRect.left - offsetB);
        pts.add((float) boxRect.top - offsetA);

        pts2 = new float[16];
        pts2[0] = boxRect.left+len;
        pts2[1] = boxRect.top;
        pts2[2] = boxRect.right-len;
        pts2[3] = boxRect.top;

        pts2[4] = boxRect.right;
        pts2[5] = boxRect.top+len;
        pts2[6] = boxRect.right;
        pts2[7] = boxRect.bottom-len;

        pts2[8] = boxRect.right-len;
        pts2[9] = boxRect.bottom;
        pts2[10] = boxRect.left+len;
        pts2[11] = boxRect.bottom;

        pts2[12] = boxRect.left;
        pts2[13] = boxRect.bottom-len;
        pts2[14] = boxRect.left;
        pts2[15] = boxRect.top+len;

    }
}
