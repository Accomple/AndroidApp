package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Debug";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recylerView);
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

}