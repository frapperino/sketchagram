package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class ADigitalPerson {
    private final String NAME;
    private final String USERNAME;
    private final Profile PROFILE;

    protected ADigitalPerson(String name, String username, Profile profile) {
        this.NAME = name;
        this.USERNAME = username;
        this.PROFILE = profile;
    }

    public String getNAME() {
        return NAME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }
}
