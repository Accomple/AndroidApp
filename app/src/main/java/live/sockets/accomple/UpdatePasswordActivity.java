package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordActivity extends AppCompatActivity {

    private Button cancelButton;
    private Button confirmButton;
    private EditText currentPasswordField;
    private EditText newPasswordField;
    private EditText confirmPasswordField;
    private TextView infoTextView;

    private final String TAG = "Debug";
    private String token = "EMPTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        cancelButton = findViewById(R.id.cancelButton1);
        confirmButton = findViewById(R.id.confirmButton);
        currentPasswordField = findViewById(R.id.currentPasswordField);
        newPasswordField = findViewById(R.id.newPasswordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        infoTextView = findViewById(R.id.infoTextView);
        token = Shared.storage.getString("token","EMPTY");


        addEventListeners();
    }

    private void addEventListeners(){

        cancelButton.setOnClickListener(v -> finish());

        confirmButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordField.getText().toString();
            String password = newPasswordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();

            if(currentPassword.trim().isEmpty()){
                currentPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Current Password left blank");
            }
            else if(password.trim().isEmpty()) {
                newPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                confirmPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("New Password can't be Empty");
            }
            else if(!password.equalsIgnoreCase(confirmPassword)) {
                newPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                confirmPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Passwords didn't Match");
            } else if (password.length() < 8){
                newPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                confirmPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                infoTextView.setText("Invalid password, minimum length: 8");
            } else {
                currentPasswordField.setText(null);
                confirmPasswordField.setText(null);
                newPasswordField.setText(null);

                StringRequest passwordUpdateRequest = new StringRequest(
                        Request.Method.PUT,
                        Shared.ROOT_URL + "/accounts/password/update/user=me/",
                        response -> {
                            Log.d(TAG, response);
                            infoTextView.setTextColor(Color.parseColor("#4CAF50"));
                            infoTextView.setText("Password Changed Successfully");
                        },
                        error -> {
                            Log.d(TAG,new String(error.networkResponse.data));
                            if(error.networkResponse.statusCode == 401) {
                                currentPasswordField.setBackground(getDrawable(R.drawable.custom_error));
                                infoTextView.setTextColor(Color.parseColor("#E91E63"));
                                infoTextView.setText("Current Password was Incorrect");
                            }
                            else {
                                finish();
                                Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<>();
                        params.put("current_password", currentPassword);
                        params.put("new_password", password);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String>  params = new HashMap<>();
                        params.put("Authorization", "Token "+token);
                        return params;
                    }
                };

                Shared.requestQueue.add(passwordUpdateRequest);
            }
        });

        currentPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPasswordField.setBackground(getDrawable(R.drawable.custom_input));
                infoTextView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPasswordField.setBackground(getDrawable(R.drawable.custom_input));
                confirmPasswordField.setBackground(getDrawable(R.drawable.custom_input));
                infoTextView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPasswordField.setBackground(getDrawable(R.drawable.custom_input));
                confirmPasswordField.setBackground(getDrawable(R.drawable.custom_input));
                infoTextView.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}