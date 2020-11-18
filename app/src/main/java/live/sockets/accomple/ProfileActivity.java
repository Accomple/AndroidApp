package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView imageView = findViewById(R.id.menuImageView);
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


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}