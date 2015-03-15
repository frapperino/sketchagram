package sketchagram.chalmers.com.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Olliver on 15-03-06.
 */
public class NetworkMessage<T> {
    private String sender;
    private List<String> receivers;
    private T content;
    private long timestamp;

    public NetworkMessage(){

    }

    public NetworkMessage(long timestamp,String sender, List<String> receivers, T content){
        this.timestamp = timestamp;
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
    }

    public String getSender(){return this.sender;}
    public List<String> getReceivers(){return this.receivers;}
    public long getTimestamp(){return this.timestamp;}
    public T getContent(){return this.content;}

    public void convertToNetworkMessage(ClientMessage clientMessage){

        T m = (T)clientMessage.getContent();
        String sender = clientMessage.getSender().getUsername();
        List<String> receivers = new ArrayList<>();
        for(Object person : clientMessage.getReceivers()){
            receivers.add(((ADigitalPerson) person).getUsername());
        }
        this.timestamp = System.currentTimeMillis();
        this.sender = sender;
        this.receivers = receivers;
        this.content = m;
    }

    public ClientMessage convertFromNetworkMessage(MessageType type){
        List<String> receivers = getReceivers();
        List<ADigitalPerson> personReceivers = new ArrayList<>();
        for(String user : receivers){
            for(Contact contact : SystemUser.getInstance().getUser().getContactList()){
                if(user.equals(contact.getUsername())){
                    personReceivers.add(contact);
                    break;
                }
            }

        }
        Set<ADigitalPerson> allUsers = new HashSet<>();
        allUsers.addAll(SystemUser.getInstance().getUser().getContactList());
        allUsers.add(SystemUser.getInstance().getUser());
        ADigitalPerson sender = null;
        for(ADigitalPerson person : allUsers){
            if(person.getUsername().equals(getSender())){
                sender = person;
            }
        }

        switch (type){
            case TEXTMESSAGE:
                ClientMessage<String> clientMessage = new ClientMessage<String>(getTimestamp(), sender, personReceivers, (String)getContent(), MessageType.TEXTMESSAGE);
                return clientMessage;

        }
        return null;
    }

}
