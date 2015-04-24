package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 23/04/15.
 * Contains all strings used as paths when communicating with phone.
 */
public enum BTCommType {
    GET_CONTACTS("contacts"), GET_DRAWINGS("drawings"), GET_USERNAME("username"),
    SEND_DRAWING("drawing"), SEND_CONTACT("messageTo");

    private final String type;

    BTCommType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
