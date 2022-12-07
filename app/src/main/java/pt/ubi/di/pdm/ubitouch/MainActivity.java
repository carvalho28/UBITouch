package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMain = findViewById(R.id.btnMain);

        // TO REMOVE token, id, username
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();


        // on click go to register activity
        btnMain.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
    }
}