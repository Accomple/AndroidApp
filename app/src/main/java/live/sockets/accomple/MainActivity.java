package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView imageView;

    private boolean exitOnBack = false;

    private final String TAG = "Debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.menuImageView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        imageView.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        NavigationView navigationView = findViewById(R.id.sideNav);
        NavDrawer.setNavigationView(navigationView);
        NavDrawer.setParent(this);

        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(NavDrawer.onNavigationItemSelectedListener());
        View head = navigationView.getHeaderView(0);
        head.setOnClickListener(NavDrawer.onHeaderSelected());


        Shared.requestQueue = Volley.newRequestQueue(this);  // this = context

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
}