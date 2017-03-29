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


public class PaintView extends View {
    private Path mDrawPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor = getResources().getColor(R.color.paint_black);
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;
    private float mBrushSize = DrawingActivity.MEDIUM_BRUSH;
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
        mDrawPath = new Path();
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                break;
            default:
                return false;

        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        mPaintColor = Color.parseColor(newColor);
        if (!isErased) {
            mDrawPath.reset();
            mDrawPaint.setColor(mPaintColor);
        }
        invalidate();
    }

    public void setBrushSize(float brushSize) {
        this.mBrushSize = brushSize;
        mDrawPath.reset();
        mDrawPaint.setStrokeWidth(this.mBrushSize);
        mDrawPaint.setColor(mPaintColor);
        isErased = false;
        invalidate();
    }

    public void setErased(float eraseSize) {
        mDrawPath.reset();
        isErased = true;
        mDrawPaint.setColor(Color.WHITE);
        mDrawPaint.setStrokeWidth(eraseSize);
        invalidate();
    }

    public void newPage() {
        mDrawCanvas.drawColor(Color.WHITE);
        mDrawPaint.setColor(Color.WHITE);
        invalidate();
        mDrawPath.reset();
        mDrawPaint.setColor(mPaintColor);
        isErased = false;
        mDrawPaint.setStrokeWidth(mBrushSize);
        invalidate();
    }
}
