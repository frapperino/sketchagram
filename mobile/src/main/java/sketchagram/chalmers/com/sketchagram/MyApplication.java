package sketchagram.chalmers.com.sketchagram;

import android.app.Application;
import sketchagram.chalmers.com.database.DBHelper;
import sketchagram.chalmers.com.database.SketchagramDb;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.internal.ge;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.BTCommType;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.EmoticonType;
import sketchagram.chalmers.com.model.IUserManager;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.User;
import sketchagram.chalmers.com.model.UserManager;
import sketchagram.chalmers.com.network.Connection;
import sketchagram.chalmers.com.network.NetworkException;
import sketchagram.chalmers.com.network.NetworkService;

/**
 * Created by Alexander Härenstam on 2015-02-26.
 */
public class MyApplication extends Application implements MessageApi.MessageListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private static MyApplication ourInstance;
    private static SketchagramDb db = null;
    private static Context context;
    private static String FIRST_STARTUP = "FIRST_STARTUP";
    private User user;
    private boolean firstStart = true;
    GoogleApiClient mGoogleApiClient;

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
        //  Needed for communication between watch and device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        tellWatchConnectedState("connected");
                        //  "onConnected: null" is normal.
                        //  There's nothing in our bundle.
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
        Wearable.MessageApi.addListener(mGoogleApiClient, this);

    }

    /**
     * Receives all messages from the wear device.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        DataMap dataMap = new DataMap();
        IUserManager userManager = UserManager.getInstance();
        if(messageEvent.getPath().equals(BTCommType.GET_CONTACTS.toString())) { //From contact-activity
            ContactSync cs = new ContactSync(userManager.getAllContacts());
            sendToWatch(BTCommType.GET_CONTACTS.toString(), cs.putToDataMap(dataMap).toByteArray());
        } else if(messageEvent.getPath().equals(BTCommType.SEND_TO_CONTACT.toString())) { //From clock emoji-message
            ContactSync cs = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));
            for(Contact c : cs.getContacts()) {
                List<Contact> ls = new ArrayList<>();
                ls.add(c);

                userManager.sendMessage(ls, "Emoticon from wear");
            }
        } else if(messageEvent.getPath().equals(BTCommType.GET_DRAWINGS.toString())) { // From ConversationViewActivity
            String username = DataMap.fromByteArray(messageEvent.getData()).getString("convid");
            Contact contact = null;
            Conversation conversation = null;
            for (Contact c : userManager.getAllContacts()) {
                if (c.getUsername().equals(username))
                    contact = c;
            }

            for (Conversation c : userManager.getAllConversations()) {
                for(ADigitalPerson p : c.getParticipants()){
                    if(contact.getUsername().equals(p.getUsername()))
                        conversation = c;
                }

            }

            dataMap.clear();
            int i = 0;

            ArrayList<String> emojis = new ArrayList<String>();
            ArrayList<Integer> emojiPositions = new ArrayList<Integer>();

            if(conversation != null) {
                for (ClientMessage message : conversation.getHistory()) {
                    if (message.getType().equals(MessageType.EMOTICON)) {
                        emojis.add(((Emoticon)message.getContent()).getEmoticonType().toString());
                        emojiPositions.add(i);
                    }
                    if (message.getType().equals(MessageType.DRAWING)) {
                        Drawing drawing = (Drawing) message.getContent();
                        dataMap.putFloatArray("x-coordinates " + i, drawing.getX());
                        dataMap.putFloatArray("y-coordinates " + i, drawing.getY());
                        dataMap.putLongArray("drawing-times " + i, drawing.getTimes());
                        dataMap.putStringArray("actions " + i, drawing.getActions());
                        dataMap.putByteArray("staticDrawing " + i, drawing.getStaticDrawingByteArray());
                    }

                    i++;
                }
                dataMap.putInt("amountOfMessages", i);
                dataMap.putStringArrayList("emojis", emojis);
                dataMap.putIntegerArrayList("emojisPositions", emojiPositions);

            } else {
                dataMap.putInt("amountOfMessages", 0);
            }

            sendToWatch(BTCommType.GET_DRAWINGS.toString(), dataMap.toByteArray());

            //From DrawingActivity in clock
        } else if(messageEvent.getPath().equals(BTCommType.SEND_DRAWING.toString())) {

            Drawing drawing = new Drawing(DataMap.fromByteArray(messageEvent.getData()));
            ContactSync cs = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));
            userManager.sendMessage(cs.getContacts(), drawing);

        } else if(messageEvent.getPath().equals(BTCommType.SEND_EMOJI.toString())) {
            Log.e("EMOJI", "trying to send");
            ContactSync cs = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));
            String emoji = DataMap.fromByteArray(messageEvent.getData()).getString(BTCommType.SEND_EMOJI.toString());
            userManager.sendMessage(cs.getContacts(), new Emoticon(EmoticonType.valueOf(emoji)));

        }
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This method will generate all the nodes that are attached to a Google Api Client.
     * There should only be one node however, which should be the watch.
     */
    private void sendToWatch(String msg, final byte[] data){
        getSharedPreferences("WATCHMSG", 0).edit().putString("WATCHMSG", msg).commit();

        new AsyncTask<Void, Void, List<Node>>(){

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            getSharedPreferences("WATCHMSG",0).getString("WATCHMSG", ""),
                            data
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v("DEVELOPER", "......Clock: " + sendMessageResult.getStatus().getStatusMessage());
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

    //Below code is for connecting and communicating with Wear
    private void tellWatchConnectedState(final String state) {

        new AsyncTask<Void, Void, List<Node>>() {

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for (Node node : nodeList) {

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            " " + state,
                            null
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        }
                    });
                }
            }
        }.execute();

    }
}
