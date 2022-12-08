package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

public class FeedActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;

    // DEBUG
    private final String TAG = "JOAO";
    private final String events_query_URI = "https://server-ubi-touch.herokuapp.com/events/all";

    private Integer[] event_IDs;
    private String[] event_titles;
    private String[] event_descriptions;
    private String[] event_images;
    private Integer[] event_creators;
    private String[] event_creation_dates;
    private String[] event_updated_dates;
    private int nOfEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        imageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.feedProgressBar);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String imageProfile = sharedPref.getString("picture", "false");
        Picasso.get().load(imageProfile).into(imageView);

        getEventsData();
    }

    public void getEventsData() {
        // show loading circle
        progressBar.setVisibility(View.VISIBLE);

//        Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, events_query_URI, null,
                response -> {
                    try {
                        JSONArray events = response.getJSONArray("data");
                        nOfEvents = events.length();
                        event_IDs = new Integer[nOfEvents];
                        event_titles = new String[nOfEvents];
                        event_descriptions = new String[nOfEvents];
                        event_images = new String[nOfEvents];
                        event_creators = new Integer[nOfEvents];
                        event_creation_dates = new String[nOfEvents];
                        event_updated_dates = new String[nOfEvents];

                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);
                            event_IDs[i] = Integer.parseInt(e.getString("idEvent"));
                            event_titles[i] = e.getString("title");
                            event_descriptions[i] = e.getString("description");
                            event_images[i] = e.getString(e.getString("image"));
                            event_creators[i] = Integer.parseInt(e.getString("idUser"));
                            event_creation_dates[i] = e.getString("createdAt");
                            event_updated_dates[i] = e.getString("updatedAt");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json error");
                    }
                },
                error -> {
                    // if there was an error
//                    msgError.setText(R.string.error_msg);
//                    msgError.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error");
                });

        // add the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

//         hide loading circle
        progressBar.setVisibility(View.GONE);
    }
}