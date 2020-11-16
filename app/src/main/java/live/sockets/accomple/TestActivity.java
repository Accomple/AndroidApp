package live.sockets.accomple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import live.sockets.accomple.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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
}