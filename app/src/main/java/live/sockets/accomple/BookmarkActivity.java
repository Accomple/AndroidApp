package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.HashMap;
import java.util.Map;

public class BookmarkActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView backImageView;
    private TextView noBookMarksTextView;

    private String token = "EMPTY";
    private final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.recyclerView);
        backImageView = findViewById(R.id.backImageView);
        noBookMarksTextView = findViewById(R.id.noBookMarksTextView);
        token = Shared.storage.getString("token","EMPTY");

        noBookMarksTextView.animate().translationYBy(-10000).setDuration(0);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/bookmarks/get/user=me/",
                null,
                response -> {
                    Log.d(TAG,response.toString());
                    if(response.length() == 0)
                        noBookMarksTextView.animate().translationYBy(10000).setDuration(0);
                    recyclerView.setAdapter(new BookmarksAdapter(getApplicationContext(),response));
                },
                error -> Log.d(TAG, error.toString())
        ){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(getRequest);

        addEventListeners();
    }

    public void addEventListeners(){

        backImageView.setOnClickListener(v -> finish());
    }
}