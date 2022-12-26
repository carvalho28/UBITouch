package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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

        langEn.setOnClickListener(
                v -> {
                    //TODO
                }
        );

        langFr.setOnClickListener(
                v -> {
                    //TODO
                }
        );

        langPt.setOnClickListener(
                v -> {
                    //TODO
                }
        );

        // Return to the previous activity
        back.setOnClickListener(
                v -> {
                    finish();
                }
        );
    }
}