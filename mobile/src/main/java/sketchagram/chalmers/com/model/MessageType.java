package sketchagram.chalmers.com.model;

/**
 * Created by Olliver on 15-03-11.
 */
public enum MessageType {
    TEXTMESSAGE("TEXTMESSAGE"), PICTURE("PICTURE"), EMOTICON("EMOTICON"), PAINTING("PAINTING");

    private final String type;

    MessageType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
