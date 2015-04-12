package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public class Contact extends ADigitalPerson {
    private boolean favorite;
    private Status status;

    public Contact(String username, Profile profile) {
        super(username, profile);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setStatus(Status status){ this.status = status;}
    public Status getStatus(){return status;}

    @Override
    public String toString(){
        return super.getUsername();
    }

}
