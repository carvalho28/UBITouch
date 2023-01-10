package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnRegister;
    TextInputEditText name, email, username, password, confirmPassword;
    TextView msgError;
    ProgressBar progressBar;
    TextView signIn;

    // DEBUG
    private final String TAG = "Diogo";

    // URL of the API
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sharedPref != null) {
            sharedPref.edit().clear().apply();
        }

        btnRegister = findViewById(R.id.buttonRegister);

        name = findViewById(R.id.registerName);
        email = findViewById(R.id.registerInputEmail);
        username = findViewById(R.id.registerInputUsername);
        password = findViewById(R.id.registerInputPassword);
        confirmPassword = findViewById(R.id.registerInputConfirmPassword);
        msgError = findViewById(R.id.textViewError);
        progressBar = findViewById(R.id.registerProgressBar);
        signIn = findViewById(R.id.signIn);

        msgError.setVisibility(TextView.GONE);

        Log.i(TAG, "RegisterActivity: onCreate()");

        signIn.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
        );

        btnRegister.setOnClickListener(
                v -> {
                    // if there is an empty field
                    if (name.getText().toString().isEmpty()
                            || email.getText().toString().isEmpty() || username.getText().toString().isEmpty()
                            || password.getText().toString().isEmpty()
                            || confirmPassword.getText().toString().isEmpty()) {
                        Log.i(TAG, "RegisterActivity: onCreate(): empty field");
                        msgError.setText(R.string.emptyFieldError);
                        msgError.setVisibility(TextView.VISIBLE);
                    }
                    // if the password and the confirm password are different
                    else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        msgError.setText(R.string.differentPasswords);
                        msgError.setVisibility(TextView.VISIBLE);
                    }
                    // if the password is less than 10 characters
                    else if (password.getText().toString().length() < 8) {
                        msgError.setText(R.string.tooShortPassword);
                        msgError.setVisibility(TextView.VISIBLE);
                    }
                    // if the email is not valid
                    else if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
                        msgError.setText(R.string.invalidEmail);
                        msgError.setVisibility(TextView.VISIBLE);
                    }
                    // create the user
                    else {
                        // dismiss keyboard
                        View thisView = this.getCurrentFocus();
                        if (thisView != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(thisView.getWindowToken(), 0);
                        }
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        try {
                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", name.getText().toString());
                            jsonObject.put("username", username.getText().toString());
                            jsonObject.put("email", email.getText().toString());
                            jsonObject.put("password", password.getText().toString());

                            // make a json object request
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                                    jsonObject,
                                    response -> {
                                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                                        Log.i(TAG, "RegisterActivity: onCreate(): response: " + response.toString());
                                        // go to the login activity
                                        Intent intent = new Intent(this, LoginActivity.class);
                                        startActivity(intent);
                                    },
                                    error -> {
                                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                                        Log.i(TAG, "RegisterActivity: onCreate(): error: " + error.toString());
                                        msgError.setText(R.string.existing_user);
                                        msgError.setVisibility(TextView.VISIBLE);
                                        // close the keyboard
                                        View view = this.getCurrentFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                                    Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        username.requestFocus();
                                    }) {
                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }

                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    Log.i(TAG, "RegisterActivity: onCreate(): parseNetworkResponse(): "
                                            + response.toString());
                                    return super.parseNetworkResponse(response);
                                }
                            };
                            requestQueue.add(jsonObjectRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}