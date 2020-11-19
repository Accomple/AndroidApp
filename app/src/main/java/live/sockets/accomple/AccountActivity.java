package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import live.sockets.accomple.R;

public class AccountActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private ImageView backImageView;
    private TextView profileNameTextView;
    private TextView statusTextView;
    private Button logoutButton;
    private ConstraintLayout profileLayout;
    private ConstraintLayout activeBookingLayout;
    private ConstraintLayout bookmarksLayout;

    private final String TAG = "Debug";
    private String token = "EMPTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        profileImageView = findViewById(R.id.profileImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        statusTextView = findViewById(R.id.statusTextView);
        profileLayout = findViewById(R.id.profileLayout);
        activeBookingLayout = findViewById(R.id.activeBookingLayout);
        bookmarksLayout = findViewById(R.id.bookmarksLayout);
        backImageView = findViewById(R.id.backImageView);
        logoutButton = findViewById(R.id.logoutButton);
        token = Shared.storage.getString("token","EMPTY");

        requestAndRenderProfileDetails();
        addEventListeners();
    }

    private void addEventListeners(){
        backImageView.setOnClickListener(v -> finish());

        logoutButton.setOnClickListener(v -> {
            Shared.storage.edit().remove("token").apply();
            Shared.storage.edit().remove("is_verified").apply();
            Shared.storage.edit().remove("name").apply();
            StringRequest logoutRequest = new StringRequest(
                    Request.Method.GET,
                    Shared.ROOT_URL+"/accounts/logout/",
                    response -> Log.d(TAG, response),
                    error -> Log.d(TAG, error.toString())
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Token "+token);
                    return params;
                }
            };
            Shared.requestQueue.add(logoutRequest);
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        });
    }

    private void requestAndRenderProfileDetails(){
        StringRequest profileRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/get_profile/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    boolean is_verified = Boolean.parseBoolean(jsonObject.get("is_verified").getAsString());
                    String first_name = jsonObject.get("first_name").getAsString();
                    String last_name = jsonObject.get("last_name").getAsString();
                    String name = first_name+" "+last_name;
                    String profile_pic = jsonObject.get("profile_pic").getAsString();

                    Shared.storage.edit().putBoolean("is_verified",is_verified).apply();
                    Shared.storage.edit().putString("name",name).apply();

                    profileNameTextView.setText(name);
                    if(is_verified){
                        statusTextView.setTextColor(Color.parseColor("#4CAF50"));
                        statusTextView.setText("Verified");
                    }
                    Glide.with(this).load(Shared.ROOT_URL+profile_pic).into(profileImageView);
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

        Shared.requestQueue.add(profileRequest);
    }
}