package sketchagram.chalmers.com.sketchagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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

    private boolean isTouchable = true;

    //Tracking last time a drawing action was made.
    DrawingHelper helper;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        helper = new DrawingHelper();
    }

    public void setTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
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
        drawPaint.setStrokeWidth(10);
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
        if(isTouchable) {
            helper.startMeasuring();
            helper.setAccessed();
            DrawingEvent drawingEvent = null;
            WindowManager wm = (WindowManager) findViewById(R.id.drawing).getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    drawingEvent = new DrawingEvent(System.nanoTime(), event.getX()/size.x, event.getY()/size.y, DrawMotionEvents.ACTION_DOWN);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawingEvent = new DrawingEvent(System.nanoTime(), event.getX()/size.x, event.getY()/size.y, DrawMotionEvents.ACTION_MOVE);
                    break;
                case MotionEvent.ACTION_UP:
                    drawingEvent = new DrawingEvent(System.nanoTime(), event.getX()/size.x, event.getY()/size.y, DrawMotionEvents.ACTION_UP);
                    break;
            }
            if(drawingEvent != null) {
                helper.addMotion(drawingEvent);    //Must use a copy since android recycles.
                return handleMotionEvent(drawingEvent);
            }
            return false;
        } else {
            return super.onTouchEvent(event);   //does nothing.
        }
    }

    /**
     * Takes care of drawing.
     * @param event
     */
    public boolean handleMotionEvent(DrawingEvent event) {
        WindowManager wm = (WindowManager) findViewById(R.id.drawing).getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //Retrieve the X and Y positions of the user touch:
        float percentX = event.getX();
        float percentY = event.getY();
        float touchX = size.x*percentX;
        float touchY = size.y*percentY;

        //The MotionEvent parameter to the onTouchEvent method will
        // let us respond to particular touch events.
        // The actions we are interested in to implement drawing are down, move and up.
        switch (event.getAction()) {
            case ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case ACTION_UP:
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
    public void clearCanvas(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
        //TODO: Animations that will make graphics fade.
    }

    /**
     * Displays the provided drawing
     * drawn with the original time-delay between strokes.
     * @param drawing
     */
    public void displayDrawing(Drawing drawing) {
        CountdownTask task = new CountdownTask(drawing);
        task.execute();
    }

    /**
     * Instantly displays the drawing on canvas.
     * @param drawing
     * @deprecated No longer in any use.
     */
    public void displayStaticDrawing(Drawing drawing) {
        List<DrawingEvent> motions = drawing.getMotions();
        for(DrawingEvent e: motions) {
            handleMotionEvent(e);
        }
    }

    /**
     * Runs the DrawingEvent.
     */
    private class EventRunnable implements Runnable {
        private DrawingEvent event;
        public EventRunnable(DrawingEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            handleMotionEvent(event);
        }
    }

    /**
     * Uses a different thread to count time-deltas between user input, i.e. MotionEvents.
     * Allowing each motion to be drawn identical to the original.
     * Created by Alexander on 2015-03-28.
     */
    private class CountdownTask extends AsyncTask<Void, Void, Void> {
        private Drawing drawing;
        private Handler handler;

        public CountdownTask(Drawing drawing) {
            this.drawing = drawing;
            handler = new Handler();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DrawingEvent curr;
            long timeDeltaInMilli;
            List<DrawingEvent> events = drawing.getMotions();
            DrawingEvent first = events.get(0);

            handler.post(new EventRunnable(first));
            for (int i = 1; i < events.size(); i++) {
                curr = events.get(i);
                timeDeltaInMilli = ((curr.getTime() - first.getTime()) / 1000000);
                handler.postDelayed(new EventRunnable(curr), timeDeltaInMilli);
            }
            helper.notifyObservers();
            return null;
        }
    }

    /**
     * Return a copy of the image in form a bitmap.
     * @return the bitmap in question.
     */
    private byte[] getCanvasBitmapAsByte() {
        Bitmap bmp = canvasBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void addHelperObserver(Observer observer) {
        helper.addObserver(observer);
    }

    /**
     * Helpclass for drawing. Tracking time passed since last action on drawing.
     * If the maximum awaiting time has passed, an action is made.
     * Created by Alexander on 2015-03-27.
     */
    private class DrawingHelper extends Observable {
        private long lastActionTime;
        private Handler handler;
        private boolean isRunning;
        private Drawing drawing;

        //Max nano-time allowed while awaiting input.
        private static final long MAX_AWAIT_TIME = 2000000000;

        public DrawingHelper() {
            handler = new Handler();
            isRunning = false;
            drawing = new Drawing();
        }

        public void startMeasuring() {
            if(!isRunning) {
                isRunning = true;
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        while(true) {
                            if((System.nanoTime() - lastActionTime) >= MAX_AWAIT_TIME ) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setChanged();
                                        drawing.setStaticDrawing(getCanvasBitmapAsByte());
                                        notifyObservers(drawing);
                                    }
                                });
                                isRunning = false;
                                return null;
                            }
                        }
                    }
                };
                asyncTask.execute();
            }
        }

        public void setAccessed() {
            lastActionTime = System.nanoTime();
        }

        public void addMotion(DrawingEvent event) {
            drawing.addMotion(event);
        }

        public Drawing getDrawing() {
            return drawing;
        }
    }
}

