package mobiledoctors.yomna.mobiledoctorstask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by yomna on 3/3/16.
 */
public class Home extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView userInfo = (TextView)findViewById(R.id.userInfo);
        //profilePictureURL
        userInfo.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("profilePictureURL", "Oops, profilePictureURL not found ! "));
      //  userInfo.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userName", "Oops, User name not found ! "));
    }
}