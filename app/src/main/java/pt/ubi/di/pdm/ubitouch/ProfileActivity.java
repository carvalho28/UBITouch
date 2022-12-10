package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView name, username, bio;
    ImageView profileUserImage;
    Button editProfileButton;
    ProgressBar progressBar;

    // Variables
    String userId;
    String token;

    private Integer[] event_IDs;
    private String[] event_titles;
    private String[] event_descriptions;
    private String[] event_images;
    private int event_creator;
    private String[] event_creation_dates;
    private String[] event_updated_dates;
    private String[] event_dates;
    private String[] event_times;
    private int nOfEvents;

    // URL
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/";
    private String user_events_URL = "https://server-ubi-touch.herokuapp.com/events/user/";
    private final String TAG = "JOAO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profileName);
        username = findViewById(R.id.profileUsername);
        bio = findViewById(R.id.profileBio);
        profileUserImage = findViewById(R.id.editProfileImage);
        editProfileButton = findViewById(R.id.editProfileButton);
        progressBar = findViewById(R.id.userProgressBar);

        progressBar.setVisibility(View.VISIBLE);

        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        event_creator = Integer.parseInt(userId);
        user_events_URL += event_creator;

        // print the user id and token
        // Log.i("Diogo", "Profile: " + userId);
        // Log.i("Diogo", "Profile: " + token);

        getUserData(userId);
        getUserEvents();

        editProfileButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
        progressBar.setVisibility(View.GONE);
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

    private void getUserEvents() {
        // Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, user_events_URL, null,
                response -> {
                    try {
                        JSONArray events = response.getJSONArray("data");
                        nOfEvents = events.length();
                        event_IDs = new Integer[nOfEvents];
                        event_titles = new String[nOfEvents];
                        event_descriptions = new String[nOfEvents];
                        event_images = new String[nOfEvents];
                        event_dates = new String[nOfEvents];
                        event_times = new String[nOfEvents];
                        event_creation_dates = new String[nOfEvents];
                        event_updated_dates = new String[nOfEvents];

                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
                            event_IDs[i] = Integer.parseInt(e.getString("idEvent"));
                            event_titles[i] = e.getString("title");
                            event_descriptions[i] = e.getString("description");
                            event_images[i] = e.getString(e.getString("image"));
                            event_dates[i] = e.getString("eventDate");
                            event_times[i] = e.getString("eventHour");
                            event_creation_dates[i] = e.getString("createdAt");
                            event_updated_dates[i] = e.getString("updatedAt");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json error");
                    }
                },
                error -> {
                    // if there was an error TODO
                    // msgError.setText(R.string.error_msg);
                    // msgError.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error");
                });

        // add the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}