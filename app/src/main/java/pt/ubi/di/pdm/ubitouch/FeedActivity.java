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
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import java.util.concurrent.atomic.AtomicInteger;

public class FeedActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    FloatingActionButton newEvent;
    RecyclerView recyclerView;

    // DEBUG
    private final String TAG = "JOAO";
    private final String events_query_URI = "https://server-ubi-touch.herokuapp.com/events/all";

    // private ArrayList<Integer> event_IDs;
    // private ArrayList<String> event_titles;
    // private ArrayList<String> event_descriptions;
    // private ArrayList<String> event_images;
    // private ArrayList<Integer> event_creators;
    // private ArrayList<String> event_dates;
    // private ArrayList<String> event_times;
    // private ArrayList<String> event_creation_dates;
    // private ArrayList<String> event_updated_dates;
    private int nOfEvents;

    private ArrayList<PostActivity> listEvents;

    private RecyclerAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        imageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.feedProgressBar);
        newEvent = findViewById(R.id.btnNewEvent);

        recyclerView = findViewById(R.id.recyclerView);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String imageProfile = sharedPref.getString("picture", "false");
        Picasso.get().load(imageProfile).into(imageView);

        getEventsData();

        displayEvents();

        newEvent.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, CreateActivity.class);
            startActivity(intent);
        });
    }

    private void displayEvents() {
        if (nOfEvents == 0) {
            // no events
        }
        else {
            customAdapter = new RecyclerAdapter(FeedActivity.this, listEvents);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        }
    }

    public void getEventsData() {
        // show loading circle
        progressBar.setVisibility(View.VISIBLE);

        // Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, events_query_URI, null,
                response -> {
                    try {
                        JSONArray events = response.getJSONArray("data");
                        nOfEvents = events.length();
                        // event_IDs = new Integer[nOfEvents];
                        // event_titles = new String[nOfEvents];
                        // event_descriptions = new String[nOfEvents];
                        // event_images = new String[nOfEvents];
                        // event_creators = new Integer[nOfEvents];
                        // event_dates = new String[nOfEvents];
                        // event_times = new String[nOfEvents];
                        // event_creation_dates = new String[nOfEvents];
                        // event_updated_dates = new String[nOfEvents];
                        Log.i(TAG, "bEFORE FOR");
                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
                            // int id = Integer.parseInt(e.getString("idEvent"));
                            Log.i(TAG, "Here 1");
                            String title = e.getString("title");
                            Log.i(TAG, "Here 2");
                            String description = e.getString("description");
                            // verify if image exists
                            Log.i(TAG, "Here 3");
                            String image = e.getString("image");
                            Log.i(TAG, "Here 4");
                            String userId = e.getString("idUser");
                            String eventDate = e.getString("eventDate");
                            String eventTime = e.getString("eventHour");
                            String creationDate = e.getString("createdAt");
                            String updated_dates = e.getString("updatedAt");
                            Log.i(TAG, "After getStrings " + i);
                            PostActivity event = new PostActivity(
                                title, description, creationDate, eventDate, eventTime, image
                            );
                            Log.i(TAG, "After new event + " + i);
                            listEvents.add(event);
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

        // hide loading circle
        progressBar.setVisibility(View.GONE);
    }
}