package sketchagram.chalmers.com.sketchagram;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
        , ContactFragment.OnFragmentInteractionListener, ConversationFragment.OnFragmentInteractionListener,
        InConversationFragment.OnFragmentInteractionListener{

    private final String FILENAME = "user";
    private final String MESSAGE = "message";
    private EmoticonFragment emoticonFragment;
    private ContactFragment contactFragment;
    private ConversationFragment conversationFragment;
    private InConversationFragment inConversationFragment;

    /**
     * NavDrawer variables.
     */
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle = "Sketchagram"; //TODO: Change to be correct text.

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(FILENAME, 0);
        emoticonFragment = new EmoticonFragment();
        contactFragment = new ContactFragment();
        conversationFragment = new ConversationFragment();
        inConversationFragment = new InConversationFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragmentlayout, conversationFragment);
        ft.commit();
//        ((TextView)findViewById(R.id.text)).setText(username);
        User user = new User(pref.getString("username", "User"), new Profile());
        SystemUser.getInstance().setUser(user);
        DummyData.injectData();

        /*
        NavDrawer
         */
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Conversations
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1), true, "5"));
        // Contacts
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, "50+"));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
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
        Log.d("DRAWER DEBUG", ""+id);
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
            prefs.commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_new_message) {

            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.fragmentlayout, emoticonFragment);
            t.commit();
        } else if (id == 16908332) {
            mDrawerLayout.openDrawer(mDrawerList);
            Log.d("DRAWER DEBUG", "HOME CLICKED");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e("EMOTICON", uri.getPath());

        SharedPreferences preferences = getSharedPreferences(MESSAGE, 0);
        preferences.edit()
                .clear()
                .putString(MESSAGE, ":D")
                .commit();

        //Create a new fragment and replace the old fragment in layout.
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.fragmentlayout, contactFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.e("FRAGMENT", id);
        if (id.contains("conversation")) {

            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.fragmentlayout, inConversationFragment)
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
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.fragmentlayout, conversationFragment)
                    .commit();
        }
    }
    /**
     * Slide menu item click listener for NavDraw.
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ConversationFragment();
                break;
            case 1:
                fragment = new EmoticonFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentlayout, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}