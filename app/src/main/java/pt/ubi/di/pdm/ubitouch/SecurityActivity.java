package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SecurityActivity extends AppCompatActivity {

    ImageButton changePasswordSetting;
    ImageButton deleteSetting;
    ImageButton back;
    ImageButton btnRemoveUsers;
    CardView cardViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        changePasswordSetting = findViewById(R.id.changePasswordSetting);
        deleteSetting = findViewById(R.id.deleteSetting);
        back = findViewById(R.id.btnNotif);

        cardViewUsers = findViewById(R.id.cardViewUsers);
        btnRemoveUsers = findViewById(R.id.btnRemoveUsers);

        // shared preferences to get isAdmin
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String isAdmin = sharedPreferences.getString("isAdmin", "0");

        if (isAdmin.equals("1")) {
            cardViewUsers.setVisibility(View.VISIBLE);
        } else {
            cardViewUsers.setVisibility(View.GONE);
        }

        btnRemoveUsers.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, AdminUsersActivity.class);
                    startActivity(intent);
                });

        back.setOnClickListener(
                v -> {
                    finish();
                });

        changePasswordSetting.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, ChangePasswordActivity.class);
                    startActivity(intent);
                });

        deleteSetting.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, DeleteActivity.class);
                    startActivity(intent);
                });
    }
}