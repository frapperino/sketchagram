package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public class User extends ADigitalPerson {
    String password = "password";   //TODO: replace with real password.
    boolean requireLogin = true;

    public User(String name, String username, Profile profile) {
        super(name, username, profile);
    }
}
