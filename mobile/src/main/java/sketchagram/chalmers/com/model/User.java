package sketchagram.chalmers.com.model;

import android.os.Handler;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class User extends ADigitalPerson  {
    private String password = "password";   //TODO: replace with real password.
    private boolean requireLogin = true;
    private List<Conversation> conversationList;
    private List<Contact> contactList;


    public User(String username, Profile profile) {
        super(username, profile);
        conversationList = new ArrayList<Conversation>();
        contactList = new ArrayList<Contact>();
    }

    public void addContact(Contact contact){
        if(contact == null) {
            throw new IllegalArgumentException("contact is invalid!");
        }
        contactList.add(contact);
    }

    public void addConversation(Conversation conversation){
        boolean exist = false;
        for(Conversation c : conversationList){
            if(c.getParticipants().equals(conversation.getParticipants())) {
                for (ClientMessage msg : conversation.getHistory())
                    c.addMessage(msg);
                exist = true;
            }

        }
        if(!exist)
            conversationList.add(conversation);
        setChanged();
        updateObservers();
    }

    /**
     * Gets contacts from database which is synced with server.
     * @return the contactlist
     */
    public List<Contact> getContactList() {
        //TODO: get contacts from database instead of server
        return SystemUser.getInstance().getConnection().getContacts();
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    @Override
    public String toString() {
        return getUsername();
    }

    public void addContact(String userName){
        try {
            SystemUser.getInstance().getConnection().addContact(userName);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the specified message
     * @param clientMessage The message to be sent contains receivers
     * @param type The type of the message
     */
    public void sendMessage(ClientMessage clientMessage, MessageType type){
        List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
        boolean exist = false;
        Conversation conversation = null;
        conversation = conversationExists(clientMessage.getReceivers());
        if(conversation == null){
            List<ADigitalPerson> otherParticipants = new ArrayList<>();
            otherParticipants.addAll(clientMessage.getReceivers());
            otherParticipants.add(clientMessage.getSender());
            //Remove yourself from participants
            otherParticipants.remove(this);
            conversation = new Conversation(otherParticipants);
            this.addConversation(conversation);
        }

        SystemUser.getInstance().getConnection().sendMessage(clientMessage);
        conversation.addMessage(clientMessage);

    }

    public void addMessage(ClientMessage clientMessage){
        List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
        boolean exist = false;
        Conversation conversation = null;
        conversation = conversationExists(clientMessage.getReceivers());
        if(conversation == null) {
            List<ADigitalPerson> otherParticipants = new ArrayList<>();
            otherParticipants.addAll(clientMessage.getReceivers());
            otherParticipants.add(clientMessage.getSender());
            //Remove yourself from participants
            otherParticipants.remove(this);
            conversation = new Conversation(otherParticipants);
            this.addConversation(conversation);
        }
        conversation.addMessage(clientMessage);
        updateObservers();

    }

    /**
     * Checks if the receiver list matches the specified conversation
     * @param receivers
     * @return
     */
    private Conversation conversationExists(List<ADigitalPerson> receivers){
        List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
        for(Conversation c : conversationList){
            boolean same = true;
            for(ADigitalPerson participant : c.getParticipants()) {
                for(ADigitalPerson receiver : receivers){
                    if(!participant.equals(receiver)){
                        same = false;
                    }
                }
            }
            if(same){
                return c;
            }
        }
        return null;
    }

    private void updateObservers(){
        Handler handler = new Handler(MyApplication.getContext().getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notifyObservers();
            }
        };
        handler.post(runnable);
    }

}
