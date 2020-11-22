package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView bookingNoTextView;
    private TextView ownerNameTextView;
    private TextView ownerEmailTextView;
    private TextView ownerContactTextView;
    private TextView bookingTimeTextView;
    private TextView buildingNameTextView;
    private TextView areaTextView;
    private TextView genderLabelTextView;
    private TextView starPerksTextView;
    private TextView roomTitleTextView;
    private TextView occupancyTextView;
    private TextView rentTextView;
    private ImageView backImageView;
    private ImageView buildingImageView;
    private Button cancelButton;
    private CardView buildingCardView;

    private final String TAG = "Debug";
    private String token = "EMPTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingNoTextView = findViewById(R.id.bookingNoTextView);
        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        ownerEmailTextView = findViewById(R.id.ownerEmailTextView);
        ownerContactTextView = findViewById(R.id.ownerContactTextView);
        bookingTimeTextView = findViewById(R.id.bookingTimeTextView);
        buildingNameTextView = findViewById(R.id.bulidingNameTextView);
        areaTextView = findViewById(R.id.areaTextView);
        genderLabelTextView = findViewById(R.id.genderLabelTextView);
        starPerksTextView = findViewById(R.id.starPerksTextView);
        roomTitleTextView = findViewById(R.id.roomTitleTextView);
        occupancyTextView = findViewById(R.id.occupancyTextView);
        rentTextView = findViewById(R.id.rentTextView);
        backImageView = findViewById(R.id.backImageView);
        buildingImageView = findViewById(R.id.buildingImageView);
        cancelButton = findViewById(R.id.cancelButton);
        buildingCardView = findViewById(R.id.buildingCardView);
        token = Shared.storage.getString("token","EMPTY");

        requestAndRender();

        addEventListeners();

    }

    private void requestAndRender(){

        StringRequest bookingRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/booking/get/user=me/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    JsonObject building = jsonObject.getAsJsonObject("building");
                    String display_pic = building.get("display_pic").getAsString();
                    Glide.with(this).load(Shared.ROOT_URL+display_pic).into(buildingImageView);
                },
                error -> Log.d(TAG, error.toString())
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(bookingRequest);
    }

    private void addEventListeners(){

        backImageView.setOnClickListener(v -> finish());


    }
}