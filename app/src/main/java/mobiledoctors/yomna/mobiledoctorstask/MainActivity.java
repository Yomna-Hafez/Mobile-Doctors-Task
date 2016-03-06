package mobiledoctors.yomna.mobiledoctorstask;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class MainActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLogged()) {
            Intent openHome = new Intent(MainActivity.this, Home.class);
            startActivity(openHome);
            finish();

        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            setContentView(R.layout.activity_main);
            info = (TextView) findViewById(R.id.info);
            loginButton = (LoginButton) findViewById(R.id.login_button);


            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                private ProfileTracker mProfileTracker;

                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (Profile.getCurrentProfile() == null) {
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                // profile2 is the new profile
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userName", profile2.getName()).commit();
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("profilePictureURL", profile2.getProfilePictureUri(400, 400).toString()).commit();
                                mProfileTracker.stopTracking();
                            }
                        };
                        mProfileTracker.startTracking();
                    } else {
                        Profile profile = Profile.getCurrentProfile();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userName", profile.getName()).commit();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("profilePictureURL", profile.getProfilePictureUri(400, 400).toString()).commit();

                    }
                    Intent openHome = new Intent(MainActivity.this, Home.class);
                    startActivity(openHome);
                    finish();
                }

                @Override
                public void onCancel() {
                    info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {
                    info.setText("Login attempt failed.");
                }
            });
        }
    }

    protected boolean isLogged() {
        String userName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userName", "");
        String profilePictureURL = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("profilePictureURL", "");

        if (userName != null && profilePictureURL != null) {
            if (!(userName.equals("") && userName.equals(""))) {
                return true;
            }
        }
        return false;
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    //Track App Installs and App Opens - check app's Insights dashboard - FB
    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
