package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 02/04/15.
 */
public class DrawingHolder {

    private static DrawingHolder instance = null;
    private static Drawing drawing;

    protected DrawingHolder() {}

    public static DrawingHolder getInstance() {
        if(instance == null)
            instance = new DrawingHolder();

        return instance;

    }

    public Drawing getDrawing(){
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public void resetDrawing() {
        drawing = null;
    }

}
