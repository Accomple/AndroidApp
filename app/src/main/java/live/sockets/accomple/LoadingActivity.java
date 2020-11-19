package live.sockets.accomple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class LoadingActivity extends AppCompatActivity {
    ImageView loadingImageView;

    private final String TAG = "Debug";
    private int readStoragePermission;
    private int fineLocationPermission;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Shared.storage = getSharedPreferences("live.sockets.notes",MODE_PRIVATE);
        Shared.requestQueue = Volley.newRequestQueue(this);
        loadingImageView = findViewById(R.id.loadingImageView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = location -> {
            try {
                Log.d(TAG, String.valueOf(location));
                Shared.currentLocation = location;
                setCurrentCity(location);

            } catch (Exception e) {
                Log.d(TAG, e.toString());
                redirect();
            }
        };

        Shared.currentCity = "Pune";
        Shared.currentLocation = new Location(LocationManager.GPS_PROVIDER);
        Shared.currentLocation.setLatitude(18.5247663);
        Shared.currentLocation.setLongitude(73.7927557);

        Glide.with(this).load(R.drawable.loading).into(loadingImageView);

        requestPermissions();
    }

    private void redirect(){
        String token = Shared.storage.getString("token","EMPTY");
        Intent intent;
        if(token.equalsIgnoreCase("EMPTY"))
            intent = new Intent(this, LoginActivity.class);
         else
            intent = new Intent(this, MainActivity.class);

         startActivity(intent);
    }

    private void requestPermissions(){
        readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        fineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (readStoragePermission != PackageManager.PERMISSION_GRANTED || fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION },73);
        }else{
            Log.d(TAG, "All Permissions Acquired");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5000, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==73 && grantResults.length==2){
            readStoragePermission = grantResults[0];
            fineLocationPermission = grantResults[1];
            if(readStoragePermission != PackageManager.PERMISSION_GRANTED)
                finishAndRemoveTask();
            if(fineLocationPermission != PackageManager.PERMISSION_GRANTED)
                finishAndRemoveTask();
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5000, locationListener);
        }
    }

    private void setCurrentCity(Location location) throws Exception{
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        String postalCode = addresses.get(0).getPostalCode();

        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://api.postalpincode.in/pincode/"+postalCode,
                null,
                response -> {
                    try {
                        JSONObject postOffice = response.getJSONObject(0).getJSONArray("PostOffice").getJSONObject(0);
                        Shared.currentCity = postOffice.getString("District");
                        Log.d(TAG, Shared.currentCity);
                        redirect();
                    } catch (Exception e) {
                        Shared.currentCity = "Pune";
                        e.printStackTrace();
                        redirect();
                    }

                },
                error -> Log.d(TAG, error.toString())
        );

        Shared.requestQueue.add(getRequest);
    }
}