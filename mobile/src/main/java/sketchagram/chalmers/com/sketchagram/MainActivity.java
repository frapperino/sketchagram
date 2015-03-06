package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Fragment;    //v4 only used for android version 3 or lower.
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.test.DummyData;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.User;


public class MainActivity extends ActionBarActivity implements EmoticonFragment.OnFragmentInteractionListener
        , ContactSendFragment.OnFragmentInteractionListener, ConversationFragment.OnFragmentInteractionListener,
        InConversationFragment.OnFragmentInteractionListener, ContactManagementFragment.OnFragmentInteractionListener, AddContactFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks{

    private final String FILENAME = "user";
    private final String MESSAGE = "message";
    private Fragment emoticonFragment;
    private Fragment contactSendFragment;
    private Fragment conversationFragment;
    private Fragment inConversationFragment;
    private Fragment contactManagementFragment;
    private Fragment addContactFragment;
    private FragmentManager fragmentManager;

    // used to store app title
    private CharSequence mTitle;

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;

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
        User user = new User(pref.getString("username", "User"), new Profile());
        SystemUser.getInstance().setUser(user);
        DummyData.injectData();

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
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.about_dialog);
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
        Log.d("FRAGMENT", id);
        if (id.contains("conversation")) {
            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(R.id.fragment_frame, inConversationFragment)
                    .commit();
        } else {
            List<ADigitalPerson> receivers = new ArrayList<>();
            for (Contact c : SystemUser.getInstance().getUser().getContactList()) {
                if (c.getUsername().equals(id))
                    receivers.add(c);
            }

            List<ADigitalPerson> participants = receivers;
            participants.add(SystemUser.getInstance().getUser());
            Emoticon emoticon = new Emoticon(System.currentTimeMillis(), SystemUser.getInstance().getUser(), receivers);

            Conversation conversation = new Conversation(participants);
            conversation.addMessage(emoticon);
            SystemUser.getInstance().getUser().addConversation(conversation);

            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(R.id.fragment_frame, conversationFragment)
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d("NavDraw", ""+position);
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
}