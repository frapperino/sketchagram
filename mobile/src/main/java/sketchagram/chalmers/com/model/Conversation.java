package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation implements Comparable{
    private List<ClientMessage> history;
    private List<ADigitalPerson> participants;
    private int conversationId;

    public Conversation(List<ADigitalPerson> participants, int conversationId){
        history = new ArrayList<>();
        this.participants = participants;
        this.conversationId = conversationId;
    }
    public Conversation(List<ADigitalPerson> participants, List<ClientMessage> oldMessages, int conversationId){
        history = oldMessages;
        this.participants = participants;
        this.conversationId = conversationId;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public int getConversationId(){ return  conversationId; }

    public List<ADigitalPerson> getParticipants() {
        return participants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    /**
     * Determines whether or not this conversation has unread messages.
     * @return true if there exists unread messages. false otherwise.
     */
    public boolean hasUnreadMessages() {
        for(ClientMessage c: history) {
            if(!c.isRead()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        String participants = "";
        for(ADigitalPerson participant : getParticipants()) {
            if(!participant.getUsername().toLowerCase().equals(UserManager.getInstance().getUsername().toLowerCase())) {
                participants = participant.getUsername();
            }
        }

        /*String participants = getParticipants().toString();
        participants = participants.substring(1, participants.length()-1); //Remove [].
        participants = participants.replace(",", " ");//To remove commas
        participants = participants.replace(MyApplication.getInstance().getUser().getUsername().toLowerCase(), ""); //To remove username of current user when printing
        participants = participants.replace(MyApplication.getInstance().getUser().getUsername(), ""); //To remove username of current user when printing*/

        return participants;
    }

    @Override
    public int compareTo(Object other) {
        if (this == other)  {
            return 0;
        }
        final List<ClientMessage> myHistory = this.getHistory();
        final List<ClientMessage> otherHistory = ((Conversation)other).getHistory();
        if(myHistory.isEmpty() && otherHistory.isEmpty()) {
            return 0;
        } else if(myHistory.isEmpty() && !otherHistory.isEmpty()) {
            return -1;
        } else if(!myHistory.isEmpty() && otherHistory.isEmpty()) {
            return 1;
        }
        final long myTimestamp = myHistory.get(myHistory.size()-1).getTimestamp();
        final long otherTimestamp = otherHistory.get(otherHistory.size()-1).getTimestamp();
        if(myTimestamp < otherTimestamp) {
            return 1;
        } else if(myTimestamp > otherTimestamp){
            return -1;
        } else {
            return 0;
        }
    }
}
