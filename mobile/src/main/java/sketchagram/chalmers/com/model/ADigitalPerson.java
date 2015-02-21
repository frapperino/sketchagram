package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class ADigitalPerson {
    private final String username;
    private final Profile profile;

    protected ADigitalPerson(String username, Profile profile) {
        this.username = username;
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public Profile getProfile() {
        return profile;
    }
}
