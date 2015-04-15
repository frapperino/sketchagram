package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public class Contact extends ADigitalPerson {
    private boolean favorite;

    public Contact(String username, Profile profile) {
        super(username, profile);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    @Override
    public String toString(){
        Status status = super.getStatus();
        if(status == null) {
            return super.getUsername();
        } else {
            return super.getUsername() + "  " + status.toString();
        }
    }

}
