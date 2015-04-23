package sketchagram.chalmers.com.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events = new LinkedList<>();
    //Color used in the drawing. TODO: Change from static to dynamic.
    private final int COLOR = Color.MAGENTA;

    private byte[] staticDrawing;

    public Drawing() {
        events = new LinkedList<>();
    }

    public Drawing(DataMap data) {
        events = new LinkedList<>();

        float[] yf = data.getFloatArray("y-coordinates");
        float[] xf = data.getFloatArray("x-coordinates");
        long[] times = data.getLongArray("drawing-times");
        String[] actions = data.getStringArray("actions");
        staticDrawing = data.getByteArray("staticDrawing");

        for(int i = 0; i < times.length; i++)
            events.add(new DrawingEvent(times[i], xf[i], yf[i], DrawMotionEvents.valueOf(actions[i])));
    }

    public int getCOLOR() {
        return COLOR;
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

    @Override
    public String toString(){
        return "Drawing";
    }

    public float[] getX() {
        List<Float> fs = new ArrayList<>();
        float[] xf;
        for(DrawingEvent event : events) {
            fs.add(event.getX());
        }

        xf = new float[fs.size()];
        int i = 0;
        for(float f : fs) {
            xf[i] = f;
        }
        return xf;
    }

    public float[] getY() {
        List<Float> fs = new ArrayList<>();
        float[] yf;
        for(DrawingEvent event : events) {
            fs.add(event.getY());
        }

        yf = new float[fs.size()];
        int i = 0;
        for(float f : fs) {
            yf[i] = f;
        }
        return yf;
    }

    public long[] getTimes() {
        long[] times = new long[events.size()];
        int i = 0;
        for(DrawingEvent event : events) {
            times[i] = event.getTime();
            i++;
        }
        return times;
    }

    public String[] getActions() {
        String[] actions = new String[events.size()];
        int i = 0;
        for(DrawingEvent event : events) {
            actions[i] = event.getAction().name();
        }
        return actions;
    }

    public void setStaticDrawing(byte[] staticDrawing) {
        this.staticDrawing = staticDrawing;
    }

    /**
     * Get a static drawing, as small as possible, in the correct aspect ratio.
     * @param reqWidth minimum width required.
     * @param reqHeight minimum height required.
     * @return Bitmap to use. Null if no drawing exists.
     */
    public Bitmap getStaticDrawing(int reqWidth, int reqHeight) {
        if(staticDrawing == null) {
            return null;
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(staticDrawing, 0, staticDrawing.length, options);

        // Calculate inSampleSize, i.e. aspect ratio for smaller size but with correct ratio.
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(staticDrawing, 0, staticDrawing.length, options);
    }

    /**
     * Calculate aspect ratio to allow shrinking of image size.
     * @param options Original options of Bitmap
     * @param reqWidth minimum width
     * @param reqHeight minimum height
     * @return inSampleSize
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public byte[] getStaticDrawingByteArray() {
        return staticDrawing;
    }
}
