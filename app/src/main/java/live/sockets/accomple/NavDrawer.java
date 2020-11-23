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
    private static final String TAG = "Debug";

    public static void setNavigationView(NavigationView navigationView) {
        NavDrawer.navigationView = navigationView;
    }

    protected static void setParent(Context parent){
        NavDrawer.parent = parent;
    }

    protected static NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener(){
        return item -> {
            clearSelection(navigationView.getMenu());
            Intent intent;
            switch (item.getItemId()) {
                case R.id.accommodations:
                    Log.d(TAG, "accommodations");
                    intent = new Intent(parent, MainActivity.class);
                    parent.startActivity(intent);
                break;

                case R.id.booking:
                    Log.d(TAG, "booking");
                    String token = Shared.storage.getString("token","EMPTY");
                    if(token.equalsIgnoreCase("EMPTY"))
                        intent = new Intent(parent, LoginActivity.class);
                    else
                        intent = new Intent(parent, BookingActivity.class);
                    parent.startActivity(intent);
                break;
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
            Log.d(TAG, "header");
            String name = Shared.storage.getString("name","EMPTY");
            Intent intent;
            if(name.equalsIgnoreCase("EMPTY"))
                intent = new Intent(parent, LoginActivity.class);
            else
                intent = new Intent(parent, AccountActivity.class);

            parent.startActivity(intent);
        };
    }

    protected static View.OnClickListener onVerificationStatusClicked(){
        return v -> {
            clearSelection(navigationView.getMenu());
            String token = Shared.storage.getString("token","EMPTY");
            boolean is_verified = Shared.storage.getBoolean("is_verified",false);
            if (token.equalsIgnoreCase("EMPTY")){
                Intent intent = new Intent(parent, LoginActivity.class);
                parent.startActivity(intent);
            } else {
                if(is_verified){
                    Toast.makeText(parent,"Verified",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(parent, VerificationActivity.class);
                    parent.startActivity(intent);
                }
            }

        };
    }
}
