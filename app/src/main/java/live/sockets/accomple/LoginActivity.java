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
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Debug";
    private TextView skipLink;
    private int readStoragePermission;
    private int fineLocationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        skipLink = findViewById(R.id.skipLink);

        requestPermissions();
        Shared.storage = getSharedPreferences("live.sockets.notes",MODE_PRIVATE);
        String token = Shared.storage.getString("Token","EMPTY");
        if(!token.equalsIgnoreCase("EMPTY")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        skipLink = findViewById(R.id.skipLink);
        skipLink.setOnClickListener(v -> finishAndRemoveTask());
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
}