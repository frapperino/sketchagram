package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<ClientMessage> history;
    private Set<ADigitalPerson> otherParticipants;

    public Conversation(Set<ADigitalPerson> participants){
        history = new ArrayList<ClientMessage>();
        this.otherParticipants = participants;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public Set<ADigitalPerson> getParticipants() {
        return otherParticipants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    @Override
    public String toString(){
        return getParticipants().toString();
    }
}
