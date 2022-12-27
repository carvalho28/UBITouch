package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    TextView langEn, langFr, langPt;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        langEn = findViewById(R.id.langEn);
        langFr = findViewById(R.id.langFr);
        langPt = findViewById(R.id.langPt);

        back = findViewById(R.id.btnNotif);

        // prevent actual language from being selected
        SharedPreferences sharedPreferences = getSharedPreferences("language", MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "en");
        if (lang.equals("en")) {
            langEn.setEnabled(false);
        } else if (lang.equals("fr")) {
            langFr.setEnabled(false);
        } else if (lang.equals("pt")) {
            langPt.setEnabled(false);
        }

        langEn.setOnClickListener(
                v -> {
                    // set language to english
                    changeLanguage("en");
                });

        langFr.setOnClickListener(
                v -> {
                    changeLanguage("fr");
                });

        langPt.setOnClickListener(
                v -> {
                    changeLanguage("pt");
                });

        // Return to the previous activity
        back.setOnClickListener(
                v -> {
                    finish();
                });
    }

    private void changeLanguage(String language) {
        Locale locale = null;
        if (language.equals("en")) {
            locale = new Locale("en");
        } else if (language.equals("fr")) {
            locale = new Locale("fr");
        } else if (language.equals("pt")) {
            locale = new Locale("pt");
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // set language to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("language", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();

        Intent intent = new Intent(LanguageActivity.this, FeedActivity.class);
        startActivity(intent);
    }
}