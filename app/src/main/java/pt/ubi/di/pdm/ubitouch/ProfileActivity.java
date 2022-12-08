package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView name, username, bio;
    ImageView profileUserImage;
    Button editProfileButton;

    // Variables
    String userId;
    String token;

    // URL
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profileName);
        username = findViewById(R.id.profileUsername);
        bio = findViewById(R.id.profileBio);
        profileUserImage = findViewById(R.id.editProfileImage);
        editProfileButton = findViewById(R.id.editProfileButton);

        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        // print the user id and token
        // Log.i("Diogo", "Profile: " + userId);
        // Log.i("Diogo", "Profile: " + token);

        getUserData(userId);

        editProfileButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
    }

    private void getUserData(String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        name.setText(response.getString("firstName") + " " + response.getString("lastName"));
                        username.setText(response.getString("username"));
                        bio.setText(response.getString("biography"));
                        Picasso.get().load(response.getString("picture")).into(profileUserImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.i("Diogo", "getUserData: " + error);
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.i("Diogo", "getHeaders: " + headers);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}