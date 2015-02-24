package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance = new SystemUser();
    private User user;

    public static SystemUser getInstance() {
        return ourInstance;
    }

    private SystemUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
