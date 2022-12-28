package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;

    DarkMode darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        darkmode = new DarkMode(this);

        if (darkmode.loadDarkMode() == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Diogo", String.valueOf(Configs.isConfigInitialized));

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // TO REMOVE token, id, username
        // SharedPreferences sharedPref = getSharedPreferences("user",
        // Context.MODE_PRIVATE);
        // sharedPref.edit().clear().apply();

        if (!Configs.isConfigInitialized) {
            Configs.initConfig(this);
        }

        // set language as the one saved in shared preferences
        // get phone language
        String language = Locale.getDefault().getLanguage();
        Log.d("Diogo", language);
        // get language from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("language", MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "");
        // if language is not set, set it to phone language
        if (lang.equals("")) {
            lang = language;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", lang);
            editor.apply();
        }
        Locale locale = new Locale(lang);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

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

        if (!NoInternet.isConnectedToInternet(this)) {
            NoInternet.showNoInternetDialog(this);
        }

        // get shared preferences, if id is not null, go to feed
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPref.getString("id", "");
        if (!id.equals("")) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }

    }
}