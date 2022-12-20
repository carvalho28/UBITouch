package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Diogo", String.valueOf(Configs.isConfigInitialized));

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // TO REMOVE token, id, username
//        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
//        sharedPref.edit().clear().apply();
        if (!Configs.isConfigInitialized) {
            Configs.initConfig(this);
        }


        // on click go to register activity
        btnRegister.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, RegisterActivity.class);


                    startActivity(intent);
                });

        // on click go to login activity
        btnLogin.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
    }

//    private void initConfig() {
//        Map config = new HashMap();
//        config.put("cloud_name", CLOUD_NAME);
//        config.put("api_key", "966681439871748");
//        config.put("api_secret", "QYlGWWg5A9I7JSt4D0r4GXdnG6w");
//        // config.put("secure", true);
//        MediaManager.init(this, config);
//        isConfigInitialized = true;
//    }
}