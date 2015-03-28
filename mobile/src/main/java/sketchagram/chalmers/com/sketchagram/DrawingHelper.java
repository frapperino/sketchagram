package sketchagram.chalmers.com.sketchagram;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;

import sketchagram.chalmers.com.model.Drawing;

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
                                    Toast toast;
                                    toast = Toast.makeText(MyApplication.getContext(), "RESETTING DRAWING!", Toast.LENGTH_SHORT);
                                    toast.show();
                                    setChanged();
                                    notifyObservers();
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

    public void addMotion(MotionEvent event) {
        drawing.addMotion(event);
    }

    public List<MotionEvent> getMotions() {
        return drawing.getMotions();
    }

    public Drawing getDrawing() {
        return drawing;
    }
}
