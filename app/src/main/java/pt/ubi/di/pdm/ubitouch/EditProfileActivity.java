package pt.ubi.di.pdm.ubitouch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText firstName, lastName, bio;
    Button btnEditSave;
    ImageButton profileImage;
    ProgressBar progressBar;

    // Variables
    String userId;
    String token;
    Map config = new HashMap();
    private Uri imageUri;

    // Debug
    private final String TAG = "Diogo";

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    // reduce the image to fit the profile image
                    Picasso.get().load(imageUri).fit().centerCrop().into(profileImage);
                }
            });

    // URL of the API
    private final String URL = "https://server-ubi-touch.herokuapp.com/users/";
    private final String URLupdate = URL + "update/";

    // Cloudinary
    private final String CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dvo3vlsl3/image/upload";
    private final String CLOUD_NAME = "dvo3vlsl3";
    private final String UPLOAD_PRESET = "ubitouch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = findViewById(R.id.editProfileFirstName);
        lastName = findViewById(R.id.editProfileLastName);
        bio = findViewById(R.id.editProfileBio);
        btnEditSave = findViewById(R.id.editProfileButton);
        profileImage = findViewById(R.id.editProfileImage);
        progressBar = findViewById(R.id.editProfileProgressBar);

        // get the user id from the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        // print the user id
        Log.i("Diogo", "EditProfileActivity: onCreate() - userId: " + userId);
        Log.i("Diogo", "EditProfileActivity: onCreate() - token: " + token);

        initConfig();

        profileImage.setOnClickListener(
                v -> {
                    getImage();
                });

        getUserData(userId);

        btnEditSave.setOnClickListener(
                v -> {
                    updateProfile(userId);
                });
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    // update the user profile
    private void updateProfile(String userId) {
        Log.d(TAG, ": " + " button clicked");

        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d(TAG, "onStart: " + "started");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d(TAG, "onStart: " + "uploading");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                // get the image url
                String imageUrl = (String) resultData.get("url");
                // convert to https
                imageUrl = imageUrl.replace("http", "https");

                // make the request
                RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("firstName", firstName.getText().toString());
                    jsonBody.put("lastName", lastName.getText().toString());
                    jsonBody.put("biography", bio.getText().toString());
                    jsonBody.put("picture", imageUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.PUT,
                        URLupdate + userId,
                        jsonBody,
                        response -> {
                            Log.i("Diogo", "Response: " + response.toString());
                            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        },
                        error -> {
                            Log.e("Diogo", "Error: " + error.toString());
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                queue.add(jsonObjectRequest);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d(TAG, "onStart: " + error);
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d(TAG, "onStart: " + error);
            }
        }).dispatch();
    }

    // get data from the API using the user id
    private void getUserData(String userId) {
        // use url + userId to get the user data, and use the token to authenticate
        String url = URL + userId;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // print the response
                    Log.i("Diogo", "Response: " + response.toString());
                    try {
                        firstName.setText(response.getString("firstName"));
                        lastName.setText(response.getString("lastName"));
                        bio.setText(response.getString("biography"));
                        Picasso.get().load(response.getString("picture")).fit().centerCrop().into(profileImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "Erro no load");
                    }
                },
                error -> {
                    Log.e("Diogo", "Error: " + error.getMessage());
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void initConfig() {
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", "966681439871748");
        config.put("api_secret", "QYlGWWg5A9I7JSt4D0r4GXdnG6w");
        // config.put("secure", true);
        MediaManager.init(this, config);
    }
}