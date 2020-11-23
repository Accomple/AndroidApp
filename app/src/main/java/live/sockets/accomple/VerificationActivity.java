package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

    private EditText[] digits;
    private Button submitButton;
    private TextView resendTextView;
    private TextView messageTextView;
    private TextView countDownTextView;
    private TextView instructionTextView;

    private final String TAG = "Debug";
    private String token = "EMPTY";
    private boolean is_verified = false;
    private int currentDigit;
    private final int MAX_DIGITS = 6;
    private CountDownTimer timer;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        digits = new EditText[MAX_DIGITS];
        digits[0] = findViewById(R.id.digitOne);
        digits[1] = findViewById(R.id.digitTwo);
        digits[2] = findViewById(R.id.digitThree);
        digits[3] = findViewById(R.id.digitFour);
        digits[4] = findViewById(R.id.digitFive);
        digits[5] = findViewById(R.id.digitSix);
        messageTextView = findViewById(R.id.messageTextView);
        resendTextView = findViewById(R.id.resendTextView);
        countDownTextView = findViewById(R.id.countDownTextView);
        instructionTextView = findViewById(R.id.instructionTextView);
        submitButton = findViewById(R.id.submitButton);
        token = Shared.storage.getString("token","EMPTY");

        instructionTextView.setText(null);
        submitButton.setEnabled(false);
        messageTextView.setText(null);

        addEventListeners();

        countDownTextView.setText(null);
        resendTextView.setTextColor(Color.parseColor("#757575"));
        resendTextView.setEnabled(false);

        timer = new CountDownTimer(60000+100,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.setText(String.format("%02d", (int)millisUntilFinished/1000)+"s");
            }

            @Override
            public void onFinish() {
                resendTextView.setTextColor(Color.parseColor("#512DA8"));
                countDownTextView.setText(null);
                resendTextView.setEnabled(true);
            }
        };

        requestAndRender();

    }

    private void addEventListeners(){

        for(EditText digit : digits) {

            digit.addTextChangedListener(new TextWatcher() {
                EditText editText;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int id = getCurrentFocus().getId();
                        editText = findViewById(id);
                        currentDigit = Integer.parseInt(editText.getTag().toString());
                    } catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String string = editText.getText().toString();
                        int n = string.length();

                        String otp = "";
                        for(EditText digit : digits)
                            otp += digit.getText().toString();
                        submitButton.setEnabled(otp.length() == 6);

                        if(is_verified)
                            submitButton.setEnabled(false);

                        messageTextView.setText(null);

                        switch (n) {
                            case 0:
                                return;

                            case 1:
                                if (currentDigit == MAX_DIGITS - 1)
                                    digits[currentDigit].requestFocus();
                                else
                                    digits[currentDigit + 1].requestFocus();
                                break;

                            default:
                                String digit = String.valueOf(string.charAt(n - 1));
                                editText.setText(digit);

                        }
                    } catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                }
            });

            // move cursor to last position
            digit.setOnFocusChangeListener((v, hasFocus) -> digit.setSelection(digit.getText().length()));
        }

        submitButton.setOnClickListener(v -> {
            otp = "";
            for(EditText digit : digits)
                otp += digit.getText().toString();

            StringRequest submitRequest = new StringRequest(

                    Request.Method.POST,
                    Shared.ROOT_URL+"/accounts/verification_code/",
                    response -> {
                        Log.d(TAG, response);
                        Shared.storage.edit().putBoolean("is_verified",true).apply();
                        is_verified = true;
                        messageTextView.setTextColor(Color.parseColor("#4CAF50"));
                        messageTextView.setText("Account Verified!");
                        submitButton.setEnabled(false);
                        Toast.makeText(this,"Account Verified!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                    },
                    error -> {
                        if(error.networkResponse.statusCode == 423){
                            messageTextView.setTextColor(Color.parseColor("#4CAF50"));
                            messageTextView.setText("Verified Account!");
                            submitButton.setEnabled(false);
                        } else if (error.networkResponse.statusCode == 422){
                            messageTextView.setTextColor(Color.parseColor("#FF4081"));
                            messageTextView.setText("Invalid OTP");
                        } else if (error.networkResponse.statusCode == 410){
                            messageTextView.setTextColor(Color.parseColor("#FF4081"));
                            messageTextView.setText("OTP Expired");
                        }
                        else
                            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
                    }
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<>();
                    params.put("otp", otp);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Token "+token);
                    return params;
                }
            };

            Shared.requestQueue.add(submitRequest);

        });

        resendTextView.setOnClickListener(v -> resend());
    }

    private void requestAndRender(){
        StringRequest statusRequest = new StringRequest(
                Request.Method.GET,
                Shared.ROOT_URL+"/accounts/verification_code/",
                response -> {
                    Log.d(TAG, response);
                    JsonObject jsonObject = new Gson().fromJson(response,JsonObject.class);
                    String email = jsonObject.get("email").getAsString();
                    instructionTextView.setText("Passcode sent to "+email);
                    resend();
                },
                error -> Toast.makeText(this,"Something Went Wrong!", Toast.LENGTH_LONG).show()
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(statusRequest);
    }

    private void resend(){
        resendTextView.setTextColor(Color.parseColor("#757575"));
        resendTextView.setEnabled(false);

        StringRequest resendRequest = new StringRequest(
                Request.Method.POST,
                Shared.ROOT_URL+"/accounts/verification_code/",
                response -> {
                    Log.d(TAG, response);
                    timer.start();
                },
                error -> {
                    if(error.networkResponse.statusCode == 423){
                        messageTextView.setTextColor(Color.parseColor("#4CAF50"));
                        messageTextView.setText("Verified Account!");
                        submitButton.setEnabled(false);
                    }
                    else {
                        Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
                        resendTextView.setTextColor(Color.parseColor("#512DA8"));
                        countDownTextView.setText(null);
                        resendTextView.setEnabled(true);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("resend", "true");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        Shared.requestQueue.add(resendRequest);
    }
}