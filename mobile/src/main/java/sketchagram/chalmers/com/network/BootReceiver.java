package sketchagram.chalmers.com.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Olliver on 2015-04-09.
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context, NetworkService.class));
            Log.d("RECEIVER", "SERVICE STARTED");
        }else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            context.stopService(new Intent(context, NetworkService.class));
        }
    }
}
