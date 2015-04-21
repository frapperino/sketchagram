package sketchagram.chalmers.com.sketchagram;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events;

    public Drawing() {
        events = new LinkedList<>();
    }

    public Drawing(float[] xFloat, float[] yFloat, long[] longTimes, String[] actionsString) {

        events = new LinkedList<>();

        float[] yf = yFloat;
        float[] xf = xFloat;
        long[] times = longTimes;
        String[] actions = actionsString;

        int i = 0;
        for(float f : yf) {
            events.add(new DrawingEvent(times[i], xf[i], yf[i], DrawMotionEvents.valueOf(actions[i])));
        }

    }
    public List<DrawingEvent> getMotions() {
        return events;
    }


    /**
     * Adds a DrawingEvent to the queue by converting the MotionEvent.
     * @param event
     */
    public void addMotion(DrawingEvent event) {
        events.add(event);
    }


    public DataMap putToDataMap(DataMap data) {
        data.remove("y-coordinates");
        data.remove("x-coordinates");
        data.remove("drawing-times");
        data.remove("actions");

        long[] times = new long[events.size()];
        float[] xf = new float[events.size()];
        float[] yf = new float[events.size()];
        String[] actions = new String[events.size()];

        int i = 0;
        for(DrawingEvent de : events) {
            xf[i] = de.getX();
            yf[i] = de.getY();
            times[i] = de.getTime();
            actions[i] = de.getAction().toString();
            i++;
        }

        data.putFloatArray("x-coordinates", xf);
        data.putFloatArray("y-coordinates", yf);
        data.putLongArray("drawing-times", times);
        data.putStringArray("actions", actions);
        return data;
    }
}
