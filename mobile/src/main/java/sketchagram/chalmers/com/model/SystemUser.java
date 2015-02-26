package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance;
    private User user;

    public static void initInstance() {
        ourInstance = new SystemUser();
    }

    public static SystemUser getInstance() {
        return ourInstance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
