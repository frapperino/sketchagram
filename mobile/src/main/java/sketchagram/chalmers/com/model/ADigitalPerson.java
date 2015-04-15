package sketchagram.chalmers.com.model;

import java.util.Observable;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class ADigitalPerson extends Observable{
    private final String username;
    private Status status;
    private final Profile profile;

    public ADigitalPerson(){
        this.username = "";
        this.profile = new Profile();
    }

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

    public void setStatus(Status status){ this.status = status;}
    public Status getStatus(){return status;}

    @Override
    public boolean equals(Object obj){
        return this.username.equals(((ADigitalPerson)obj).getUsername());
    }
    @Override
    public int hashCode(){
        return (int) username.hashCode() * profile.hashCode() * 17;
    }

}
