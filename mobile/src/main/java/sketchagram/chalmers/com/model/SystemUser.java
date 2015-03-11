package sketchagram.chalmers.com.model;
import sketchagram.chalmers.com.network.*;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance = new SystemUser();
    private User user;
    private IConnection connection = new Connection();

    public static SystemUser getInstance() {
        return ourInstance;
    }

    private SystemUser() {
        this.connection.init();
    }

    public static void initInstance(){
        ourInstance = new SystemUser();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public IConnection getConnection() {return connection;}
    public void newConnection() {
        this.connection = new Connection();
        this.connection.init();
    }
}
