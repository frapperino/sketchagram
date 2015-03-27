package sketchagram.chalmers.com.sketchagram;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Defines a view which the user can draw upon using touch gestures.
 * Created by Alexander on 2015-03-25.
 *
 * See tutorial. http://code.tutsplus.com/series/create-a-drawing-app-on-android--cms-704
 *
 * Potential color pickers:
 * https://github.com/LarsWerkman/HoloColorPicker
 * https://code.google.com/p/color-picker-view/
 */
public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private static int BRUSH_COLOR = 0xff00304e;    //http://colrd.com/color/
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    //Tracking last time a drawing action was made.
    DrawingHelper helper;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    /**
     * Get drawing area setup for interaction
     */
    private void setupDrawing() {
        //First instantiate the drawing Path and Paint objects:
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(BRUSH_COLOR); //set the initial color

        //set the initial path properties:
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG); //Dithering set by parameter to constructor.
    }

    /**
     * View given size
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Instantiate the drawing canvas and bitmap using the width and height values
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    /**
     * Draw view
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //Draw the canvas and the drawing path
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    /**
     * Detect user touch.
     * When the user touches the View, we move to that position to start drawing.
     * When they move their finger on the View, we draw the path along with their touch.
     * When they lift their finger up off the View,
     * we draw the Path and reset it for the next drawing operation.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Retrieve the X and Y positions of the user touch:
        float touchX = event.getX();
        float touchY = event.getY();

        helper.startMeasuring();
        helper.setAccessed();

        //The MotionEvent parameter to the onTouchEvent method will
        // let us respond to particular touch events.
        // The actions we are interested in to implement drawing are down, move and up.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();   //Will cause the onDraw method to execute.
        return true;
    }

    /**
     * Start a new drawing.
     */
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    /**
     * Set the Drawing helper in the fragment that uses the view.
     */
    public void setHelper(DrawingHelper helper) {
        this.helper = helper;
    }
}
