package sketchagram.chalmers.com.model;

import android.os.Handler;

import com.google.gson.Gson;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
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
                for (AMessage msg : conversation.getHistory())
                    c.addMessage(msg);
                exist = true;
            }

        }
        if(!exist)
            conversationList.add(conversation);
        setChanged();
        Handler handler = new Handler(MyApplication.getContext().getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notifyObservers();
            }
        };
        handler.post(runnable);
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

    public void sendMessage(AMessage aMessage, MessageTypes type){
        List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
        boolean exist = false;
        Conversation conversation = null;
        for(Conversation c : conversationList){
            if(c.getParticipants().equals(aMessage.getRECEIVER())){
                exist = true;
                conversation = c;
            }
        }
        if(!exist) {
            conversation = new Conversation(aMessage.getRECEIVER());
            conversationList.add(conversation);
        }
        SystemUser.getInstance().getConnection().sendMessage(aMessage, type);
        conversation.addMessage(aMessage);

    }

}
