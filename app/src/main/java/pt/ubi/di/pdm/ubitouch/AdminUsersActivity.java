package pt.ubi.di.pdm.ubitouch;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

public class AdminUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/all";
    private ArrayList<User> listUsers = new ArrayList<>();
    private RecyclerAdapterUser customAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getUserData();
    }

    private void getUserData() {
        // Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        JSONArray users = response.getJSONArray("data");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject e = (JSONObject)users.get(i);
                            // int id = Integer.parseInt(e.getString("idEvent"));
                            String name = e.getString("name");
                            String username = e.getString("username");
                            String image = e.getString("picture");
                            listUsers.add(new User(name, username, image));
                            // if user is admin then verified flag is visible
                            // ----- if verified == 1 then it is verified, else verified == 0 it is
                            // unverified
                            // if the user is not an admin then the flag is invisible
                        }

                        customAdapter = new RecyclerAdapterUser(this, listUsers);
                        recyclerView.setAdapter(customAdapter);
                    } catch (JSONException e) {
                        Log.e(TAG, "json error");
                    }
                },
                error -> {
                    // if there was an error TODO
                    Log.e(TAG, "Error");
                });

        // add the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }
}