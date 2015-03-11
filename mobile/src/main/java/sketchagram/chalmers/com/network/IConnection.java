package sketchagram.chalmers.com.network;

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
    public void createConversation(ADigitalPerson recipient);;
    public void sendMessage(AMessage aMessage, String type);

}
