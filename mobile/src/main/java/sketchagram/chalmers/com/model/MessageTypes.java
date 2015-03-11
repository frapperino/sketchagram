package sketchagram.chalmers.com.model;

/**
 * Created by Olliver on 15-03-11.
 */
public enum MessageTypes {
    TEXTMESSAGE("TEXTMESSAGE"), PICTURE("PICTURE"), EMOTICON("EMOTICON"), PAINTING("PAINTING");

    private final String type;

    MessageTypes(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
