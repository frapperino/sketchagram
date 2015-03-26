package sketchagram.chalmers.com.model;
import sketchagram.chalmers.com.network.*;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance;
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

    public boolean login(String userName, String password) {
        if(this.connection.login(userName,password)){
            setUser(new User(userName, new Profile()));
            return true;
        }
        return false;
    }

    public Exception createAccount(String userName, String password) {
        return this.connection.createAccount(userName, password);
    }

    public void logout(){
        this.connection.logout();
    }
}
