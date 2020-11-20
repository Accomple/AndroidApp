package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Spinner citySpinner;

    private String TAG = "Debug";
    ArrayList<String> cities = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        maleRadioButton = findViewById(R.id.maleRaidoButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        citySpinner = findViewById(R.id.citySpinner);


        cities.add("Pune");
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

    }


}