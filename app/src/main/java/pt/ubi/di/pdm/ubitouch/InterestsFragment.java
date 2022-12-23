package pt.ubi.di.pdm.ubitouch;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InterestsFragment extends Fragment {

    private int event_creator;
    private int nOfEvents;
    private ArrayList<Event> listEvents = new ArrayList<>();
    private RecyclerView recyclerView;

    private RecyclerAdapter customAdapter;

    // URL
    private String user_events_URL = "https://server-ubi-touch.herokuapp.com/interests/user/";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.userInterests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("id", "false");
        String token = sharedPref.getString("token", "false");

        event_creator = Integer.parseInt(userId);
        user_events_URL += event_creator;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, user_events_URL, null,
                response -> {
                    try {
                        JSONArray events = response.getJSONArray("data");
                        nOfEvents = events.length();
                        Log.i("Diogo", "N of events: " + nOfEvents);

                        for (int i = 0; i < events.length(); i++) {
                            JSONObject e = (JSONObject) events.get(i);

                            // int id = Integer.parseInt(e.getString("idEvent"));
                            String title = e.getString("title");
                            String description = e.getString("description");
                            String imageUser = e.getString("picture");
                            String isVerified = e.getString("isVerified");
                            //String userId = e.getString("idUser");
                            String eventDate = e.getString("eventDate");
                            String eventHour = e.getString("eventHour");
                            String creationDate = e.getString("createdAt");
                            String latitude = e.getString("latitude");
                            String longitude = e.getString("longitude");
                            String name = e.getString("name");
                            String username = e.getString("username");
                            listEvents.add(new Event(title, imageUser, description, eventHour, eventDate, "1", "0",
                                    latitude, longitude, name, username));
                            // if user is admin then verified flag is visible
                            // ----- if verified == 1 then it is verified, else verified == 0 it is unverified
                            // if the user is not an admin then the flag is invisible
                        }

                        customAdapter = new RecyclerAdapter(getContext(), listEvents);
                        recyclerView.setAdapter(customAdapter);
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}