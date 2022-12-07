package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText firstName, lastName, bio;
    Button btnEditSave;

    // Variables
    String userId;
    String token;

    // URL of the API
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/";
    private final String URLupdate = URL + "update/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = findViewById(R.id.editProfileFirstName);
        lastName = findViewById(R.id.editProfileLastName);
        bio = findViewById(R.id.editProfileBio);
        btnEditSave = findViewById(R.id.editProfileButton);

        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "241");
        token = sharedPref.getString("token", "");

        // print the user id
        Log.i("Diogo", "EditProfileActivity: onCreate() - userId: " + userId);
        Log.i("Diogo", "EditProfileActivity: onCreate() - token: " + token);

        getUserData(userId);

        btnEditSave.setOnClickListener(
                v -> {
                    // updateProfile(userId);
                });
    }

    // get data from the API using the user id
    private void getUserData(String userId) {
        // use url + userId to get the user data, and use the token to authenticate
        String url = URL + userId;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        firstName.setText(response.getString("firstName"));
                        lastName.setText(response.getString("lastName"));
                        bio.setText(response.getString("bio"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Diogo", "Error: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }
}