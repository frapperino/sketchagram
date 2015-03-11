package sketchagram.chalmers.com.network;

import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.AMessage;

/**
 * Created by Olliver on 2015-03-11.
 */
public interface IConnection {
    public void init();
    public boolean login(String userName, String password);
    public void logout();
    public Exception createAccount(String userName, String password);
    public void createConversation(ADigitalPerson recipient);
    public void createGroupConversation(List<ADigitalPerson> recipients, String name);
    public void sendMessage(AMessage aMessage, String type);

}
