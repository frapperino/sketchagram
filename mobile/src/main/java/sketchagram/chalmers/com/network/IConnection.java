package sketchagram.chalmers.com.network;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.MessageType;

/**
 * Created by Olliver on 2015-03-11.
 */
public interface IConnection {
    public void init();
    public boolean login(String userName, String password);
    public void logout();
    public Exception createAccount(String userName, String password);
    public void createGroupConversation(Set<ADigitalPerson> recipients, String name);
    public void sendMessage(ClientMessage clientMessage);
    public void addContact(String userName)throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException;
    public List<Contact> getContacts();

}
