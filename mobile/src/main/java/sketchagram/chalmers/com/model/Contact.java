package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public class Contact extends ADigitalPerson {
    private boolean favorite;

    protected Contact(String name, String username, Profile profile) {
        super(name, username, profile);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
