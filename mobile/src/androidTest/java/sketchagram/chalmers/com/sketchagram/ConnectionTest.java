package sketchagram.chalmers.com.sketchagram;

import android.test.AndroidTestCase;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.User;
import sketchagram.chalmers.com.network.Connection;

/**
 * Created by Olliver on 2015-03-11.
 */
public class ConnectionTest extends AndroidTestCase {
    protected void setUp() throws Exception {
        super.setUp();
        SystemUser.initInstance();
    }

    public void chatTest(){
        Connection conn1 = new Connection();
        Connection conn2 = new Connection();
        conn1.init();
        conn2.init();
        conn1.login("asdasd", "asdasd");
        conn2.login("olliver", "olliver");
        Contact contact = new Contact("olliver", new Profile());
        conn1.createConversation(contact);
    }
}
