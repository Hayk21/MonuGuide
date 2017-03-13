package blue_team.com.monuguide.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.DrawingActivity;

/**
 * Created by Hayk on 13.03.2017.
 */

public class PaintView extends View{
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = getResources().getColor(R.color.paint_black);
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize = DrawingActivity.MEDIUM_BRUSH;
    public static boolean isErased = false;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startDrawing();
    }

    private void startDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                break;
            default:
                return false;

        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        paintColor = Color.parseColor(newColor);
        if (!isErased) {
            drawPath.reset();
            drawPaint.setColor(paintColor);
        }
        invalidate();
    }

    public void setBrushSize(float brushSize) {
        this.brushSize = brushSize;
        drawPath.reset();
        drawPaint.setStrokeWidth(this.brushSize);
        drawPaint.setColor(paintColor);
        isErased = false;
        invalidate();
    }

    public void setErased(float eraseSize) {
        drawPath.reset();
        isErased = true;
        drawPaint.setColor(Color.WHITE);
        drawPaint.setStrokeWidth(eraseSize);
        invalidate();
    }

    public void newPage() {
        drawCanvas.drawColor(Color.WHITE);
        drawPaint.setColor(Color.WHITE);
        invalidate();
        drawPath.reset();
        drawPaint.setColor(paintColor);
        isErased = false;
        drawPaint.setStrokeWidth(brushSize);
        invalidate();
    }
}
