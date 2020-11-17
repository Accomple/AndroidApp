package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView menuImageView;
    private ImageView profilePicImageView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navigationHeader;
    private TextView nameTextView;
    private TextView verificationStatusTextView;

    private boolean exitOnBack = false;
    private String token = "EMPTY";
    private final String TAG = "Debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuImageView = findViewById(R.id.menuImageView);
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.sideNav);
        navigationHeader = navigationView.getHeaderView(0);
        nameTextView = navigationHeader.findViewById(R.id.nameTextView);
        verificationStatusTextView = navigationHeader.findViewById(R.id.verificationStatusTextView);
        profilePicImageView  = navigationHeader.findViewById(R.id.profilePicImageView);
        token = Shared.storage.getString("token","EMPTY");

        setupNavigationDrawer();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!token.equalsIgnoreCase("EMPTY"))
            loadProfilePic();

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accommodations/",
                null,
                response -> {
                    Log.d(TAG,response.toString());
                    recyclerView.setAdapter(new AccommodationListAdapter(getApplicationContext(),response));
                },
                error -> {
                    Log.d(TAG, error.toString());
                }
        );


        // add it to the RequestQueue
        Shared.requestQueue.add(getRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (exitOnBack) {
                exitOnBack = false;
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            } else  {
                Toast.makeText(this,"Press back again to exit the app.",Toast.LENGTH_LONG).show();
                exitOnBack = true;
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setupNavigationDrawer(){
        menuImageView.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navigationView.setItemIconTintList(null);
        NavDrawer.setNavigationView(navigationView);
        NavDrawer.setParent(this);
        navigationView.setNavigationItemSelectedListener(NavDrawer.onNavigationItemSelectedListener());
        navigationHeader.setOnClickListener(NavDrawer.onHeaderSelected());
        navigationView.getMenu().getItem(0).setChecked(true);

        String name = Shared.storage.getString("name","EMPTY");
        boolean is_verified = Shared.storage.getBoolean("is_verified",false);
        if(is_verified){
            verificationStatusTextView.setTextColor(Color.parseColor("#4CAF50"));
            verificationStatusTextView.setText("Verified");
        }
        if(!name.equalsIgnoreCase("EMPTY")){
            nameTextView.setText(name);
        }
    }

    private void loadProfilePic(){
        StringRequest profileRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/get_profile/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    try {
                        String profile_pic = jsonObject.get("profile_pic").getAsString();
                        Glide.with(this).load(Shared.ROOT_URL+profile_pic).into(profilePicImageView);

                    } catch (UnsupportedOperationException e){
                        Log.d(TAG, "No Profile Pic");
                    }

                },
                error -> Log.d(TAG, String.valueOf(error.networkResponse.statusCode))
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