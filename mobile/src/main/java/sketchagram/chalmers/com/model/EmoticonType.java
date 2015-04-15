package sketchagram.chalmers.com.model;

/**
 * Created by Olliver on 2015-04-14.
 */
public enum EmoticonType {
    SMILE("SMILE"), THUMBSUP("THUMBSUP"), THUMBSDOWN("THUMBSDOWN"); //TODO: add more emoticons.

    private final String type;

    EmoticonType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
