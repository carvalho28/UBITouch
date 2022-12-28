package pt.ubi.di.pdm.ubitouch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchLightDark;
    SwitchCompat switchNotification;
    ImageButton languageSetting;
    ImageButton securityPrivacySetting;
    ImageButton aboutUsSetting;
    ConstraintLayout logoutSetting;
    ImageButton back;
    ImageView notificationIcon;
    TextView notificationText;

    ImageView languageIcon;
    ImageView securityIcon;
    ImageView aboutUsIcon;
    ImageView logOutIcon;

    DarkMode darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchLightDark = findViewById(R.id.switchLD);
        switchNotification = findViewById(R.id.switchN);
        languageSetting = findViewById(R.id.changePasswordSetting);
        securityPrivacySetting = findViewById(R.id.securityPrivacySetting);
        aboutUsSetting = findViewById(R.id.aboutUsSetting);
        logoutSetting = findViewById(R.id.logoutSetting);
        darkmode = new DarkMode(this);
        back = findViewById(R.id.btnNotif);
        notificationIcon = findViewById(R.id.notificationIcon);
        notificationText = findViewById(R.id.notificationText);
        languageIcon = findViewById(R.id.language);
        securityIcon = findViewById(R.id.security);
        aboutUsIcon = findViewById(R.id.about_us);
        logOutIcon = findViewById(R.id.imageView2);

        if (darkmode.loadDarkMode() == true) {
            switchLightDark.setChecked(true);
        }

        // check if notification is enabled
        SharedPreferences sharedPref = getSharedPreferences("user", MODE_PRIVATE);
        String notification = sharedPref.getString("notification", "x");
        String isAdmin = sharedPref.getString("isAdmin", "0");
        if (isAdmin.equals("1")) {
            switchNotification.setVisibility(SwitchCompat.VISIBLE);
            notificationIcon.setVisibility(SwitchCompat.VISIBLE);
            notificationText.setVisibility(SwitchCompat.VISIBLE);
        } else {
            switchNotification.setVisibility(SwitchCompat.GONE);
            notificationIcon.setVisibility(SwitchCompat.GONE);
            notificationText.setVisibility(SwitchCompat.GONE);

            languageIcon.setBackgroundTintList(getResources().getColorStateList(R.color.orange));
            aboutUsIcon.setBackgroundTintList(getResources().getColorStateList(R.color.orange));

            if (darkmode.loadDarkMode() == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                securityIcon.setBackgroundTintList(getResources().getColorStateList(R.color.babyBlue));
                logOutIcon.setBackgroundTintList(getResources().getColorStateList(R.color.babyBlue));
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                securityIcon.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
                logOutIcon.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
            }
        }
        if (Objects.equals(notification, "1") || Objects.equals(notification, "x")) {
            switchNotification.setChecked(true);
        } else {
            switchNotification.setChecked(false);
        }

        back.setOnClickListener(
                v -> {
                    finish();
                });

        // Mudar theme
        switchLightDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                darkmode.setDarkmodeState(true);
                finish();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                darkmode.setDarkmodeState(false);
                finish();
            }


        });

        // switch notification to enable or disable
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // enable notifications
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("UBITouch", "UBITouch",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                    // save notification state
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("notification", "1");
                    editor.apply();
                }
            } else {
                // disable notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.deleteNotificationChannel("UBITouch");
                    // save notification state
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("notification", "0");
                    editor.apply();
                }
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

        aboutUsSetting.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, AboutUsActivity.class);

                    startActivity(intent);
                });

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
