package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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


public class EmojiListActivity extends Activity implements WearableListView.ClickListener,
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private MyListAdapter mAdapter;
    private WearableListView mListView;

    private String receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_list);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.emoji_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.emojiListView);
                messagePhone(BTCommType.GET_EMOJIS.toString(), null);
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

        receiver = getIntent().getStringExtra(BTCommType.SEND_TO_CONTACT.toString());

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emoji_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAdapter(){
        mAdapter = new MyListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setClickListener(EmojiListActivity.this);
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

        //Add the receiver to a dataMap
        ContactSync cs = new ContactSync();
        cs.addContact(receiver);
        cs.putToDataMap(dataMap);

        //Add the emoji to a dataMap
        dataMap.putString(BTCommType.SEND_EMOJI.toString(), mAdapter.getEmojis().get(viewHolder.getPosition()));

        //Send to phone
        messagePhone(BTCommType.SEND_EMOJI.toString(), dataMap.toByteArray());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    /**
     * This function is called when the phone wants to send info,
     * only contacts and drawings are received in this onMessageReceived.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    }

    public class MyListAdapter extends WearableListView.Adapter {

        private final Context context;
        private final Bitmap[] items;
        private final List<String> emojis;

        public MyListAdapter(Context context) {
            this.context = context;
            items = new Bitmap[4];
            emojis = new ArrayList<String>();
            loadBitmaps();
        }
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new MyItemView(EmojiListActivity.this));
        }

        private void loadBitmaps() {
            items[0] = BitmapFactory.decodeResource(getResources(), EmoticonType.SAD.getRes());
            emojis.add(EmoticonType.SAD.toString());
            items[1] = BitmapFactory.decodeResource(getResources(), EmoticonType.HAPPY.getRes());
            emojis.add(EmoticonType.HAPPY.toString());
            items[2] = BitmapFactory.decodeResource(getResources(), EmoticonType.FLIRT.getRes());
            emojis.add(EmoticonType.FLIRT.toString());
            items[3] = BitmapFactory.decodeResource(getResources(), EmoticonType.HEART.getRes());
            emojis.add(EmoticonType.HEART.toString());

        }

        public List<String> getEmojis() {
            return emojis;
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            MyItemView mItemView = (MyItemView) viewHolder.itemView;
            final Bitmap item = items[i];

            ImageView img = (ImageView) mItemView.findViewById(R.id.image);
            img.setImageBitmap(item);


        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    private final class MyItemView extends FrameLayout implements WearableListView.OnCenterProximityListener{

        final ImageView image;

        public MyItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.activity_emoji_view, this);
            image = (ImageView) findViewById(R.id.image);
        }

        @Override
        public void onCenterPosition(boolean b) {
            image.animate().scaleX(1f).scaleY(1f).alpha(1);

        }

        @Override
        public void onNonCenterPosition(boolean b) {

            image.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        }
    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //What to do if a message is received
        }
    }

}
