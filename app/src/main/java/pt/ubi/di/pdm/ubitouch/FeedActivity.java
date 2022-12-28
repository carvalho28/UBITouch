package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    FloatingActionButton newEvent;
    TextView noPostsText;
    RecyclerView recyclerView;
    TextView profileName;
    LinearLayout linLayout;

    // Header
    ImageButton settings;
    ImageButton notifications;

    // Footer
    ImageButton home;
    ImageButton profile;

    // Refresh
    SwipeRefreshLayout swipeRefreshLayout;

    // DEBUG
    private final String TAG = "JOAO";
    private final String events_query_URI = "https://server-ubi-touch.herokuapp.com/events/";
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/";
    String userID;
    String token;
    String isAdmin;

    private int nOfEvents;

    private ArrayList<Event> listEvents = new ArrayList<>();

    private RecyclerAdapter customAdapter;
    private RecyclerView.LayoutManager layoutManager;

    DarkMode darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Header
        settings = findViewById(R.id.btnSettings);
        notifications = findViewById(R.id.btnNotif);

        // Footer
        profile = findViewById(R.id.btnProfile);
        home = findViewById(R.id.btnHome);

        // Middle
        imageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.feedProgressBar);
        newEvent = findViewById(R.id.btnNewEvent);
        noPostsText = findViewById(R.id.noPostsText);
        profileName = findViewById(R.id.profileName);
        recyclerView = findViewById(R.id.recyclerView);
        linLayout = findViewById(R.id.userandname);

        darkmode = new DarkMode(this);

        // Refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        if (darkmode.loadDarkMode() == true) {
            swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.softBlack);
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(this, R.color.babyBlue));
        } else {
            swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey);
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(this, R.color.blue));
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            listEvents.clear();
            getEventsData();
            swipeRefreshLayout.setRefreshing(false);
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String imageProfile = sharedPref.getString("picture", "false");
        userID = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");
        isAdmin = sharedPref.getString("isAdmin", "0");
        // set profEvents to x
        sharedPref.edit().putString("profEvents", "x").apply();

        // profileName.setText(userID);

        getUserData(userID);
        getEventsData();

        // Header
        settings.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, SettingsActivity.class);

                    startActivity(intent);
                });

        /*
         * notifications.setOnClickListener(
         * v -> {
         * Intent intent = new Intent(this, NotificationsActivity.class);
         *
         * startActivity(intent);
         * });
         */

        // Footer
        home.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        profile.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userID", sp.getString("id", "false"));
            startActivity(intent);
        });

        newEvent.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        progressBar.setVisibility(View.VISIBLE);
        linLayout.setVisibility(View.INVISIBLE);
    }

    private void getUserData(String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        profileName.setText(response.getString("name"));
                        Picasso.get().load(response.getString("picture")).into(imageView);
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

    public void getEventsData() {
        // if isAdmin == 1 add admin to query
        String url;
        if (isAdmin.equals("1")) {
            url = events_query_URI + "admin/" + userID;
        } else {
            url = events_query_URI + userID;
        }

        // Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray events = response.getJSONArray("data");
                        // log the response
                        Log.i(TAG, "getEventsData: " + events);
                        nOfEvents = events.length();
                        if (nOfEvents > 0)
                            noPostsText.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
                            // int id = Integer.parseInt(e.getString("idEvent"));
                            String title = e.getString("title");
                            String description = e.getString("description");
                            String imageUser = e.getString("picture");
                            String isVerified = e.getString("isVerified");
                            String userId = e.getString("idUser");
                            String eventDate = e.getString("eventDate");
                            String eventHour = e.getString("eventHour");
                            String creationDate = e.getString("createdAt");
                            String latitude = e.getString("latitude");
                            String longitude = e.getString("longitude");
                            String name = e.getString("name");
                            String username = e.getString("username");
                            String idEvent = e.getString("idEvent");
                            String isInterested = e.getString("isInterested");
                            String imageOrVideo = e.getString("image");
                            listEvents.add(new Event(title, imageUser, description, eventHour, eventDate, isVerified,
                                    latitude, longitude, name, username, idEvent, isInterested, userId, imageOrVideo));
                            // if user is admin then verified flag is visible
                            // ----- if verified == 1 then it is verified, else verified == 0 it is
                            // unverified
                            // if the user is not an admin then the flag is invisible
                        }

                        customAdapter = new RecyclerAdapter(this, listEvents);
                        recyclerView.setAdapter(customAdapter);

                        progressBar.setVisibility(View.GONE);
                        linLayout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        Log.e("Diogo", "json error");
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