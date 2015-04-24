package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 23/04/15.
 * Contains all strings used as paths when communicating with phone.
 */
public enum BTCommType {
    GET_CONTACTS("GET_CONTACTS"), GET_DRAWINGS("GET_DRAWINGS"), GET_USERNAME("GET_USERNAME"),
    SEND_DRAWING("SEND_DRAWINGS"), SEND_TO_CONTACT("SEND_TO"), GET_EMOJIS("GET_EMOJIS"),
    SEND_EMOJI("SEND_EMOJI");

    private final String type;

    BTCommType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
