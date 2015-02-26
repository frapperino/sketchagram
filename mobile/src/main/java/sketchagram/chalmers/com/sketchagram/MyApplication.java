package sketchagram.chalmers.com.sketchagram;

import android.app.Application;

import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Alle on 2015-02-26.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        SystemUser.initInstance();
    }

    public void customAppMethod()
    {
        // Custom application method
    }
}
