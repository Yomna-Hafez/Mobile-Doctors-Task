package mobiledoctors.yomna.mobiledoctorstask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yomna on 3/3/16.
 */
public class Home extends FragmentActivity {
    private DrawerLayout drawerLayout;
    private ListView listCategories;
    int PLACE_PICKER_REQUEST = 1;
    String currentLocation;
    Double shortestDistance;
    double currentLat, currentLng;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listCategories = (ListView) findViewById(R.id.listCategories);

        initializeMap();
        initializeDrawerHeader();
        initializeDrawerList();
        isGpsEnabled();

        listCategories.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView itemText = (TextView) view.findViewById(R.id.title);
                String choosenCategory = itemText.getText().toString().toLowerCase();
                drawerLayout.closeDrawers();
                getNearbyPlaces(choosenCategory);
            }
        });

    }

    private void initializeMap() {

        map.setMyLocationEnabled(true);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                currentLocation = location.getLatitude() + "," + location.getLongitude();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                map.addMarker(new MarkerOptions()
                        .position(loc)
                        .title("Hello Mobile Doctors!"));

            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);

//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//        try {
//            startActivityForResult(builder.build(getApplicationContext()), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
    }

    ;

    private void getNearbyPlaces(String category) {
        if (isNetworkEnabled()) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocation + "&radius=500&type=" + category + "&key=AIzaSyCfJDlN8lXcLCh8H0f8G62ZQM0Qj9N0e9Y";
            Ion.with(getApplicationContext())
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                map.clear();
                                try {
                                    String res = result.toString();
                                    JSONObject resultObj = new JSONObject(res);
                                    JSONArray data = resultObj.getJSONArray("results");
                                    if (data.length() < 1) {
                                        Toast.makeText(getApplicationContext(), "Sorry, no places here",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject locationData = data.getJSONObject(i);
                                            double lat = (double) locationData.getJSONObject("geometry").getJSONObject("location").get("lat");
                                            double lng = (double) locationData.getJSONObject("geometry").getJSONObject("location").get("lng");
                                            String name = locationData.get("name").toString();
                                            drawNearbyPlaces(lat, lng, name);
                                        }

                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    ;


    private void drawNearbyPlaces(double destinationLat, double destinationLng, String placeName) {
        LatLng loc = new LatLng(destinationLat, destinationLng);
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(placeName));

        // todo call find shortest route - bonus
        // calculateDistance(currentLat, currentLng, destinationLat, destinationLng );
    }

    ;


    private void calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
//        Toast.makeText(getApplicationContext(), "distance : " + dist,
//                Toast.LENGTH_LONG).show();

        // return (dist);
//        if (shortestDistance == null){
//            shortestDistance = dist;
//        }else{
//            if(dist < shortestDistance){
//
//            }
//        }

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private void drawSortestPath(double destinationLat, double destinationLng) {

    }

    ;

    private void initializeDrawerHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        userName.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userName", "Oops, User name not found ! "));
        ImageView profilePicture = (ImageView) findViewById(R.id.ivProfilePicture);
        Picasso.with(this)
                .load(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("profilePictureURL", ""))
                .placeholder(R.drawable.test1)
                .error(R.drawable.test1)
                .into(profilePicture);
    }

    ;


    private void initializeDrawerList() {

        NavDrawerItem listData[] = new NavDrawerItem[]
                {
                        new NavDrawerItem(R.drawable.resturant, "Resturant"),
                        new NavDrawerItem(R.drawable.museum, "Museum"),
                        new NavDrawerItem(R.drawable.school, "School"),
                        new NavDrawerItem(R.drawable.spa, "Spa"),

                };

        DrawerListAdapter adapter = new DrawerListAdapter(this,
                R.layout.drawer_list_item, listData);
        listCategories.setAdapter(adapter);
    }

    ;


    private boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            Toast.makeText(getApplicationContext(), "Please enable your location service", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    ;

    private boolean isNetworkEnabled() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Please enable network connection", Toast.LENGTH_LONG).show();
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
            }
        }
    }

    ;


    // `onPostCreate` called when activity start-up is complete after `onStart()`
// NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}