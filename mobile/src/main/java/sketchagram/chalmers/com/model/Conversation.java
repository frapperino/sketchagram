package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<ClientMessage> history;
    private List<ADigitalPerson> otherParticipants;
    private int conversationId;

    public Conversation(List<ADigitalPerson> participants, int conversationId){
        history = new ArrayList<ClientMessage>();
        this.otherParticipants = participants;
        this.conversationId = conversationId;
    }
    public Conversation(List<ADigitalPerson> participants, List<ClientMessage> oldMessages, int conversationId){
        history = oldMessages;
        this.otherParticipants = participants;
        this.conversationId = conversationId;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public int getConversationId(){ return  conversationId; }

    public List<ADigitalPerson> getParticipants() {
        return otherParticipants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    @Override
    public String toString(){
        getParticipants().remove(SystemUser.getInstance().getUser());// to not display user
        String participants = getParticipants().toString();
        participants = participants.substring(1, participants.length()-1); //Remove [].
        getParticipants().add(SystemUser.getInstance().getUser());//to keep list consistent
        return participants;
    }
}
