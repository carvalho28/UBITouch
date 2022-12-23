package pt.ubi.di.pdm.ubitouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

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

        //User info
        name = findViewById(R.id.profileName);
        username = findViewById(R.id.profileUsername);
        bio = findViewById(R.id.profileBio);
        profileUserImage = findViewById(R.id.editProfileImage);
        editProfileButton = findViewById(R.id.editProfileButton);


        //Tabs
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(vpAdapter);
        vpAdapter.addFragment(new InterestsFragment());
        vpAdapter.addFragment(new EventsFragment());




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.d("here", String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");
        // TODO If false, will it lead to not found, login ...?


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
}

