package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Fragment;    //v4 only used for android version 3 or lower.
import android.support.v4.widget.DrawerLayout;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Profile;
import android.widget.Button;
import sketchagram.chalmers.com.model.SystemUser;


public class MainActivity extends ActionBarActivity implements EmoticonFragment.OnFragmentInteractionListener,
        ConversationFragment.OnFragmentInteractionListener,
        InConversationFragment.OnFragmentInteractionListener, MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        Handler.Callback, ContactSendFragment.OnFragmentInteractionListener,
        ContactManagementFragment.OnFragmentInteractionListener, AddContactFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks{


    private static final int MSG_POST_NOTIFICATIONS = 0;
    private static final long POST_NOTIFICATIONS_DELAY_MS = 200;
    private final String FILENAME = "user";
    private final String MESSAGE = "message";
    private final String TAG = "Sketchagram";
    private Fragment emoticonFragment;
    private Fragment contactSendFragment;
    private Fragment conversationFragment;
    private Fragment inConversationFragment;
    private Fragment contactManagementFragment;
    private Fragment addContactFragment;
    private FragmentManager fragmentManager; 
    private Handler mHandler;
    private int postedNotificationCount = 0;

    // used to store app title
    private CharSequence mTitle;

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(FILENAME, 0);
        emoticonFragment = new EmoticonFragment();
        contactSendFragment = new ContactSendFragment();
        conversationFragment = new ConversationFragment();
        inConversationFragment = new InConversationFragment();
        contactManagementFragment = new ContactManagementFragment();
        addContactFragment = new AddContactFragment();
        //DummyData.injectData();

        //  Needed for communication between watch and device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        tellWatchConnectedState("connected");
                        //  "onConnected: null" is normal.
                        //  There's nothing in our bundle.
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
        Wearable.MessageApi.addListener(mGoogleApiClient, this).setResultCallback(resultCallback);
        mHandler = new Handler(this);

        fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_frame, conversationFragment);
        ft.commit();

        /*
        Navigation drawer
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        } else if (id == R.id.action_about) {
            return true;
        } else if (id == R.id.action_logout) {
            //Delete saved user
            SharedPreferences pref = getSharedPreferences(FILENAME, 0);
            SharedPreferences.Editor prefs = pref.edit();
            prefs.clear();
            prefs.apply();
            SystemUser.getInstance().logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_new_message) {
            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(R.id.fragment_frame, emoticonFragment);
            t.commit();
        } else if (id == android.R.id.home) {
            //Open or close navigation drawer on ActionBar click.
            mDrawerLayout.closeDrawers();
        } else {
            throw new IllegalStateException("Forbidden item selected in menu!");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("EMOTICON", uri.getPath());

        SharedPreferences preferences = getSharedPreferences(MESSAGE, 0);
        preferences.edit()
                .clear()
                .putString(MESSAGE, ":D")
                .apply();

        //Create a new fragment and replace the old fragment in layout.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, contactSendFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.e("FRAGMENTINTERACTION", id);
        if (id.contains("conversation")) {
            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(R.id.fragment_frame, inConversationFragment)
                    .commit();
        } else {
            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(R.id.fragment_frame, conversationFragment)
                    .commit();

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d("NavDraw", "" + position);
        //Logic for item selection in navigation drawer.
        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = conversationFragment;
                break;
            case 1:
                fragment = contactManagementFragment;
                break;
            default:
                throw new IllegalStateException("Illegal option chosen in NavigationDrawer!");
        }
        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, fragment)
                    .commit();
        }
    }

    /**
     * Start the add contact fragment on responding button-press.
     * @param view
     */
    public void startAddContactFragment(View view) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_frame, addContactFragment);
        ft.commit();
    }

    public void addContact(View view) {
        SystemUser.getInstance().getUser().addContact("alleballe");
        Log.d("Add_Contact", "Button pressed!");
    }

    /**
     *
     * @param participants should be in the form of a list.toString()
     * @param msg isn't used at the moment, but should be implemented.
     */
    private void postNewConversation(String participants, AMessage msg) {
        List<ADigitalPerson> receivers = new ArrayList<>();
        for(String contact : participants.split(" ")) {
            if(contact.contains("[") ){
                contact = contact.substring(1,contact.length()-1);
            } else if(contact.contains("]")){
                contact = contact.substring(0,contact.length()-1);
            } else {
                contact = contact.substring(0,contact.length()-1);
            }

            for (Contact c : SystemUser.getInstance().getUser().getContactList()) {
                if (c.getUsername().equals(contact))
                    receivers.add(c);
            }
        }

        receivers.add(SystemUser.getInstance().getUser());

        //just for now
        msg = new Emoticon(System.currentTimeMillis(), SystemUser.getInstance().getUser(), receivers);

        Conversation newConversation = new Conversation(receivers);
        newConversation.addMessage(msg);
        SystemUser.getInstance().getUser().addConversation(newConversation);

    }


    //Below code is for connecting and communicating with Wear


    private void tellWatchConnectedState(final String state){

        new AsyncTask<Void, Void, List<Node>>(){

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.v(TAG, "telling " + node.getId() + " i am " + state);

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            " " + state,
                            null
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v(TAG, "Phone: " + sendMessageResult.getStatus().getStatusMessage());
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
                    //TODO put code here to do something when listener is added.
                    return null;
                }
            }.execute();
        }
    };

    /**
     * Begin to re-post the sample notification(s).
     */
    private void updateNotifications(boolean cancelExisting) {
        // Disable messages to skip notification deleted messages during cancel.
        sendBroadcast(new Intent(NotificationIntentReceiver.ACTION_DISABLE_MESSAGES)
                .setClass(this, NotificationIntentReceiver.class));

        if (cancelExisting) {
            // Cancel all existing notifications to trigger fresh-posting behavior: For example,
            // switching from HIGH to LOW priority does not cause a reordering in Notification Shade.
            NotificationManagerCompat.from(this).cancelAll();
            postedNotificationCount = 0;

            // Post the updated notifications on a delay to avoid a cancel+post race condition
            // with notification manager.
            mHandler.removeMessages(MSG_POST_NOTIFICATIONS);
            mHandler.sendEmptyMessageDelayed(MSG_POST_NOTIFICATIONS, POST_NOTIFICATIONS_DELAY_MS);
        } else {
            postNotifications();
        }
    }

    /**
     * Post the sample notification(s) using current options.
     */
    private void postNotifications() {
        sendBroadcast(new Intent(NotificationIntentReceiver.ACTION_ENABLE_MESSAGES)
                .setClass(this, NotificationIntentReceiver.class));

        NotificationPreset preset = NotificationPresets.PRESETS[
                0];
        CharSequence titlePreset = "Notifikation";
        CharSequence textPreset = "Det här är en notifikation!!! Wiiiie! :D";
        NotificationPreset.BuildOptions options = new NotificationPreset.BuildOptions(
                titlePreset,
                textPreset);
        Notification[] notifications = preset.buildNotifications(this, options);

        // Post new notifications
        for (int i = 0; i < notifications.length; i++) {
            NotificationManagerCompat.from(this).notify(i, notifications[i]);
        }
        // Cancel any that are beyond the current count.
        for (int i = notifications.length; i < postedNotificationCount; i++) {
            NotificationManagerCompat.from(this).cancel(i);
        }
        postedNotificationCount = notifications.length;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().contains("contacts")) {
            sendToWatch(SystemUser.getInstance().getUser().getContactList().toString());
        } else if(messageEvent.getPath().contains("clockversations")) {
            sendToWatch(SystemUser.getInstance().getUser().getConversationList().toString());
        } else if(messageEvent.getPath().contains("username")) {
            sendToWatch(SystemUser.getInstance().getUser().getUsername());
        } else {
            Log.e("CLOCK", "Click");
            onFragmentInteraction(messageEvent.getPath());
        }
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

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }



    /**
     * This method will generate all the nodes that are attached to a Google Api Client.
     * There should only be one node however, which should be the watch.
     */
    private void sendToWatch(String msg){
        getSharedPreferences("WATCHMSG", 0).edit().putString("WATCHMSG", msg).commit();

        new AsyncTask<Void, Void, List<Node>>(){

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.v(TAG, "......Phone: Sending Msg:  to node:  " + node.getId());

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            getSharedPreferences("WATCHMSG",0).getString("WATCHMSG", ""),
                            null
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
}