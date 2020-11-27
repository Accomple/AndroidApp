package live.sockets.accomple;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    private ImageView backImageView;
    private final String TAG = "Debug";
    private String token = "EMPTY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsncondition);
        backImageView = findViewById(R.id.backImageView);
        token = Shared.storage.getString("token","EMPTY");
        addEventListeners();
    }

    private void addEventListeners(){
        backImageView.setOnClickListener(v -> finish());
    }
}
