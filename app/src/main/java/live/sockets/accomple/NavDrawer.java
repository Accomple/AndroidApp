package live.sockets.accomple;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

public abstract class NavDrawer {

    protected static Context parent;
    protected static NavigationView navigationView;

    public static void setNavigationView(NavigationView navigationView) {
        NavDrawer.navigationView = navigationView;
    }

    protected static void setParent(Context parent){
        NavDrawer.parent = parent;
    }

    protected static NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener(){
        return item -> {
            clearSelection(navigationView.getMenu());
            item.setChecked(true);
            if(item.getItemId() == R.id.item1) {
                Intent intent = new Intent(parent,ProfileActivity.class);
                parent.startActivity(intent);
                Toast.makeText(parent,"item1",Toast.LENGTH_SHORT).show();
                Log.d("TAG","item1");
            }
            return true;
        };
    }

    protected static void clearSelection(Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                // Un check sub menu items
                clearSelection(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    protected static View.OnClickListener onHeaderSelected(){
        return v -> {
            clearSelection(navigationView.getMenu());
            Toast.makeText(parent,"header",Toast.LENGTH_SHORT).show();
        };
    }
}
