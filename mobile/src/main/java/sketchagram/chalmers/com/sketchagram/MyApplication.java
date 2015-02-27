package sketchagram.chalmers.com.sketchagram;

import android.app.Application;

import sketchagram.chalmers.com.database.DBHelper;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Alle on 2015-02-26.
 */
public class MyApplication extends Application {
    private static MyApplication ourInstance;

    public MyApplication getInstance(){
        return ourInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        ourInstance = this;

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        SystemUser.initInstance();
        DBHelper.initInstance(getApplicationContext());
    }

    public void customAppMethod()
    {
        // Custom application method
    }
}
