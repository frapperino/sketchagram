package sketchagram.chalmers.com.model;

import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class TextMessage extends AMessage {

    private String textMessage;

    public TextMessage(long timestamp, ADigitalPerson sender, Set<ADigitalPerson> receiver) {
        super(timestamp, sender, receiver);
        textMessage = "";
    }

    @Override
    public String getMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}
