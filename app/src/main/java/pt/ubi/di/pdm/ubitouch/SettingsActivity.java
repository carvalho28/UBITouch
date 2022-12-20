package pt.ubi.di.pdm.ubitouch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchLightDark;
    SwitchCompat switchNotification;
    RelativeLayout languageSetting;
    RelativeLayout securityPrivacySetting;
    RelativeLayout aboutUsSetting;
    RelativeLayout logoutSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchLightDark = findViewById(R.id.switchLD);
        switchNotification = findViewById(R.id.switchN);
        languageSetting = findViewById(R.id.languageSetting);
        securityPrivacySetting = findViewById(R.id.securityPrivacySetting);
        aboutUsSetting = findViewById(R.id.aboutUsSetting);
        logoutSetting = findViewById(R.id.logoutSetting);

        // Verificar se o sistema se encontra em dark mode ou nao
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                switchLightDark.setChecked(true);
                break;
        }

        // Mudar theme
        switchLightDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        languageSetting.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, LanguageActivity.class);

                    startActivity(intent);
                });

        securityPrivacySetting.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, SecurityActivity.class);

                    startActivity(intent);
                });

        // aboutUsSetting.setOnClickListener(
        // v -> {
        // Intent intent = new Intent(this, AboutUsActivity.class);
        //
        // startActivity(intent);
        // });

        logoutSetting.setOnClickListener(
                v -> {
                    fcmTokenClear();
                });
    }

    private void fcmTokenClear() {
        SharedPreferences sharedPref = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String token = sharedPref.getString("token", "");
        String userId = sharedPref.getString("id", "");

        Log.d("Diogo", token);
        Log.d("Diogo", userId);

        String url = "https://server-ubi-touch.herokuapp.com/users/deleteFcmToken/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        // json request, sending token as header
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    String msg = null;
                    try {
                        msg = response.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (msg.equals("Fcm token deleted successfully")) {
                        // clear shared preferences
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, error -> {
                    Log.e("Volley", "Error");
                    // error
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        // add request to queue
        queue.add(jsonObjectRequest);
    }
}
