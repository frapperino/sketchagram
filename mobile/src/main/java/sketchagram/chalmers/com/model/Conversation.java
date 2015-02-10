package sketchagram.chalmers.com.model;

import java.util.List;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<AMessage> history;
    private List<ADigitalPerson> participants;

    public Conversation(){

    }

    public List<AMessage> getHistory() {
        return history;
    }

    public void setHistory(List<AMessage> history) {
        this.history = history;
    }

    public List<ADigitalPerson> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ADigitalPerson> participants) {
        this.participants = participants;
    }
}
