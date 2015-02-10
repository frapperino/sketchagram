package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class ADigitalPerson {
    private final String name;
    private final String username;
    private final Profile profile;

    protected ADigitalPerson(String name, String username, Profile profile) {
        this.name = name;
        this.username = username;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
