package sketchagram.chalmers.com.model;

/**
 * Created by Bosch on 10/02/15.
 */
public class Contact extends ADigitalPerson {
    private boolean favorite;
    private long lastAccessed;

    public Contact(String username, Profile profile) {

        super(username, profile);

    }
    public Contact (String username, Profile profile, long lastAccessed) {
        this(username, profile);
        this.lastAccessed = lastAccessed;
    }

    public long getLastAccessed(){ return this.lastAccessed; }

    public void setLastAccessed(long lastAccessed){ this.lastAccessed = lastAccessed; }

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

    public int compareTo(Object obj) {
        return (int)(this.lastAccessed - ((Contact)obj).getLastAccessed());
    }

}
