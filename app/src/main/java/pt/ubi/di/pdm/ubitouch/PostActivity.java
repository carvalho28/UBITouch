package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {

    ImageView postUserImage;
    TextView postUsername, postTitle, postDescription, postDate, postTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postUserImage = findViewById(R.id.postUserImage);
        postUsername = findViewById(R.id.postUsername);
        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        postDate = findViewById(R.id.postDate);
        postTime = findViewById(R.id.postTime);

        // BACKEND STUFF WORK IN PROGRESS
    }


}