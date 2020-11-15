package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    final String ROOT_URL = "http://192.168.0.180:8000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                ROOT_URL+"accommodations/",
                null,
                response -> {
                    Log.d("Response", response.toString());
                    render(response);
                },
                error -> {
                    Log.d("Error", error.toString());
                    TextView textView = findViewById(R.id.textView);
                    textView.setText("Something Went Wrong");

                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void render(JSONArray response){
        TextView textView = findViewById(R.id.textView);
        try{
            String json = "";
            for(int i=0; i<response.length(); i++){
                json += "Accommodation: "+(i+1)+"\n";
                JSONObject object = response.getJSONObject(i);
                Iterator <String> keys = object.keys();

                while (keys.hasNext()){
                    String key = keys.next();
                    json += "\t"+key+": "+object.get(key).toString()+"\n";
                }
                json += "\n";
            }
            textView.setText(json);
        } catch (JSONException e){
            System.out.println(e.toString());
        }
    }
}