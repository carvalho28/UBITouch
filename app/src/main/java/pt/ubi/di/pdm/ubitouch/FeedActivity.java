package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FeedActivity extends AppCompatActivity {

    ImageView imageView;

    // DEBUG
    private final String TAG = "Diogo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        imageView = findViewById(R.id.profileImageView);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String imageProfile = sharedPref.getString("picture", "false");
        Picasso.get().load(imageProfile).into(imageView);
    }
}