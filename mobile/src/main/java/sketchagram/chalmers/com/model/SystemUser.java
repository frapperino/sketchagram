package sketchagram.chalmers.com.model;
import android.net.Network;

import sketchagram.chalmers.com.network.*;
import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance;
    private User user;

    public static SystemUser getInstance() {
        return ourInstance;
    }

    private SystemUser() {

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

    public boolean login(String userName, String password) {
        if(Connection.getInstance().login(userName,password)){
            for ( Contact user : Connection.getInstance().getContacts()){
                boolean exists = false;
                for(Contact contact : MyApplication.getInstance().getDatabase().getAllContacts()){
                    if(contact.getUsername().equals(user.getUsername())){
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    MyApplication.getInstance().getDatabase().insertContact(user);
                }
            }
            setUser(new User(userName, new Profile()));

            return true;
        }
        return false;
    }

    public void createAccount(String userName, String password) throws NetworkException.UsernameAlreadyTakenException {
        Connection.getInstance().createAccount(userName, password);
    }

    public void logout(){
        Connection.getInstance().logout();
    }
}
