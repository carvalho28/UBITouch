package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView name, username, bio;
    ImageView profileUserImage;
    Button editProfileButton;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    // Variables
    String userId;
    String token;

//    private Integer[] event_IDs;
//    private String[] event_titles;
//    private String[] event_descriptions;
//    private String[] event_images;
    private int event_creator;
//    private String[] event_creation_dates;
//    private String[] event_updated_dates;
//    private String[] event_dates;
//    private String[] event_times;
    private int nOfEvents;
    private ArrayList<Event> listEvents = new ArrayList<>();

    private RecyclerAdapter customAdapter;
    private RecyclerView.LayoutManager layoutManager;

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

        recyclerView = findViewById(R.id.userPosts);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progressBar.setVisibility(View.VISIBLE);

        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");
        // TODO If false, will it lead to not found, login ...?

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
                        name.setText(response.getString("name"));
                        username.setText("@" + response.getString("username"));
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
                        Log.i(TAG, "N of events: " + nOfEvents);

//                        event_IDs = new Integer[nOfEvents];
//                        event_titles = new String[nOfEvents];
//                        event_descriptions = new String[nOfEvents];
//                        event_images = new String[nOfEvents];
//                        event_dates = new String[nOfEvents];
//                        event_times = new String[nOfEvents];
//                        event_creation_dates = new String[nOfEvents];
//                        event_updated_dates = new String[nOfEvents];

                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
//
                            // int id = Integer.parseInt(e.getString("idEvent"));
                            String title = e.getString("title");
                            String description = e.getString("description");
                            String image = e.getString("image");
                            String isVerified = e.getString("isVerified");
                            //String userId = e.getString("idUser");
                            String eventDate = e.getString("eventDate");
                            String eventHour = e.getString("eventHour");
                            String creationDate = e.getString("createdAt");
                            //String updated_dates = e.getString("updatedAt");
                            listEvents.add(new Event(title, image, description, eventHour, eventDate, "1", "0"));
                            // if user is admin then verified flag is visible
                            // ----- if verified == 1 then it is verified, else verified == 0 it is unverified
                            // if the user is not an admin then the flag is invisible
                        }

                        customAdapter = new RecyclerAdapter(this, listEvents);
                        recyclerView.setAdapter(customAdapter);
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