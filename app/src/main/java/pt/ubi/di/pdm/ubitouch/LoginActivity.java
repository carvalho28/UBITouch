package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    MaterialButton btnLogin;
    TextInputEditText username, password;
    TextView msgError;
    ProgressBar progressBar;
    TextView register;

    // URL of the API
    private final String URLEmail = "https://server-ubi-touch.herokuapp.com/users/loginEmail";
    private final String URLUsername = "https://server-ubi-touch.herokuapp.com/users/loginUsername";

    // DEBUG
    private final String TAG = "Diogo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String x = sharedPref.getString("token", "");

        Log.i(TAG, x);

        if (!x.isEmpty()) {
            checkAdmin();
        }

        btnLogin = findViewById(R.id.buttonLogin);
        username = findViewById(R.id.loginInputUsernameOrEmail);
        password = findViewById(R.id.loginInputPassword);
        msgError = findViewById(R.id.loginTextViewError);
        progressBar = findViewById(R.id.loginProgressBar);
        register = findViewById(R.id.register);

        // show loading circle
        progressBar.setVisibility(View.VISIBLE);

        // on writing on the username field or password field, hide the error message
        username.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                msgError.setVisibility(View.GONE);
            }
        });

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                msgError.setVisibility(View.GONE);
            }
        });

        register.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                });

        btnLogin.setOnClickListener(
                v -> {
                    // if there is an empty field
                    if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                        msgError.setText(R.string.emptyFieldError);
                        msgError.setVisibility(View.VISIBLE);
                    } else {
                        // check if the username is an email or not
                        if (username.getText().toString().contains("@")) {
                            // if it is an email
                            login(URLEmail);
                        } else {
                            // if it is a username
                            login(URLUsername);
                        }
                    }
                });
        // hide loading circle
        progressBar.setVisibility(View.GONE);
    }

    private void login(String URL) {

        Log.i(TAG, "LoginActivity: login()");
        AtomicReference<String> fcmToken = new AtomicReference<>("");

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);
        // create the JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            if (Objects.equals(URL, URLEmail)) {
                jsonBody.put("email", username.getText().toString());
            } else {
                jsonBody.put("username", username.getText().toString());
            }
            jsonBody.put("password", password.getText().toString());
            // await firebase token
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                fcmToken.set(task.getResult());

                if (!fcmToken.get().isEmpty()) {
                    try {
                        jsonBody.put("fcmToken", fcmToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jsonBody.put("fcmToken", "null");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, jsonBody.toString());

                // create the request
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                        response -> {
                            // if the login was successful
                            if (response.has("token")) {
                                Log.i(TAG, response.toString());
                                // save the token, id and username
                                SharedPreferences sharedPref = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                try {
                                    editor.putString("token", response.getString("token"));
                                    editor.putString("id", response.getString("idUser"));
                                    editor.putString("username", response.getString("username"));
                                    editor.putString("name", response.getString("name"));
                                    editor.putString("picture", response.getString("picture"));
                                    editor.putString("profEvents", response.getString("idUser"));
                                    editor.putString("isAdmin", response.getString("isAdmin"));
                                    editor.apply();

                                    // print the token
                                    Log.i(TAG, "token: " + response.getString("token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // show loading circle
                                progressBar.setVisibility(View.VISIBLE);

                                checkAdmin();
                            }
                        },
                        error -> {
                            // if there was an error
                            msgError.setText(R.string.incorrect_credentials);
                            msgError.setVisibility(View.VISIBLE);
                        });

                // add the request to the queue
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // check admin and intent to the correct activity
    private void checkAdmin() {
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String isAdmin = sharedPref.getString("isAdmin", "0");

        Intent intent;
        if (isAdmin.equals("1")) {
            intent = new Intent(this, FeedActivity.class);
        } else {
            intent = new Intent(this, FeedActivity.class);
        }
        startActivity(intent);
    }
}