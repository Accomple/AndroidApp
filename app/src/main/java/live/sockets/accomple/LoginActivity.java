package live.sockets.accomple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Debug";
    private TextView skipLink;
    private TextView registerLink;
    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;

    private int readStoragePermission;
    private int fineLocationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Shared.storage = getSharedPreferences("live.sockets.notes",MODE_PRIVATE);
        Shared.requestQueue = Volley.newRequestQueue(this);

        skipLink = findViewById(R.id.skipLink);
        registerLink = findViewById(R.id.registerLink);
        loginButton = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        requestPermissions();

        String token = Shared.storage.getString("token","EMPTY");
        if(!token.equalsIgnoreCase("EMPTY")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        addEventListeners();
    }

    private void requestPermissions(){
        readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        fineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (readStoragePermission != PackageManager.PERMISSION_GRANTED || fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION },73);
        }else Log.d(TAG, "All Permissions Granted");
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
        }
    }

    private void addEventListeners(){
        skipLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
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
                    boolean is_verified = Boolean.parseBoolean(jsonObject.get("is_verified").getAsString());
                    String name = jsonObject.get("name").getAsString();

                    Shared.storage.edit().putString("token",token).apply();
                    Shared.storage.edit().putBoolean("is_owner",is_owner).apply();
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