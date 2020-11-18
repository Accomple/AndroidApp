package live.sockets.accomple;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;

public abstract class Shared {
    protected static final String ROOT_URL = "http://api.accomple.sockets.live";
//    protected static final String ROOT_URL = "http://10.42.0.1";
    protected static RequestQueue requestQueue;
    protected static SharedPreferences storage;
}
