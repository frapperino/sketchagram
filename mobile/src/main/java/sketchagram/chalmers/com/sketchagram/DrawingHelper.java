package sketchagram.chalmers.com.sketchagram;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;

import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.DrawingEvent;

/**
 * Helpclass for drawing. Tracking time passed since last action on drawing.
 * If the maximum awaited time has passed, an action is made.
 * Created by Alexander on 2015-03-27.
 */
public class DrawingHelper extends Observable{
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

    public DrawingHelper(Drawing drawing) {
        handler = new Handler();
        isRunning = false;
        this.drawing = drawing;
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
                                    Toast.makeText(MyApplication.getContext(), "Sent drawing.", Toast.LENGTH_SHORT).show();
                                    setChanged();
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

    public List<DrawingEvent> getMotions() {
        return drawing.getMotions();
    }

    public Drawing getDrawing() {
        return drawing;
    }
}
