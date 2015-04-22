package sketchagram.chalmers.com.sketchagram;

import android.app.Application;
import sketchagram.chalmers.com.database.DBHelper;
import sketchagram.chalmers.com.database.SketchagramDb;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.internal.ge;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.User;
import sketchagram.chalmers.com.network.Connection;
import sketchagram.chalmers.com.network.NetworkException;
import sketchagram.chalmers.com.network.NetworkService;

/**
 * Created by Alexander Härenstam on 2015-02-26.
 */
public class MyApplication extends Application {
    private static MyApplication ourInstance;
    private static SketchagramDb db = null;
    private static Context context;
    private static String FIRST_STARTUP = "FIRST_STARTUP";
    private User user;

    public static MyApplication getInstance(){
        return ourInstance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        ourInstance = this;

        startService(new Intent(context, NetworkService.class));
        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    /**
     * Initiate singletons for whole application.
     */
    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        db = new SketchagramDb(getApplicationContext());
    }

    public SharedPreferences getSharedPreferences(){return getSharedPreferences("user", 0);}

    public SketchagramDb getDatabase(){
        if(db == null){
            db = new SketchagramDb(getApplicationContext());
        }
        return db;
    }

    public void customAppMethod()
    {
        // Custom application method
    }

    public static Context getContext(){
        return context;
    }

    public boolean login(String userName, String password) {
        if(Connection.getInstance().login(userName,password)){
            for ( Contact user : Connection.getInstance().getContacts()){
                boolean exists = false;
                for(Contact contact : MyApplication.getInstance().getDatabase().getAllContacts()){
                    if(contact.getUsername().equals(user.getUsername())){
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    MyApplication.getInstance().getDatabase().insertContact(user);
                }
            }
            user = new User(userName, new Profile());

            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public void createAccount(String userName, String password) throws NetworkException.UsernameAlreadyTakenException {
        Connection.getInstance().createAccount(userName, password);
    }

    public void logout(){
        Connection.getInstance().logout();
    }
}
