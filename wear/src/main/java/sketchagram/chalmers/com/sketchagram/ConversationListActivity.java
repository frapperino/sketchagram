package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 27/02/15.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends Activity implements WearableListView.ClickListener,
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks  {

    private GoogleApiClient mGoogleApiClient;
    private WearableListView mListView;
    private MyListAdapter mAdapter;
    private List<String> conversations;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_stub);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                conversations = new ArrayList<>();
                messagePhone("contacts", null);
                loadAdapter();

            }
        });


        //  Is needed for communication between the wearable and the device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d("WATCH", "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void loadAdapter(){
        Log.e("ADAPTER", conversations.toString());
        mAdapter = new MyListAdapter(this, conversations);
        mListView.setAdapter(mAdapter);
        mListView.setClickListener(ConversationListActivity.this);
    }



    /**
     * This method will generate all the nodes that are attached to a Google Api Client.
     * Now, theoretically, only one should be: the phone. However, they return us more
     * a list. In the case where the phone happens to not be the first/only, I decided to
     * make a List of all the nodes and we'll loop through them and send each of them
     * a message. After getting the list of nodes, it sends a message to each of them telling
     * it to start. One the last successful node, it saves it as our one peerNode.
     */
    private void messagePhone(final String message, final byte[] byteMap){

        new AsyncTask<Void, Void, List<Node>>(){

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.e("WATCH", "......Phone: Sending Msg:  to node:  " + node.getId());
                    Log.e("WATCH", "Sending to: " + message.toString());
                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            message.toString(),
                            byteMap
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.e("DEVELOPER", "......Phone: " + sendMessageResult.getStatus().getStatusMessage());

                        }
                    });
                }
            }
        }.execute();

    }

    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        DataMap dataMap = new DataMap();
        dataMap.putString("convid", conversations.get(viewHolder.getPosition()));
        messagePhone("inConversation", dataMap.toByteArray());
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("WATCH", "connected to Google Play Services on Wear!");
        Wearable.MessageApi.addListener(mGoogleApiClient, this).setResultCallback(resultCallback);
    }

    /**
     * Not needed, but here to show capabilities. This callback occurs after the MessageApi
     * listener is added to the Google API Client.
     */
    private ResultCallback<Status> resultCallback =  new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            Log.v("WATCH", "Status: " + status.getStatus().isSuccess());
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    Log.e("WATCH", "doInBackground");
                    return null;
                }
            }.execute();
        }
    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("WATCH", "Conversation here");
        if(messageEvent.getPath().contains("contacts")) {

            ContactSync contactsSync = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));

            conversations = contactsSync.getContacts();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    loadAdapter();
                }
            });
        }

        if(messageEvent.getPath().contains("drawings")) {
            List<Drawing> drawings = new ArrayList<>();
            DataMap data = DataMap.fromByteArray(messageEvent.getData());
            int drawingsAmount = data.getInt("amountOfDrawings");
            for (int i = 0; i < drawingsAmount; i++) {
                Drawing drawing = new Drawing(data.getFloatArray("y-coordinates " + i)
                        , data.getFloatArray("x-coordinates " + i)
                        , data.getLongArray("drawing-times " + i)
                        , data.getStringArray("actions " + i)
                        , data.getByteArray("staticDrawing " + i));
                drawings.add(drawing);

            }
            Log.e("drawings", "new drawings");
            DrawingHolder.getInstance().setDrawings(drawings);


            Intent intent = new Intent(this, ConversationViewActivity.class);
            startActivity(intent);
        }
    }

    public class MyListAdapter extends WearableListView.Adapter {

        private final Context context;
        private final List<String> items;

        public MyListAdapter(Context context, List<String> items) {
            this.context = context;
            this.items = items;
        }
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new MyItemView(ConversationListActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            MyItemView mItemView = (MyItemView) viewHolder.itemView;
            final String item = items.get(i);

            TextView txt = (TextView) mItemView.findViewById(R.id.text);
            txt.setText(item.toString());


        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private final class MyItemView extends FrameLayout implements WearableListView.OnCenterProximityListener{

        final ImageView image;
        final TextView txtView;

        public MyItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.wearable_listview_item, this);
            image = (ImageView) findViewById(R.id.image);
            txtView = (TextView) findViewById(R.id.text);
        }

        @Override
        public void onCenterPosition(boolean b) {
            image.animate().scaleX(1f).scaleY(1f).alpha(1);
            txtView.animate().scaleX(1f).scaleY(1f).alpha(1);

        }

        @Override
        public void onNonCenterPosition(boolean b) {

            image.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
            txtView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        }
    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //What to do if a message is received
        }
    }
}