package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private RadioButton oneRadioButton;
    private RadioButton twoRadioButton;
    private RadioButton threeRadioButton;
    private RadioButton fourRadioButton;
    private Spinner citySpinner;
    private TextView searchBar;
    private TextView rentBarTextView;
    private TextView nearMeTextView;
    private SeekBar rentSeekBar;
    private CheckBox nearMeCheckBox;
    private Button searchButton;
    private Button clearButton;
    private Button applyButton;


    private final String TAG = "Debug";
    private final int MAX_RENT = 20000;
    private final int MIN_RENT = 1000;
    ArrayList<String> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        maleRadioButton = findViewById(R.id.maleRaidoButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        oneRadioButton = findViewById(R.id.oneRadioButton);
        twoRadioButton = findViewById(R.id.twoRadioButton);
        threeRadioButton = findViewById(R.id.threeRadioButton);
        fourRadioButton = findViewById(R.id.fourRadioButton);
        citySpinner = findViewById(R.id.citySpinner);
        searchBar = findViewById(R.id.searchBar);
        nearMeTextView = findViewById(R.id.nearMeTextView);
        rentBarTextView = findViewById(R.id.rentBarTextView);
        rentSeekBar = findViewById(R.id.rentSeekBar);
        nearMeCheckBox = findViewById(R.id.nearMeCheckBox);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        applyButton = findViewById(R.id.applyButton);

        rentSeekBar.setMax(MAX_RENT);
        rentSeekBar.setProgress(MAX_RENT);

        nearMeCheckBox.setEnabled(false);

        setupCitySpinner();

        addEventListeners();
    }

    private void addEventListeners(){

        rentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= MIN_RENT){
                    progress = MIN_RENT;
                    rentSeekBar.setProgress(MIN_RENT);
                }
                rentBarTextView.setText("₹"+String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        clearButton.setOnClickListener(v -> {
            nearMeCheckBox.setChecked(false);
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(false);
            oneRadioButton.setChecked(false);
            twoRadioButton.setChecked(false);
            threeRadioButton.setChecked(false);
            fourRadioButton.setChecked(false);
            rentSeekBar.setProgress(MAX_RENT);
            rentBarTextView.setText("₹"+String.valueOf(MAX_RENT));
            searchBar.setText(null);
            citySpinner.setSelection(0);
        });

        nearMeTextView.setOnClickListener(v -> {
            if(!nearMeCheckBox.isEnabled())
                Toast.makeText(this,"Sorry! No Residences in Your Area",Toast.LENGTH_LONG).show();
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!cities.get(position).equalsIgnoreCase(Shared.currentCity))
                    nearMeCheckBox.setChecked(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nearMeCheckBox.setOnClickListener(v -> {
            if(nearMeCheckBox.isChecked())
                citySpinner.setSelection(cities.indexOf(Shared.currentCity),true);
        });

        applyButton.setOnClickListener(v -> {
            String urlExtension = getFilterUrl();
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("urlExtension",urlExtension);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            String urlExtension = getFilterUrl();
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("urlExtension",urlExtension);
            startActivity(intent);
        });

    }

    private String getFilterUrl(){
        Map<String,String> filters = new HashMap<>();
        filters.put("city",citySpinner.getSelectedItem().toString());
        filters.put("rent_lte",String.valueOf(rentSeekBar.getProgress()));

        if(nearMeCheckBox.isChecked())
            filters.put("near",Shared.currentLocation.getLatitude()+","+Shared.currentLocation.getLongitude());

        if(maleRadioButton.isChecked())
            filters.put("gender_label","M");
        else if(femaleRadioButton.isChecked())
            filters.put("gender_label","F");

        if(oneRadioButton.isChecked())
            filters.put("occupancy","1");
        else if(twoRadioButton.isChecked())
            filters.put("occupancy","2");
        else if(threeRadioButton.isChecked())
            filters.put("occupancy","3");
        else if(fourRadioButton.isChecked())
            filters.put("occupancy","4");

        String search = searchBar.getText().toString();
        if(!search.trim().isEmpty())
            filters.put("search",search);

        String urlExtension = "";
        for(String key : filters.keySet())
            urlExtension += key+"="+filters.get(key)+"&";

        return urlExtension.substring(0, urlExtension.length()-1);
    }

    private void setupCitySpinner(){
        StringRequest cityRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/active_cities/names",
                response -> {
                    Log.d(TAG, response);
                    JsonArray jsonArray = new Gson().fromJson(response,JsonArray.class);
                    for(JsonElement element : jsonArray)
                        cities.add(element.getAsString());

                    ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, cities);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(adapter);

                    if(cities.contains(Shared.currentCity)) {
                        nearMeCheckBox.setEnabled(true);
                        citySpinner.setSelection(cities.indexOf(Shared.currentCity));
                    }
                },
                error -> Log.d(TAG, error.toString())
        );
        Shared.requestQueue.add(cityRequest);
    }
}