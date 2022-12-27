package pt.ubi.di.pdm.ubitouch;

import static com.android.volley.VolleyLog.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/all";
    private ArrayList<User> listUsers = new ArrayList<>();
    private RecyclerAdapterUser customAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        recyclerView = findViewById(R.id.recyclerViewUsers);

        Log.d("Diogo", String.valueOf(R.id.recyclerViewUsers));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchView = findViewById(R.id.searchViewUsers);

        getUserData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    customAdapter.clearFilter();
                    customAdapter.notifyDataSetChanged();
                } else {
                    customAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

    }

    private void getUserData() {
        // Get events from DB
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        JSONArray users = response.getJSONArray("data");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject e = (JSONObject) users.get(i);
                            // int id = Integer.parseInt(e.getString("idEvent"));
                            String name = e.getString("name");
                            String username = e.getString("username");
                            String image = e.getString("picture");
                            String userId = e.getString("idUser");
                            listUsers.add(new User(name, username, image, userId));
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

    // private void filter(String text) {
    // ArrayList<User> filteredList = new ArrayList<>();

    // for (User user : listUsers) {
    // // filter by name or username
    // if (user.getName().toLowerCase().contains(text.toLowerCase())
    // || user.getUsername().toLowerCase().contains(text.toLowerCase())) {
    // filteredList.add(user);
    // }
    // }

    // customAdapter.setData(filteredList);
    // customAdapter.notifyDataSetChanged();
    // }
}