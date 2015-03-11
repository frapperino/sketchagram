package sketchagram.chalmers.com.network;

import com.google.gson.Gson;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.AMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.TextMessage;

/**
 * Created by Olliver on 15-03-06.
 */
public class NetworkMessage<T> {
    private String sender;
    private List<String> receivers;
    private T message;
    private long timestamp;

    public NetworkMessage(){

    }

    public NetworkMessage(long timestamp,String sender, List<String> receivers, T message){
        this.timestamp = timestamp;
        this.sender = sender;
        this.receivers = receivers;
        this.message = message;
    }

    public String getSender(){return this.sender;}
    public List<String> getReceivers(){return this.receivers;}
    public long getTimestamp(){return this.timestamp;}
    public T getMessage(){return this.message;}

    public void convertToNetworkMessage(AMessage aMessage){
        T m = aMessage.getMessage();
        String sender = aMessage.getSENDER().getUsername();
        List<String> receivers = new ArrayList<>();
        for(ADigitalPerson person : aMessage.getRECEIVER()){
            receivers.add(person.getUsername());
        }
        this.timestamp = System.currentTimeMillis();
        this.sender = sender;
        this.receivers = receivers;
        this.message = m;
    }

    public AMessage convertFromNetworkMessage(Message mess, Conversation c){
        List<String> receivers = getReceivers();
        List<ADigitalPerson> personReceivers = new ArrayList<>();
        for(String user : receivers){
            if(SystemUser.getInstance().getUser().getUsername().equals(user)){
                personReceivers.add(SystemUser.getInstance().getUser());
                break;
            }
            for(Contact contact : SystemUser.getInstance().getUser().getContactList()){
                if(user.equals(contact.getUsername())){
                    personReceivers.add(contact);
                    break;
                }
            }

        }
        List<ADigitalPerson> allUsers = new ArrayList<>();
        allUsers.addAll(SystemUser.getInstance().getUser().getContactList());
        allUsers.add(SystemUser.getInstance().getUser());
        ADigitalPerson sender = null;
        for(ADigitalPerson person : allUsers){
            if(person.getUsername().equals(getSender())){
                sender = person;
            }
        }
        switch (mess.getLanguage()){
            case "TextMessage":
                TextMessage tMessage = new TextMessage(getTimestamp(), sender, personReceivers);
                tMessage.setTextMessage((String)getMessage());
                return tMessage;

        }
        return null;
    }

}
