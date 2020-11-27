package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{
    private ImageView profilePicView;
    private ImageView uploadIcon;
    private ImageView backImageView;
    private ImageView calendarIcon;
    private EditText emailEditField;
    private EditText firstNameEditField;
    private EditText lastNameEditField;
    private EditText phoneNumberEditField;
    private EditText dateOfBirthEditField;
    private EditText passwordEditField;
    private EditText confPassEditField;
    private Button proceedButton;
    private TextView infoTextView;

    private final String TAG = "Debug";
    private String token = "EMPTY";
    private final int PICK_IMAGE_REQUEST = 111;
    private final Calendar calendar = Calendar.getInstance();
    private Bitmap profilePic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profilePicView = findViewById(R.id.profilePicView);
        uploadIcon = findViewById(R.id.uploadIcon);
        backImageView = findViewById(R.id.backImageView);
        calendarIcon = findViewById(R.id.calendarIcon);
        emailEditField = findViewById(R.id.emailEditField);
        firstNameEditField = findViewById(R.id.firstNameEditField);
        lastNameEditField = findViewById(R.id.lastNameEditField);
        phoneNumberEditField = findViewById(R.id.phoneNumberEditField);
        dateOfBirthEditField = findViewById(R.id.dateOfBirthEditField);
        passwordEditField=findViewById(R.id.passwordEditField);
        confPassEditField=findViewById(R.id.confpasswordEditField);
        proceedButton = findViewById(R.id.proceedButton);
        infoTextView = findViewById(R.id.infoTextView);
        token = Shared.storage.getString("token","EMPTY");

        //emailEditField.setEnabled(false);
        dateOfBirthEditField.setFocusable(false);

        //requestAndRenderProfileDetails();

        addEventListeners();
    }

//    private void requestAndRenderProfileDetails(){
//        StringRequest profileRequest = new StringRequest(
//                Request.Method.GET,
//                Shared.ROOT_URL+"/register/",
//                response -> {
//                    Log.d(TAG, response);
//                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
//                    String email = jsonObject.get("username").getAsString();
//                    String first_name = jsonObject.get("first_name").getAsString();
//                    String last_name = jsonObject.get("last_name").getAsString();
//                    String phone_number = jsonObject.get("phone_number").getAsString();
//                    String date_of_birth = jsonObject.get("date_of_birth").getAsString();
//                    String profile_pic = "";
//                    try {
//                        profile_pic = jsonObject.get("profile_pic").getAsString();
//                    }catch (Exception e){
//                        profile_pic = "/media/profile_pics/profile_pic_guard.png";
//                        Log.d(TAG, e.toString());
//                    }
//                    emailEditField.setText(email);
//                    firstNameEditField.setText(first_name);
//                    lastNameEditField.setText(last_name);
//                    phoneNumberEditField.setText(phone_number);
//                    dateOfBirthEditField.setText(date_of_birth);
//
//
//                    Glide.with(this).load(Shared.ROOT_URL+profile_pic).into(profilePicView);
//                },
//                error -> Log.d(TAG, error.toString())
//        ){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String>  params = new HashMap<>();
//                params.put("Authorization", "Token "+token);
//                return params;
//            }
//        };
//
//        Shared.requestQueue.add(profileRequest);
//    }

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



        proceedButton.setOnClickListener(v -> {
            //String currentPassword = currentPasswordField.getText().toString();
            String email=emailEditField.getText().toString();
            String first_name = firstNameEditField.getText().toString();
            String last_name = lastNameEditField.getText().toString();
            String phone_number = phoneNumberEditField.getText().toString();
            String date_of_birth = dateOfBirthEditField.getText().toString();
            String password = passwordEditField.getText().toString();
            String confirmPassword = confPassEditField.getText().toString();


            if(password.trim().isEmpty()) {
                passwordEditField.setBackground(getDrawable(R.drawable.custom_error));
                confPassEditField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Password can't be Empty");
            }
            else if(!password.equalsIgnoreCase(confirmPassword)) {
                passwordEditField.setBackground(getDrawable(R.drawable.custom_error));
                confPassEditField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Passwords didn't Match");
            } else if (password.length() < 8){
                passwordEditField.setBackground(getDrawable(R.drawable.custom_error));
                confPassEditField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Invalid password, minimum length: 8");
            } else {
                //currentPasswordField.setText(null);
                confPassEditField.setText(null);
                passwordEditField.setText(null);

                MultipartRequest register = new MultipartRequest(
                        Request.Method.POST,
                        Shared.ROOT_URL + "/accounts/register/",
                        response -> {
                            Log.d(TAG, new String(response.data));
                            JsonObject jsonObject = new Gson().fromJson(new String(response.data),JsonObject.class);
                            String token = jsonObject.get("token").getAsString();
                            String name = jsonObject.get("first_name").getAsString()+" "+jsonObject.get("last_name").getAsString();
                            jsonObject.get("is_owner").getAsBoolean();
                            jsonObject.get("is_superuser").getAsBoolean();
                            jsonObject.get("is_verified").getAsBoolean();
                            Shared.storage.edit().putString("token",token).apply();
                            Shared.storage.edit().putBoolean("is_verified",false).apply();
                            Shared.storage.edit().putString("name",name).apply();
                            Toast.makeText(getApplicationContext(),"OTP Verification",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this,VerificationActivity.class);
                            intent.putExtra("sendOnCreate",false);
                            startActivity(intent);
                        },
                        error -> {
                            try {
                                Log.d(TAG, new String(error.networkResponse.data));

                                if(phone_number.length()!=10){
                                    Toast.makeText(getApplicationContext(), "Please Enter 10 digit Phone Number!", Toast.LENGTH_LONG).show();
                                }
                                else if(!Validators.isEmail(email)){
                                    Toast.makeText(getApplicationContext(), "Please Enter Valid EmailId!", Toast.LENGTH_LONG).show();
                                }
                                else if (error.networkResponse.statusCode == 400)
                                    Toast.makeText(getApplicationContext(), "Invalid Fields for Profile", Toast.LENGTH_LONG).show();
                                else if (error.networkResponse.statusCode == 413)
                                    Toast.makeText(getApplicationContext(), "Request Entity Too Large", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
                            }catch (Exception ignore){
                                Toast.makeText(getApplicationContext(), "Slow Network Connection", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this,MainActivity.class);
                                startActivity(intent);
                            }
                        }
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<>();
                        params.put("username", email);
                        params.put("first_name", first_name);
                        params.put("last_name", last_name);
                        params.put("phone_number", phone_number);
                        params.put("date_of_birth", date_of_birth);
                        params.put("password",password);
                        //params.put("is_owner","false");
                        if(profilePic==null)
                            params.put("profile_pic","");
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
                };

                Shared.requestQueue.add(register);
            }
        });

        passwordEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditField.setBackground(getDrawable(R.drawable.custom_input));
                confPassEditField.setBackground(getDrawable(R.drawable.custom_input));
                infoTextView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confPassEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditField.setBackground(getDrawable(R.drawable.custom_input));
                confPassEditField.setBackground(getDrawable(R.drawable.custom_input));
                infoTextView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public void pickDate(View v){
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateOfBirthEditField.setText(year+"-"+String.format("%02d",monthOfYear+1)+"-"+String.format("%02d",dayOfMonth));
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

}
