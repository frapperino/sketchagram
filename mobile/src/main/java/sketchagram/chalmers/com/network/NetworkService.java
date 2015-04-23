package sketchagram.chalmers.com.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Olliver on 2015-04-08.
 */
public class NetworkService extends Service {

    private Connection connection;


    @Override
    public void onCreate(){
        Log.d("SERVICE", "SERVICE CREATED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        connection = Connection.getInstance();
        Log.d("SERVICE", "SERVICE STARTED");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connection = null;
        Log.d("SERVICE", "SERVICE DESTROYED");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
