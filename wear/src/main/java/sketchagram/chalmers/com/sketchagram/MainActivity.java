package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        View.OnClickListener,
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks,
        MessageFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, WearableListView.ClickListener {

    private ContactFragment contactFragment;
    private MessageFragment messageFragment;

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private final String TAG = "SKETCHAGRAM";
    public View btn;
    private Node peerNode;
    public static final String KEY_REPLY = "reply";
    private static final int SAMPLE_NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.messageButton);
        contactFragment = new ContactFragment();
        messageFragment = new MessageFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.wearable_fragment_layout, messageFragment);
        ft.commit();


        //  Is needed for communication between the wearable and the device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //What to do if a message is received
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "connected to Google Play Services on Wear!");
        Wearable.MessageApi.addListener(mGoogleApiClient, this).setResultCallback(resultCallback);
    }


    /**
     * Not needed, but here to show capabilities. This callback occurs after the MessageApi
     * listener is added to the Google API Client.
     */
    private ResultCallback<Status> resultCallback =  new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            Log.v(TAG, "Status: " + status.getStatus().isSuccess());
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    return null;
                }
            }.execute();
        }
    };



    public void sendMessage(View view){
        Log.e("WATCH", "Trying to send a message");
        Intent intent = new Intent(this, AdvancedListActivity.class);
        startActivity(intent);
    }

    /** Post a new or updated notification using the selected notification options. */
    private void updateNotification(int presetIndex) {
        NotificationPreset preset = NotificationPresets.PRESETS[presetIndex];
        Notification notif = preset.buildNotification(this);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(SAMPLE_NOTIFICATION_ID, notif);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onClick(View v) {
        Log.e("WATCH", "trying to send a message");
    }


}
