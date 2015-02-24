package sketchagram.chalmers.com.sketchagram;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import sketchagram.chalmers.com.model.test.DummyData;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.User;


public class MainActivity extends ActionBarActivity implements EmoticonFragment.OnFragmentInteractionListener
        , ContactFragment.OnFragmentInteractionListener, ConversationFragment.OnFragmentInteractionListener {

    private final String FILENAME = "user";
    private EmoticonFragment emoticonFragment;
    private ContactFragment contactFragment;
    private ConversationFragment conversationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(FILENAME, 0);
        emoticonFragment = new EmoticonFragment();
        contactFragment = new ContactFragment();
        conversationFragment = new ConversationFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragmentlayout, conversationFragment);
        ft.commit();
//        ((TextView)findViewById(R.id.text)).setText(username);
        User user = new User(pref.getString("username", null), new Profile());
        SystemUser.getInstance().setUser(user);
        DummyData.injectData();
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
            prefs.commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_new_message) {

            //Create a new fragment and replace the old fragment in layout.
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.fragmentlayout, emoticonFragment);
            t.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void imageClick(View view) {
        Log.e("IMAGE", "click");

        //Create a new fragment and replace the old fragment in layout.
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.fragmentlayout, contactFragment);
        t.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e("FRAGMENT", uri.getFragment());
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.e("FRAGMENT", id);

        //Create a new fragment and replace the old fragment in layout.
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.fragmentlayout, conversationFragment);
        t.commit();
    }
}