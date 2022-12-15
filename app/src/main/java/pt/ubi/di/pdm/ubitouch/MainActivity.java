package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnMain;
    // Cloudinary
    private final String CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dvo3vlsl3/image/upload";
    private final String CLOUD_NAME = "dvo3vlsl3";
    private final String UPLOAD_PRESET = "ubitouch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMain = findViewById(R.id.btnMain);

        // TO REMOVE token, id, username
//        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
//        sharedPref.edit().clear().apply();

        initConfig();


        // on click go to register activity
        btnMain.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, CreateActivity.class);

                    startActivity(intent);
                });
    }

    private void initConfig() {
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", "966681439871748");
        config.put("api_secret", "QYlGWWg5A9I7JSt4D0r4GXdnG6w");
        // config.put("secure", true);
        MediaManager.init(this, config);
    }
}