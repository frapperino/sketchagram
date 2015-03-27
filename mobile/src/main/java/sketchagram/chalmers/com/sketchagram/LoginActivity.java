package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import sketchagram.chalmers.com.model.SystemUser;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends Activity implements RegistrationFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener {
    private final String FILENAME = "user";

    private RegistrationFragment registrationFragment;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getSharedPreferences(FILENAME, 0);

        String userName= pref.getString("username", null);
        if(userName != null) {
            if(SystemUser.getInstance().login(userName, pref.getString("password", null))) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        registrationFragment = new RegistrationFragment();
        loginFragment = new LoginFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_frame, new LoginFragment()).commit();
    }

    /**
     * Starts register fragment.
     * @param view
     */
    public void register(View view){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, registrationFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Attempts to create a new account using the server.
     * @param view
     */
    public void createAccount(View view) {
        String mUserName = ((EditText) findViewById(R.id.user_name)).getText().toString();
        String mPassword = ((EditText) findViewById(R.id.choose_password)).getText().toString();
        String mReEnterPassword = ((EditText) findViewById(R.id.re_enter_password)).getText().toString();
        if(!mPassword.equals(mReEnterPassword)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Passwords does not match.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Exception e = SystemUser.getInstance().createAccount(mUserName, mPassword);
            if(e != null){
                if(e.getMessage().toString().equals("conflict")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Username already taken.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                //TODO: Get entered email and check if correct.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame, loginFragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT);
                toast.show();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        ((EditText) findViewById(R.id.user_name)).setText("");
        ((EditText) findViewById(R.id.choose_password)).setText("");
        ((EditText) findViewById(R.id.re_enter_password)).setText("");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        // Set up the login form.
        AutoCompleteTextView mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        EditText mPasswordView = (EditText) findViewById(R.id.password);

        SharedPreferences prefs = getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        editor.putString("username", email);
        editor.putString("password", password);
        editor.commit();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean success = SystemUser.getInstance().login(email, password);
        if (success){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}



