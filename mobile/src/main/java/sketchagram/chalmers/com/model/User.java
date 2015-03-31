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
        //TODO: get contacts from database instead of server
        contactList = SystemUser.getInstance().getConnection().getContacts();
    }

    /**
     * Adds a new conversation.
     *
     * @param conversation the conversation to be added.
     */
    public void addConversation(Conversation conversation){
        boolean exist = false;
        for(Conversation c : conversationList){
            if(c.getParticipants().equals(conversation.getParticipants())) {
                exist = true;
            }
        }
        if(!exist)
            conversationList.add(conversation);
        updateObservers(null);
    }

    /**
     * Gets contacts from database which is synced with server.
     * @return the contactlist
     */
    public List<Contact> getContactList() {
        return contactList;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    @Override
    public String toString() {
        return getUsername();
    }

    /**
     * Adds a contacts.
     *
     * @param userName contact to be added.
     */
    public boolean addContact(String userName){
        boolean success = false;
        try {
            success = SystemUser.getInstance().getConnection().addContact(userName);
            if(success) {
                contactList.add(new Contact(userName, new Profile()));
            }
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

    /**
     * Sends the specified message
     * @param clientMessage The message to be sent contains receivers
     */
    public void sendMessage(ClientMessage clientMessage){
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

    /**
     * Adds a message that was received from the server to the proper conversation.
     *
     * @param clientMessage The message received.
     */
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
        updateObservers(clientMessage);

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

    private void updateObservers(final ClientMessage message){
        setChanged();
        Handler handler = new Handler(MyApplication.getContext().getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notifyObservers(message);
            }
        };
        handler.post(runnable);
    }

}
