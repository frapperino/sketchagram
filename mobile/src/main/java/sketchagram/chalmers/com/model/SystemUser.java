package sketchagram.chalmers.com.model;

import android.content.Context;

/**
 * Created by Bosch on 20/02/15.
 */
public class SystemUser {
    private static SystemUser ourInstance = new SystemUser();
    public static DatabaseOperation databaseOperation;

    private User user;

    public static SystemUser getInstance() {
        return ourInstance;
    }

    private SystemUser() {
    }

    public void initiateDatabase(Context context) {
        databaseOperation = new DatabaseOperation(context);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
