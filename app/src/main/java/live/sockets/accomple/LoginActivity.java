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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Debug";
    private TextView skipLink;
    private TextView registerLink;
    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        skipLink = findViewById(R.id.skipLink);
        registerLink = findViewById(R.id.registerLink);
        loginButton = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        addEventListeners();

    }

    private void addEventListeners(){
        skipLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> login());
    }

    private void login(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                Shared.ROOT_URL + "/accounts/login/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    String token = jsonObject.get("token").getAsString();
                    boolean is_owner = Boolean.parseBoolean(jsonObject.get("is_owner").getAsString());
                    boolean is_superuser = Boolean.parseBoolean(jsonObject.get("is_superuser").getAsString());
                    boolean is_verified = Boolean.parseBoolean(jsonObject.get("is_verified").getAsString());
                    String name = jsonObject.get("name").getAsString();

                    if(is_owner || is_superuser){
                        Toast.makeText(getApplicationContext(),"Invalid User Type\n Kindly use Web Portal",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Shared.storage.edit().putString("token",token).apply();
                    Shared.storage.edit().putBoolean("is_verified",is_verified).apply();
                    Shared.storage.edit().putString("name",name).apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                },
                error -> {
                    Toast.makeText(getApplicationContext(),"Invalid Login Details",Toast.LENGTH_LONG).show();
                    Log.d(TAG,String.valueOf(error.networkResponse.statusCode));
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("username", email);
                params.put("password", password);
                return params;
            }
        };
        Shared.requestQueue.add(loginRequest);
    }
}