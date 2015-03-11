package sketchagram.chalmers.com.sketchagram;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.internal.ge;

import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Alexander Härenstam on 2015-02-26.
 */
public class MyApplication extends Application {
    private static MyApplication ourInstance;
    private static Context context;

    public MyApplication getInstance(){
        return ourInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        ourInstance = this;

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
        SystemUser.initInstance();
    }

    public void customAppMethod()
    {
        // Custom application method
    }

    public static Context getContext(){
        return context;
    }
}
