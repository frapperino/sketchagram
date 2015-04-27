package sketchagram.chalmers.com.sketchagram;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Fragment;    //v4 only used for android version 3 or lower.
import android.support.v4.widget.DrawerLayout;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.BTCommType;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.EmoticonType;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.network.Connection;


public class MainActivity extends ActionBarActivity
        implements SendFragment.OnFragmentInteractionListener,
        ConversationFragment.OnFragmentInteractionListener,
        InConversationFragment.OnFragmentInteractionListener, MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        Handler.Callback, ContactSendFragment.OnFragmentInteractionListener,
        ContactManagementFragment.OnFragmentInteractionListener,
        AddContactFragment.OnFragmentInteractionListener,
        DrawingFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks, Observer {

    private final String FILENAME = "user";
    private final String MESSAGE = "message";
    private final String TAG = "Sketchagram";
    private Fragment sendFragment;
    private Fragment contactSendFragment;
    private ConversationFragment conversationFragment;
    private InConversationFragment inConversationFragment;
    private Fragment contactManagementFragment;
    private DrawingFragment drawingFragment;
    private FragmentManager fragmentManager;
    private Handler mHandler;
    private DataMap dataMap;

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if logged in, else start LoginActivity
        sendFragment = new SendFragment();
        contactSendFragment = new ContactSendFragment();
        conversationFragment = new ConversationFragment();
        contactManagementFragment = new ContactManagementFragment();
        drawingFragment = new DrawingFragment();

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

        /*
         * Navigation drawer
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        dataMap = new DataMap();

        //Set observer
        MyApplication.getInstance().getUser().addObserver(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {    //Notification passed a conversationId.
            inConversationFragment = InConversationFragment.newInstance(bundle.getInt("ConversationId"));
            displayFragment(inConversationFragment);
        } else {    //Normal startup
            displayFragment(conversationFragment);
        }
     }

    public void startDrawingFragment(View v) {
        displayFragment(drawingFragment);
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
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.action_about) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.about_dialog);
            dialog.setTitle("About");
            ((Button) dialog.findViewById(R.id.dialogButtonOK)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }else if (id == R.id.action_change_password) {
            changePassword();
        } else if (id == R.id.action_logout) {
            //Delete saved user
            SharedPreferences pref = getSharedPreferences(FILENAME, 0);
            SharedPreferences.Editor prefs = pref.edit();
            prefs.clear();
            prefs.apply();
            MyApplication.getInstance().logout();
            MyApplication.getInstance().getDatabase().update();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_new_message) {
            showContactSendFragment();
        } else if (id == android.R.id.home) {
            //Open or close navigation drawer on ActionBar click.
            mDrawerLayout.closeDrawers();
        } else {
            throw new UnsupportedOperationException("Menu item selected not supported!");
        }
        return super.onOptionsItemSelected(item);
    }

    public void showContactSendFragment() {
        displayFragment(contactSendFragment);
    }

    public void newMessage(View view) {
        displayFragment(contactSendFragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        SharedPreferences preferences = getSharedPreferences(MESSAGE, 0);
        preferences.edit()
                .clear()
                .putString(MESSAGE, ":D")
                .apply();
        showContactSendFragment();
    }

    @Override
    public void onFragmentInteraction(int conversationId) {
        inConversationFragment = InConversationFragment.newInstance(conversationId);
        displayFragment(inConversationFragment);
    }

    /**
     * Change fragment displayed in the fragment frame.
     *
     * @param fragment
     */
    private void displayFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Logic for item selection in navigation drawer.
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = conversationFragment;
                break;
            case 1:
                fragment = contactManagementFragment;
                break;
            default:
                throw new IllegalStateException("Illegal option chosen in NavigationDrawer!");
        }
        if (fragment != null) {
            displayFragment(fragment);
        }
    }

    public void changePassword() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_password_dialog);
        dialog.setTitle("Change password");
        ((Button) dialog.findViewById(R.id.change_password_dialog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast toast;
                String password = ((EditText) dialog.findViewById(R.id.change_password_dialog)).getText().toString();
                if (Connection.getInstance().changePassword(password)) {
                    toast = Toast.makeText(getApplicationContext(),"Password changed.", Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Password couldn't be changed.", Toast.LENGTH_LONG);
                }
                toast.show();
            }
        });
        ((Button) dialog.findViewById(R.id.cancel_change_password_dialog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void addContact(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contact_dialog);
        dialog.setTitle("Add new contact");
        ((Button) dialog.findViewById(R.id.add_contact_dialog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String user = ((EditText) dialog.findViewById(R.id.user_name_dialog)).getText().toString().toLowerCase();
                if (MyApplication.getInstance().getUser().addContact(user)) {
                    Toast.makeText(getApplicationContext(), user + " added to contacts.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), user + " couldn't be added.", Toast.LENGTH_LONG).show();;
                }
            }
        });
        ((Button) dialog.findViewById(R.id.cancel_dialog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
    private ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            Log.v(TAG, "Status: " + status.getStatus().isSuccess());
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //TODO put code here to do something when listener is added.
                    return null;
                }
            }.execute();
        }
    };

    /**
     * Receives all messages from the wear device.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        dataMap = new DataMap();
        if(messageEvent.getPath().equals(BTCommType.GET_CONTACTS.toString())) { //From contact-activity
            ContactSync cs = new ContactSync(MyApplication.getInstance().getUser().getContactList());
            sendToWatch(BTCommType.GET_CONTACTS.toString(), cs.putToDataMap(dataMap).toByteArray());
        } else if(messageEvent.getPath().equals(BTCommType.SEND_TO_CONTACT.toString())) { //From clock emoji-message
            ContactSync cs = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));
            for(Contact c : cs.getContacts()) {
                List<ADigitalPerson> ls = new ArrayList<>();
                ls.add(c);
                ClientMessage<String> clientMessage = new ClientMessage(System.currentTimeMillis(), MyApplication.getInstance().getUser(),
                        ls, "Emoticon from wear", MessageType.TEXTMESSAGE);
                MyApplication.getInstance().getUser().addMessage(clientMessage, true);
            }
        } else if(messageEvent.getPath().equals(BTCommType.GET_USERNAME.toString())) { //From MainActivity in clock
            dataMap.putString("username", MyApplication.getInstance().getUser().getUsername());
            sendToWatch(BTCommType.GET_USERNAME.toString(), dataMap.toByteArray());
        } else if(messageEvent.getPath().equals(BTCommType.GET_DRAWINGS.toString())) { // From ConversationViewActivity
            String username = DataMap.fromByteArray(messageEvent.getData()).getString("convid");
            Contact contact = null;
            Conversation conversation = null;
            for (Contact c : MyApplication.getInstance().getUser().getContactList()) {
                if (c.getUsername().equals(username))
                    contact = c;
            }

            for (Conversation c : MyApplication.getInstance().getUser().getConversationList()) {
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
                        emojis.add(message.getContent().toString());
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
            ClientMessage<Drawing> cm = new ClientMessage(System.currentTimeMillis(), MyApplication.getInstance().getUser(),
                    cs.getContacts(), drawing, MessageType.DRAWING);
            MyApplication.getInstance().getUser().addMessage(cm, true);

        } else if(messageEvent.getPath().equals(BTCommType.SEND_EMOJI.toString())) {
            Log.e("EMOJI" , "trying to send");
            ContactSync cs = new ContactSync(DataMap.fromByteArray(messageEvent.getData()));
            String emoji = DataMap.fromByteArray(messageEvent.getData()).getString(BTCommType.SEND_EMOJI.toString());
            ClientMessage<String> cm = new ClientMessage(System.currentTimeMillis(), MyApplication.getInstance().getUser(),
                    cs.getContacts(), EmoticonType.valueOf(emoji), MessageType.EMOTICON);
            MyApplication.getInstance().getUser().addMessage(cm, true);

        } else {
            onFragmentInteraction(messageEvent.getPath());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Must be implemented?
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Must be implemented?
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Must be implemented?
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
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
                    Log.v(TAG, "......Phone: Sending Msg:  to node:  " + node.getId());

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

    @Override
    public void onBackPressed() {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void update (Observable observable, Object data){
        conversationFragment.updateList();
        ((ContactSendFragment)contactSendFragment).updateList();
        ((ContactManagementFragment)contactManagementFragment).updateList();
        if (data != null && inConversationFragment != null) {
            //Update relevant ListAdapters.
            inConversationFragment.updateList();
        }
    }

    @Override
    public void onFragmentInteraction(String id) {
        //Currently not in use but has to be implemented, as defined by a fragment.
    }

}