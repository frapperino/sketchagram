package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class AMessage {
    private final long TIMESTAMP;
    private final ADigitalPerson SENDER;
    private final Set<ADigitalPerson> RECEIVER = new HashSet<>();

    protected AMessage(long timestamp, ADigitalPerson sender, Set<ADigitalPerson> receiver) {
        this.TIMESTAMP = timestamp;
        this.SENDER = sender;
        this.RECEIVER.addAll(receiver);
    }

    public abstract  <T> T getMessage();

    public ADigitalPerson getSENDER(){
        return SENDER;
    }
    public Set<ADigitalPerson> getRECEIVER(){return RECEIVER;}

}
