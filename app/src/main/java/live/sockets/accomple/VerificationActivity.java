package live.sockets.accomple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class VerificationActivity extends AppCompatActivity {

    private EditText[] digits;
    private Button submitButton;

    private final String TAG = "Debug";
    private int currentDigit;
    private final int MAX_DIGITS = 6;
    private int current;

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
        submitButton = findViewById(R.id.submitButton);

        addEventListeners();
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
                    int id = getCurrentFocus().getId();
                    editText = findViewById(id);
                    currentDigit = Integer.parseInt(editText.getTag().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String string = editText.getText().toString();
                    int n = string.length();
                    switch (n) {
                        case 0:
                            return;

                        case 1:
                            if (currentDigit == MAX_DIGITS - 1)
                                submitButton.requestFocus();
                            else
                                digits[currentDigit+1].requestFocus();
                            break;

                        default:
                            String digit = String.valueOf(string.charAt(n - 1));
                            editText.setText(digit);

                    }
                }
            });

            // move cursor to last position
            digit.setOnFocusChangeListener((v, hasFocus) -> digit.setSelection(digit.getText().length()));
        }

    }
}