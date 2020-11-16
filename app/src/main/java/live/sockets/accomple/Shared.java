package live.sockets.accomple;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;

public abstract class Shared {
    protected static final String ROOT_URL = "http://192.168.0.180:8000";
    protected static RequestQueue requestQueue;
    protected static SharedPreferences storage;
}
