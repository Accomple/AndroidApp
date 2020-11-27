package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class BuildingDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView favouriteImageView;
    private ImageView backImageView;
    private ImageView toggleAboutImageView;
    private RecyclerView photoRecyclerView;
    private RecyclerView roomsRecyclerView;
    private LinearLayout layoutToolbar;
    private MapView mapView;
    private TextView genderTextView;
    private TextView titleTextView;
    private TextView aboutTextView;

    private GoogleMap map;
    private final String TAG = "Debug";
    private final int UNI_SHADE = Color.parseColor("#828282");
    private final int MALE_SHADE = Color.parseColor("#673AB7");
    private final int FEMALE_SHADE = Color.parseColor("#F05A78");
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyC3Z1BJHsA3nuLp2ttSbZwrZmAPDJnZcBM";
    private boolean toggledAbout = false;
    private String id;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        layoutToolbar = findViewById(R.id.layoutToolbar);
        genderTextView = findViewById(R.id.genderTextView);
        titleTextView = findViewById(R.id.titleTextView);
        favouriteImageView = findViewById(R.id.favouriteImageView);
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        aboutTextView = findViewById(R.id.aboutTextView);

        backImageView = findViewById(R.id.backImageView);
        toggleAboutImageView = findViewById(R.id.toggleAboutImageView);
        token = Shared.storage.getString("token","EMPTY");

        toggleAboutImageView.setImageResource(R.drawable.keyboard_arrow_up);
        aboutTextView.setText(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            aboutTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        id = getIntent().getStringExtra("id");
        Log.d(TAG, id);

        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        requestAndRender();

        addEventListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void addEventListeners(){

        favouriteImageView.setOnLongClickListener(v -> {
            favouriteImageView.setImageResource(R.drawable.favorite_filled);
            return true;
        });

        backImageView.setOnClickListener(v -> finish());

        toggleAboutImageView.setOnClickListener(v -> {
            toggledAbout = !toggledAbout;
            if(toggledAbout) {
                toggleAboutImageView.setImageResource(R.drawable.keyboard_arrow_down);
                aboutTextView.setText(R.string.about_text);
            } else {
                toggleAboutImageView.setImageResource(R.drawable.keyboard_arrow_up);
                aboutTextView.setText(null);
            }
        });
    }

    private void setShade(String genderLabel){
        switch (genderLabel){
            case "M":
                layoutToolbar.setBackgroundColor(MALE_SHADE);
                genderTextView.setBackgroundColor(MALE_SHADE);
                genderTextView.setText("MALE RESIDENCE");
            break;
            case "F":
                layoutToolbar.setBackgroundColor(FEMALE_SHADE);
                genderTextView.setBackgroundColor(FEMALE_SHADE);
                genderTextView.setText("FEMALE RESIDENCE");
            break;
            default:
                layoutToolbar.setBackgroundColor(UNI_SHADE);
                genderTextView.setBackgroundColor(UNI_SHADE);
                genderTextView.setText("UNI RESIDENCE");
        }
    }
    private void requestAndRender(){

        StringRequest detailRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accommodations/building/get/id="+id+"/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    JsonArray photoObjects = jsonObject.getAsJsonArray("photos");
                    JsonArray photoUrls = new JsonArray();
                    JsonArray rooms = new JsonArray();

                    photoUrls.add(jsonObject.get("display_pic").getAsString());
                    for(JsonElement element : photoObjects){
                        JsonObject object = element.getAsJsonObject();
                        photoUrls.add(object.get("photo").getAsString());
                    }
                    double lat = jsonObject.get("latitude").getAsDouble();
                    double lng = jsonObject.get("longitude").getAsDouble();
                    String gender_label = jsonObject.get("gender_label").getAsString();
                    String building_name = jsonObject.get("building_name").getAsString();
                    rooms = jsonObject.get("rooms").getAsJsonArray();

                    setShade(gender_label);
                    titleTextView.setText(building_name);
                    photoRecyclerView.setAdapter(new PhotosAdapter(getApplicationContext(),photoUrls));
                    roomsRecyclerView.setAdapter(new RoomAdapter(getApplicationContext(),rooms));
                    map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(building_name));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 11));

                },
                error -> Log.d(TAG, error.toString())
        ){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String>  params = new HashMap<>();
                if(!token.equalsIgnoreCase("EMPTY"))
                    params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(detailRequest);

    }
}