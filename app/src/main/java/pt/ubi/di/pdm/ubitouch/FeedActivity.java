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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.concurrent.atomic.AtomicInteger;

public class FeedActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    FloatingActionButton newEvent;
    TextView noPostsText;
    RecyclerView recyclerView;
    ImageButton imageButton1;
    ImageButton imageButton2;
    ImageButton imageButton3;
    ImageButton imageButton4;


    // DEBUG
    private final String TAG = "JOAO";
    private final String events_query_URI = "https://server-ubi-touch.herokuapp.com/events/all";

    private int nOfEvents;

    private ArrayList<Event> listEvents = new ArrayList<>();

    private RecyclerAdapter customAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        imageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.feedProgressBar);
        newEvent = findViewById(R.id.btnNewEvent);
        noPostsText = findViewById(R.id.noPostsText);
        imageButton4 = findViewById(R.id.imageButton4);

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String imageProfile = sharedPref.getString("picture", "false");
        Picasso.get().load(imageProfile).into(imageView);

        getEventsData();

        imageButton4.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, SettingsActivity.class);

                    startActivity(intent);
        });

        newEvent.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, CreateActivity.class);
            startActivity(intent);
        });
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


                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
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

        // hide loading circle
        progressBar.setVisibility(View.GONE);
    }
}