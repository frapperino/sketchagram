package sketchagram.chalmers.com.model;

/**
 * Created by Olliver on 2015-04-01.
 */
public enum DrawMotionEvents {
    ACTION_DOWN("ACTION_DOWN"), ACTION_MOVE("ACTION_MOVE"), ACTION_UP("ACTION_UP");

    private final String type;

    DrawMotionEvents(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
