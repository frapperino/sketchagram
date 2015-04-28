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
import sketchagram.chalmers.com.model.UserManager;
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
    private boolean firstStart = true;

    public static MyApplication getInstance(){
        return ourInstance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        ourInstance = this;
        startNetworkService();
        if(Connection.isLoggedIn() && firstStart){  //TODO: Move logic into model.
            Connection.getInstance().logout();
        }
        firstStart = false;
        String userName = ourInstance.getSharedPreferences().getString("username", null);
        String password = ourInstance.getSharedPreferences().getString("password", null);
        if(userName != null && password != null) {
            UserManager.getInstance().login(userName, password);
        }
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

    public void stopNetworkService(){
        stopService(new Intent(context, NetworkService.class));
    }

    public void startNetworkService() {
        startService(new Intent(context, NetworkService.class));
    }

    public SharedPreferences getSharedPreferences(){return getSharedPreferences("user", 0);}

    public SketchagramDb getDatabase(){
        if(db == null){
            db = new SketchagramDb(getApplicationContext());
        }
        return db;
    }

    public static Context getContext(){
        return context;
    }
}
