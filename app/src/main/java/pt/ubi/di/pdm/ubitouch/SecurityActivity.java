package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SecurityActivity extends AppCompatActivity {

    ImageButton changePasswordSetting;
    ImageButton deleteSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        changePasswordSetting = findViewById(R.id.changePasswordSetting);
        deleteSetting = findViewById(R.id.deleteSetting);

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