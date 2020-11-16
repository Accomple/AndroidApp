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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navigationHeader;

    private boolean exitOnBack = false;

    private final String TAG = "Debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.menuImageView);
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.sideNav);
        navigationHeader = navigationView.getHeaderView(0);

        setupNavigationDrawer();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void setupNavigationDrawer(){
        imageView.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navigationView.setItemIconTintList(null);
        NavDrawer.setNavigationView(navigationView);
        NavDrawer.setParent(this);
        navigationView.setNavigationItemSelectedListener(NavDrawer.onNavigationItemSelectedListener());
        navigationHeader.setOnClickListener(NavDrawer.onHeaderSelected());
        navigationView.getMenu().getItem(0).setChecked(true);
    }
}