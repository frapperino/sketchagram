package sketchagram.chalmers.com.model;


/**
 * Created by Olliver on 2015-04-14.
 */
public enum EmoticonType {
    SAD("SAD"), HAPPY("HAPPY"),
    FLIRT("FLIRT"), HEART("HEART"),
    THUMBSUP("THUMBSUP"), THUMBSDOWN("THUMBSDOWN");


    private final String type;

    EmoticonType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }

}
