package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class CreateActivity extends AppCompatActivity {

    ImageView profilePicture;
    Button btnCreatePost, btnAttachFile, btnPickDate, btnPickTime ;
    TextInputEditText createTitle, createDescription;
    TextView msgError;

    // DEBUG
    private final String TAG = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // profilePicture = findViewById(R.id.imageViewProfilePicture);

        createTitle = findViewById(R.id.createTitle);
        createDescription = findViewById(R.id.createDescription);
        msgError = findViewById(R.id.msgError);


        btnCreatePost.setOnClickListener(
                (v) -> {
                    if (createTitle.getText().toString().isEmpty()) {
                        Log.i(TAG, "CreateActivity: onCreate(): Title is empty");
                        msgError.setText(R.string.post_empty_title);
                        msgError.setVisibility(TextView.VISIBLE);
                    }

                    else if (createTitle.getText().toString().length() > 50) {
                        Log.i(TAG, "CreateActivity: onCreate(): Title is too long.");
                        msgError.setText(R.string.post_tmc_title);
                        msgError.setVisibility(TextView.VISIBLE);
                    }

                    else if (createDescription.getText().toString().length() > 50) {
                        Log.i(TAG, "CreateActivity: onCreate(): Description is too long.");
                        msgError.setText(R.string.post_tmc_desc);
                        msgError.setVisibility(TextView.VISIBLE);
                    }

                    else {
                        // API CALL
                    }
                }
        );
    }
}