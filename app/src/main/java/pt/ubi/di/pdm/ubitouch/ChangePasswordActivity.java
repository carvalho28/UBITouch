package pt.ubi.di.pdm.ubitouch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText currentPassword, newPassword, confirmPassword;
    Button buttonChangePassword;
    TextView textViewError;

    private String userId, token;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        buttonChangePassword = findViewById(R.id.buttonSave);
        textViewError = findViewById(R.id.msgChangePasswordError);
        back = findViewById(R.id.btnNotif);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "");
        token = sharedPref.getString("token", "");

        back.setOnClickListener(v -> {
            finish();
        });

        buttonChangePassword.setOnClickListener(v -> {
            changePassword();
        });


    }

    private void changePassword() {

        String url = "https://server-ubi-touch.herokuapp.com/users/changePassword/" + userId;

        String currentPassword = this.currentPassword.getText().toString();
        String newPassword = this.newPassword.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();

        if (newPassword.equals(confirmPassword)) {

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("password", currentPassword);
                jsonBody.put("newPassword", newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue queue = Volley.newRequestQueue(this);
            // new post request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            String msg = response.getString("message");
                            if (msg.equals("Password changed successfully")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                builder.setMessage("Password changed successfully")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            Intent intent = new Intent(ChangePasswordActivity.this,
                                                    FeedActivity.class);
                                            startActivity(intent);
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                textViewError.setVisibility(TextView.VISIBLE);
                                textViewError.setText("Wrong password");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    },
                    error -> {
                        // error
                        Log.d("Error.Response", error.toString());
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }

                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes();
                }
            };

            queue.add(jsonObjectRequest);

        } else {
            textViewError.setVisibility(TextView.VISIBLE);
            textViewError.setText("Passwords do not match");
        }
    }

}