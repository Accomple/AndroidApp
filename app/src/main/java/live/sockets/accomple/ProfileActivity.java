package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePicView;
    private ImageView uploadIcon;
    private ImageView backImageView;
    private ImageView calendarIcon;
    private EditText emailEditField;
    private EditText firstNameEditField;
    private EditText lastNameEditField;
    private EditText phoneNumberEditField;
    private EditText dateOfBirthEditField;
    private Button applyChangesButton;

    private final String TAG = "Debug";
    private String token = "EMPTY";
    private final int PICK_IMAGE_REQUEST = 111;
    private final Calendar calendar = Calendar.getInstance();
    private Bitmap profilePic = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicView = findViewById(R.id.profilePicView);
        uploadIcon = findViewById(R.id.uploadIcon);
        backImageView = findViewById(R.id.backImageView);
        calendarIcon = findViewById(R.id.calendarIcon);
        emailEditField = findViewById(R.id.emailEditField);
        firstNameEditField = findViewById(R.id.firstNameEditField);
        lastNameEditField = findViewById(R.id.lastNameEditField);
        phoneNumberEditField = findViewById(R.id.phoneNumberEditField);
        dateOfBirthEditField = findViewById(R.id.dateOfBirthEditField);
        applyChangesButton = findViewById(R.id.applyChangesButton);
        token = Shared.storage.getString("token","EMPTY");

        emailEditField.setEnabled(false);
        dateOfBirthEditField.setFocusable(false);

        requestAndRenderProfileDetails();

        addEventListeners();
    }


    private void requestAndRenderProfileDetails(){
        StringRequest profileRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/get_profile/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    String email = jsonObject.get("username").getAsString();
                    String first_name = jsonObject.get("first_name").getAsString();
                    String last_name = jsonObject.get("last_name").getAsString();
                    String phone_number = jsonObject.get("phone_number").getAsString();
                    String date_of_birth = jsonObject.get("date_of_birth").getAsString();
                    String profile_pic = jsonObject.get("profile_pic").getAsString();

                    emailEditField.setText(email);
                    firstNameEditField.setText(first_name);
                    lastNameEditField.setText(last_name);
                    phoneNumberEditField.setText(phone_number);
                    dateOfBirthEditField.setText(date_of_birth);


                    Glide.with(this).load(Shared.ROOT_URL+profile_pic).into(profilePicView);
                },
                error -> Log.d(TAG, error.toString())
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(profileRequest);
    }

    private void addEventListeners(){

        backImageView.setOnClickListener(v -> finish());

        profilePicView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        });

        uploadIcon.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        });

    }

    public void pickDate(View v){
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateOfBirthEditField.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            try {
                //getting image from gallery
                Uri filePath = data.getData();
                Log.d(TAG, filePath.toString());
                profilePic = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                profilePicView.setImageBitmap(profilePic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void applyChanges(View v){
        String first_name = firstNameEditField.getText().toString();
        String last_name = lastNameEditField.getText().toString();
        String phone_number = phoneNumberEditField.getText().toString();
        String date_of_birth = dateOfBirthEditField.getText().toString();

        MultipartRequest profileUpdate = new MultipartRequest(
                Request.Method.PUT,
                Shared.ROOT_URL + "/accounts/profile/update/user=me/",
                response -> {
                    Log.d(TAG, new String(response.data));
                    Shared.storage.edit().putString("name",first_name+" "+last_name).apply();
                    Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                },
                error -> {
                    Log.d(TAG,new String(error.networkResponse.data));
                    if(error.networkResponse.statusCode == 400)
                        Toast.makeText(getApplicationContext(),"Invalid Fields for Profile",Toast.LENGTH_LONG).show();
                    else if (error.networkResponse.statusCode == 413)
                        Toast.makeText(getApplicationContext(),"Request Entity Too Large",Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(),"Something Went Wrong!",Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("phone_number", phone_number);
                params.put("date_of_birth", date_of_birth);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if(profilePic != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    profilePic.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    byte[] imageBytes = outputStream.toByteArray();
                    long imageName = System.currentTimeMillis();
                    params.put("profile_pic", new DataPart(imageName + ".jpg",imageBytes));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(profileUpdate);


    }
}