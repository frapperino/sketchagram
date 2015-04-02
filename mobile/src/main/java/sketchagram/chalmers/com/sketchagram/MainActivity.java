package sketchagram.chalmers.com.sketchagram;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.SystemUser;


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
        inConversationFragment = new InConversationFragment();
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
        displayFragment(conversationFragment);

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
        SystemUser.getInstance().getUser().addObserver(this);
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
            displayFragment(contactSendFragment);
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
        SharedPreferences preferences = getSharedPreferences(MESSAGE, 0);
        preferences.edit()
                .clear()
                .putString(MESSAGE, ":D")
                .apply();
        displayFragment(contactSendFragment);
    }

    @Override
    public void onFragmentInteraction(String id) {
        if (id.contains("conversation")) {
            //Create a new fragment and replace the old fragment in layout.
            displayFragment(inConversationFragment);
        } else {
            displayFragment(conversationFragment);
        }
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
        Log.d("NavDraw", "" + position);
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

    public void addContact(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contact_dialog);
        dialog.setTitle("Add new contact");
        ((Button) dialog.findViewById(R.id.add_contact_dialog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast toast;
                String user = ((EditText) dialog.findViewById(R.id.user_name_dialog)).getText().toString();
                if (SystemUser.getInstance().getUser().addContact(user)) {
                    toast = Toast.makeText(getApplicationContext(), user + " added to contacts.", Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getApplicationContext(), user + " couldn't be added.", Toast.LENGTH_LONG);
                }
                toast.show();
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


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().contains("contacts")) {
            ContactsSync cs = new ContactsSync(SystemUser.getInstance().getUser().getContactList());
            sendToWatch("contacts", cs.putToDataMap(dataMap).toByteArray());
        } else if(messageEvent.getPath().contains("messageTo")) {
            ContactsSync cs = new ContactsSync(DataMap.fromByteArray(messageEvent.getData()));
            for(Contact c : cs.getContacts()) {
                List<ADigitalPerson> ls = new ArrayList<>();
                ls.add(c);
                ClientMessage<String> clientMessage = new ClientMessage(System.currentTimeMillis(), SystemUser.getInstance().getUser(),
                        ls, "Massmessage from wear", MessageType.TEXTMESSAGE);
                SystemUser.getInstance().getUser().sendMessage(clientMessage);
            }
        } else if(messageEvent.getPath().contains("conversations")) {

            ConversationsSync cs = new ConversationsSync();
            sendToWatch("conversations", cs.putToDataMap(dataMap).toByteArray());
        } else if(messageEvent.getPath().contains("username")) {
            dataMap.putString("username", SystemUser.getInstance().getUser().getUsername());
            sendToWatch("username", dataMap.toByteArray());
        } else if(messageEvent.getPath().contains("drawing")) {
            Drawing drawing = new Drawing(DataMap.fromByteArray(messageEvent.getData()));
            ContactsSync cs = new ContactsSync(DataMap.fromByteArray(messageEvent.getData()));
            ClientMessage<Drawing> cm = new ClientMessage(System.currentTimeMillis(), SystemUser.getInstance().getUser(),
                    cs.getContacts(), drawing, MessageType.DRAWING);
            SystemUser.getInstance().getUser().sendMessage(cm);
        } else {
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
            if (data != null) {
                //Update relevant ListAdapters.
                inConversationFragment.updateList((ClientMessage) data, this);
            }
        }
}