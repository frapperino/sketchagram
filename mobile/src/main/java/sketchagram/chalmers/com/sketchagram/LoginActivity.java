package sketchagram.chalmers.com.sketchagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.DecimalFormat;

import sketchagram.chalmers.com.model.UserManager;
import sketchagram.chalmers.com.network.NetworkException;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {
    private final String FILENAME = "user";
    private final long LOCKOUT_TIME_IN_MILI = 300000;    //5 minutes.
    private final int ATTEMPTS_ALLOWED = 5;

    private int currentApiVersion;
    private LoginFragment loginFragment;
    private int attemptsMade = 0;
    private boolean lockoutActive = false;
    private long lockoutTimestamp;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (UserManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        loginFragment = new LoginFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_frame_login, loginFragment).commit();

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * Attempts to create a new account using the server.
     *
     * @param view
     */
    public void createAccount(View view) {
        EditText usernameView = ((EditText) findViewById(R.id.enter_username_id));
        EditText passwordView = ((EditText) findViewById(R.id.choose_password_id));
        EditText reenterPasswordView = ((EditText) findViewById(R.id.re_enter_password_id));
        String mUserName = usernameView.getText().toString();
        String mPassword = passwordView.getText().toString();
        String mReEnterPassword = reenterPasswordView.getText().toString();

        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);
        reenterPasswordView.setError(null);

        String usernameError = checkUsernameValid(mUserName);
        String passwordError = checkPasswordValid(mPassword);
        String reenterPasswordError = checkPasswordValid(mReEnterPassword);

        if (usernameError != null) {
            usernameView.setError(usernameError);
        }
        if (passwordError != null) {
            passwordView.setError(passwordError);
        }
        if (reenterPasswordError != null) {
            reenterPasswordView.setError(reenterPasswordError);
        }
        if (!mPassword.equals(mReEnterPassword)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Passwords does not match.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (usernameError == null && passwordError == null && reenterPasswordError == null) {
            try {
                loginFragment.showProgressBar();
                UserManager.getInstance().createAccount(mUserName, mPassword);
                //TODO: Get entered email and check if correct.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame_login, loginFragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT);
                toast.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                usernameView.setText("");
                passwordView.setText("");
                reenterPasswordView.setText("");
                loginFragment.hideProgressBar();
            } catch (NetworkException.UsernameAlreadyTakenException e) {
                Toast.makeText(getApplicationContext(), "Username already taken.", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                //TODO: Find out where this exception is cast from.
                Toast.makeText(getApplicationContext(), "Unknown error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        if (isLockedOut())
            return;

        // Set up the login form.
        AutoCompleteTextView mUserNameView = (AutoCompleteTextView) findViewById(R.id.username_id);
        EditText mPasswordView = (EditText) findViewById(R.id.password_id);

        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        String usernameError = checkUsernameValid(username);
        String passwordError = checkPasswordValid(password);

        if (usernameError != null) {
            mUserNameView.setError(usernameError);
        }
        if (passwordError != null) {
            mPasswordView.setError(passwordError);
        }
        if (usernameError == null && passwordError == null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                if (loginServer(username, password)) {
                    SharedPreferences.Editor editor = getSharedPreferences(FILENAME, 0).edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.commit();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /**
     * Handles counting of attempts made and lockout time for
     * a user who's made too many unsuccessful attempts.
     *
     * @return true if user is locked out, false otherwise.
     */
    private boolean isLockedOut() {
        attemptsMade += 1;
        //User has exceeded allowed attempts.
        if (attemptsMade >= ATTEMPTS_ALLOWED) {
            if (!lockoutActive) {
                lockoutActive = true;
                lockoutTimestamp = System.currentTimeMillis() + LOCKOUT_TIME_IN_MILI;
            }
            //User has been locked out long enough.
            if (System.currentTimeMillis() >= lockoutTimestamp && lockoutActive) {
                attemptsMade = 0;
                lockoutActive = false;
            } else {
                double lockoutTimeMinutes = ((double) (lockoutTimestamp - System.currentTimeMillis()) / 60000);
                DecimalFormat numberFormat = new DecimalFormat("#.00"); //Maximum two decimals.
                String lockoutText = "Too many login attempts has been made! "
                        + numberFormat.format(lockoutTimeMinutes) + " minutes left until you can try again.";
                Toast toast = Toast.makeText(getApplicationContext(), lockoutText, Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to login on the server.
     *
     * @param username the provided username from user.
     * @param password the provided password from user.
     * @return true if logged in, false otherwise.
     */
    private boolean loginServer(String username, String password) {
        loginFragment.showProgressBar();
        boolean success = UserManager.getInstance().login(username, password);
        if (success) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //TODO: More detailed feedback handled from network package.
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to login. Try again later.", Toast.LENGTH_SHORT);
            toast.show();
        }
        loginFragment.hideProgressBar();
        return success;
    }

    /**
     * Logic for account check.
     *
     * @param username
     * @return A corresponding error message if faulty, otherwise null.
     */
    private String checkUsernameValid(String username) {
        if (username.length() == 0) {
            return "No username has been entered.";
        } else if (username.length() <= 3) {
            return "Username too short";
        }
        return null;
    }

    /**
     * Logic for password check.
     *
     * @param password
     * @return A corresponding error message if faulty, otherwise null.
     */
    private String checkPasswordValid(String password) {
        if (password.length() == 0) {
            return "No password has been entered.";
        } else if (password.length() <= 3) {
            return "Password length too short. Has to be at least 4 characters.";
        } else if (password.length() >= 30) {
            return "Password length too long. Should be less than 30 characters.";
        }
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //TODO: blur

    public void scrollToTop(View view) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void scrollToBottom(View view) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

}



