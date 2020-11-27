package live.sockets.accomple;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView bookingNoTextView;
    private TextView ownerNameTextView;
    private TextView ownerEmailTextView;
    private TextView ownerContactTextView;
    private TextView bookingTimeTextView;
    private TextView buildingNameTextView;
    private TextView cityTextView;
    private TextView areaTextView;
    private TextView genderLabelTextView;
    private TextView roomTitleTextView;
    private TextView occupancyTextView;
    private TextView rentTextView;
    private ImageView backImageView;
    private ImageView buildingImageView;
    private Button cancelButton;
    private CardView buildingCardView;
    private CardView bookingCardView;

    boolean exists = false;
    int building_id = 0;
    String booking_no = "0";
    private final String TAG = "Debug";
    private String token = "EMPTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingNoTextView = findViewById(R.id.bookingNoTextView);
        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        ownerEmailTextView = findViewById(R.id.ownerEmailTextView);
        ownerContactTextView = findViewById(R.id.ownerContactTextView);
        bookingTimeTextView = findViewById(R.id.bookingTimeTextView);
        buildingNameTextView = findViewById(R.id.bulidingNameTextView);
        areaTextView = findViewById(R.id.areaTextView);
        cityTextView = findViewById(R.id.cityTextView);
        genderLabelTextView = findViewById(R.id.genderLabelTextView);
        roomTitleTextView = findViewById(R.id.roomTitleTextView);
        occupancyTextView = findViewById(R.id.occupancyTextView);
        rentTextView = findViewById(R.id.rentTextView);
        backImageView = findViewById(R.id.backImageView);
        buildingImageView = findViewById(R.id.buildingImageView);
        cancelButton = findViewById(R.id.cancelButton);
        buildingCardView = findViewById(R.id.buildingCardView);
        bookingCardView = findViewById(R.id.bookingCardView);
        token = Shared.storage.getString("token","EMPTY");

        bookingCardView.animate().scaleX(0).setDuration(0);
        buildingCardView.animate().scaleX(0).setDuration(0);
        cancelButton.animate().scaleX(0).setDuration(0);
        bookingCardView.setEnabled(false);
        buildingCardView.setEnabled(false);
        cancelButton.setEnabled(false);

        requestAndRender();
        addEventListeners();

    }

    private void requestAndRender(){

        StringRequest bookingRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/booking/get/user=me/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    exists = jsonObject.get("exists").getAsBoolean();

                    if(exists) {
                        bookingCardView.animate().scaleX(1).setDuration(0);
                        buildingCardView.animate().scaleX(1).setDuration(0);
                        cancelButton.animate().scaleX(1).setDuration(0);
                        bookingCardView.setEnabled(true);
                        buildingCardView.setEnabled(true);
                        cancelButton.setEnabled(true);

                        JsonObject booking = jsonObject.getAsJsonObject("booking");
                        JsonObject building = jsonObject.getAsJsonObject("building");
                        JsonObject room = jsonObject.getAsJsonObject("room");
                        JsonObject owner = jsonObject.getAsJsonObject("owner");

                        building_id = building.get("id").getAsInt();
                        booking_no = booking.get("booking_no").getAsString();
                        String owner_name = owner.get("first_name").getAsString()+" "+owner.get("last_name").getAsString();
                        String owner_email = owner.get("email").getAsString();
                        String owner_contact = owner.get("phone_number").getAsString();
                        String date_string = booking.get("booking_date").getAsString();
                        int rent = room.get("rent").getAsInt();
                        String occupancy = room.get("occupancy").getAsString();
                        String room_title = room.get("title").getAsString();
                        String area = building.get("area").getAsString();
                        String city = building.get("city").getAsString();
                        String display_pic = building.get("display_pic").getAsString();
                        String building_name = building.get("building_name").getAsString();
                        String gender_label = building.get("gender_label").getAsString();

                        bookingNoTextView.setText(booking_no);
                        ownerNameTextView.setText(owner_name);
                        ownerEmailTextView.setText(owner_email);
                        ownerContactTextView.setText(owner_contact);
                        rentTextView.setText("Rent @ Rs."+rent+"/mo");
                        occupancyTextView.setText(occupancy);
                        roomTitleTextView.setText(room_title);
                        cityTextView.setText(city);
                        areaTextView.setText(area);
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            Date date = inputFormat.parse(date_string);
                            bookingTimeTextView.setText(date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            bookingTimeTextView.setText(date_string);
                        }
                        buildingNameTextView.setText(building_name);
                        if(gender_label.equalsIgnoreCase("M"))
                            genderLabelTextView.setText("Male");
                        if(gender_label.equalsIgnoreCase("F"))
                            genderLabelTextView.setText("Female");
                        if(gender_label.equalsIgnoreCase("U"))
                            genderLabelTextView.setText("Male/Female");
                        Glide.with(this).load(Shared.ROOT_URL + display_pic).into(buildingImageView);
                    }
                },
                error -> Toast.makeText(this,"Something Went Wrong!",Toast.LENGTH_LONG).show()
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(bookingRequest);
    }

    private void addEventListeners(){

        backImageView.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this booking?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteBooking())
                    .setNegativeButton("No",null)
                    .show();
        });

        ownerContactTextView.setOnClickListener(v -> {
            String contact = ownerContactTextView.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+contact));
            startActivity(intent);
        });

        ownerEmailTextView.setOnClickListener(v -> {
            try{
                String email = ownerEmailTextView.getText().toString();
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:"+email));
                startActivity(intent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(this,"Couldn't Launch Gmail App",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteBooking(){
        if(!exists) return;
        StringRequest deleteBooking = new StringRequest(
                Request.Method.DELETE,
                Shared.ROOT_URL+"/accommodations/booking/delete/id="+booking_no+"/",
                response -> {
                    Log.d(TAG, response);
                    bookingCardView.animate().scaleX(0).setDuration(400);
                    buildingCardView.animate().scaleX(0).setDuration(400);
                    cancelButton.animate().scaleX(0).setDuration(0);
                    bookingCardView.setEnabled(false);
                    buildingCardView.setEnabled(false);
                    cancelButton.setEnabled(false);
                    Toast.makeText(this,"Booking Cancelled",Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(this,"Something Went Wrong!",Toast.LENGTH_LONG).show()

        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(deleteBooking);
    }
}