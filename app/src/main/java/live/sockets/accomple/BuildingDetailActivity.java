package live.sockets.accomple;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
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
    private RecyclerView perksRecyclerView;
    private RecyclerView roomsRecyclerView;
    private LinearLayout layoutToolbar;
    private MapView mapView;
    private TextView genderTextView;
    private TextView startingRentTextView;
    private TextView titleTextView;
    private TextView aboutTextView;
    private TextView streetTextView;
    private TextView areaTextView0;
    private TextView landmarkTextView;
    private TextView cityTextView0;
    private TextView stateTextView;

    private GoogleMap map;
    private final String TAG = "Debug";
    private final int UNI_SHADE = Color.parseColor("#828282");
    private final int MALE_SHADE = Color.parseColor("#673AB7");
    private final int FEMALE_SHADE = Color.parseColor("#F05A78");
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyC3Z1BJHsA3nuLp2ttSbZwrZmAPDJnZcBM";
    private boolean toggledAbout = false;
    private boolean is_bookmarked = false;
    private String id;
    private int starting_rent;
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
        perksRecyclerView = findViewById(R.id.perksRecyclerView);
        streetTextView = findViewById(R.id.streetTextView);
        areaTextView0 = findViewById(R.id.areaTextView0);
        landmarkTextView = findViewById(R.id.landmarkTextView);
        cityTextView0 = findViewById(R.id.cityTextView0);
        stateTextView = findViewById(R.id.stateTextView);
        aboutTextView = findViewById(R.id.aboutTextView);
        startingRentTextView = findViewById(R.id.startingRentTextView);
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
        starting_rent = getIntent().getIntExtra("starting_rent",7299);

        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        perksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


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
            is_bookmarked = !is_bookmarked;
            if (is_bookmarked) {
                favouriteImageView.setImageResource(R.drawable.favorite_filled);
                StringRequest addBookmarkRequest = new StringRequest(
                        Request.Method.POST,
                        Shared.ROOT_URL+"/accounts/bookmark/add/building_id="+id+"/",
                        response -> {
                            Toast.makeText(this, "Added Bookmark", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, response);
                        },
                        error -> {
                            Log.d(TAG, error.toString());
                            Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();
                        params.put("Authorization", "Token "+token);
                        return params;
                    }
                };
                Shared.requestQueue.add(addBookmarkRequest);
            } else {
                favouriteImageView.setImageResource(R.drawable.favorite_bordered);
                StringRequest removeBookmarkRequest = new StringRequest(
                        Request.Method.DELETE,
                        Shared.ROOT_URL+"/accounts/bookmark/delete/building_id="+id+"/",
                        response -> {
                            Toast.makeText(this, "Removed Bookmark", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, response);
                        },
                        error -> {
                            Log.d(TAG, error.toString());
                            Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();
                        params.put("Authorization", "Token "+token);
                        return params;
                    }
                };
                Shared.requestQueue.add(removeBookmarkRequest);
            }
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
                    JsonArray perks;

                    photoUrls.add(jsonObject.get("display_pic").getAsString());
                    for(JsonElement element : photoObjects){
                        JsonObject object = element.getAsJsonObject();
                        photoUrls.add(object.get("photo").getAsString());
                    }
                    for(JsonElement element : jsonObject.get("rooms").getAsJsonArray()){
                        JsonObject object = element.getAsJsonObject();
                        if(object.get("available").getAsInt() > 0)
                            rooms.add(object);
                    }
                    perks = jsonObject.getAsJsonArray("perks");
                    double lat = jsonObject.get("latitude").getAsDouble();
                    double lng = jsonObject.get("longitude").getAsDouble();
                    is_bookmarked = jsonObject.get("is_bookmarked").getAsBoolean();
                    String gender_label = jsonObject.get("gender_label").getAsString();
                    String building_name = jsonObject.get("building_name").getAsString();
                    String street = jsonObject.get("street").getAsString();
                    String area = jsonObject.get("area").getAsString();
                    String city = jsonObject.get("city").getAsString();
                    String state = jsonObject.get("state").getAsString();
                    String zip_code = jsonObject.get("zip_code").getAsString();
                    String landmark = jsonObject.get("landmark").getAsString();

                    setShade(gender_label);
                    titleTextView.setText(building_name);
                    if(is_bookmarked)
                        favouriteImageView.setImageResource(R.drawable.favorite_filled);
                    else
                        favouriteImageView.setImageResource(R.drawable.favorite_bordered);
                    startingRentTextView.setText("₹ "+starting_rent+" ");
                    photoRecyclerView.setAdapter(new PhotosAdapter(getApplicationContext(),photoUrls));
                    roomsRecyclerView.setAdapter(new RoomAdapter(getApplicationContext(),rooms));
                    perksRecyclerView.setAdapter(new PerkAdapter(getApplicationContext(),perks));
                    streetTextView.setText(street+",");
                    areaTextView0.setText(area+",");
                    landmarkTextView.setText("Near "+landmark+",");
                    cityTextView0.setText(city+", "+zip_code+",");
                    stateTextView.setText(state);
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