package pt.ubi.di.pdm.ubitouch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    ImageView profilePicture;
    Button btnCreatePost, btnDiscard;
    ImageButton btnAttachFile;
    TextInputEditText createTitle, createDescription;
    TextView dateText, timeText, msgError;
    DatePickerDialog datePickerDialog;
    ImageView createImage;
    VideoView createVideo;
    ProgressBar progressBar;

    // DEBUG
    private final String TAG = "Diogo";

    // Time
    int hour, minute;

    // Variables
    private String userId, token;
    boolean imageChanged = false;
    private Uri imageUri;

    // Intent to get image
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    // print the image URI
                    Log.i(TAG, "CreateActivity: onCreate(): Image URI: " + imageUri);
                    // if is a video
                    if (imageUri.toString().contains("video")) {
                        createVideo.setVisibility(View.VISIBLE);
                        createVideo.setVideoURI(imageUri);
                        // play the video in loop
                        createVideo.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                        });
                        createVideo.start();
                    }
                    // if is an image
                    else {
                        createImage.setVisibility(View.VISIBLE);
                        // set the image to the image view
                        Picasso.get().load(imageUri).into(createImage);
                    }
                    // createImage.setVisibility(View.VISIBLE);
                    // // set the image to the image view
                    // Picasso.get().load(imageUri).into(createImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        profilePicture = findViewById(R.id.profilePicture);

        createTitle = findViewById(R.id.createTitle);
        createDescription = findViewById(R.id.createDescription);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        msgError = findViewById(R.id.msgError);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        createImage = findViewById(R.id.createImage);
        createVideo = findViewById(R.id.createVideo);
        btnAttachFile = findViewById(R.id.btnAttachFile);
        btnDiscard = findViewById(R.id.btnDiscard);
        progressBar = findViewById(R.id.createProgressBar);

        // shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        Log.i(TAG, "CreateActivity: onCreate(): User ID: " + userId);
        Log.i(TAG, "CreateActivity: onCreate(): Token: " + token);

        initDatePicker();
        getUserData();

        // Discard
        btnDiscard.setOnClickListener(v -> {
            Intent intent = new Intent(CreateActivity.this, FeedActivity.class);
            startActivity(intent);
        });

        // Image
        btnAttachFile.setOnClickListener(v -> {
            getImageOrVideo();
        });

        // BACKEND STUFF
        btnCreatePost.setOnClickListener(
                v -> {
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
                        Log.i(TAG, "CreateActivity: onCreate(): Creating post...");
                        createPost();
                    }
                });
    }

    // DATE PICKER
    // ---------------------------------------------------------------------------------
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                m += 1;
                String date = getDateString(d, m, y);
                dateText.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // TODAY
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month += 1; // because months start at 0

        return getDateString(day, month, year);
    }

    private String getDateString(int day, int month, int year) {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return getString(R.string.january);
        if (month == 2)
            return getString(R.string.february);
        if (month == 3)
            return getString(R.string.march);
        if (month == 4)
            return getString(R.string.april);
        if (month == 5)
            return getString(R.string.may);
        if (month == 6)
            return getString(R.string.june);
        if (month == 7)
            return getString(R.string.july);
        if (month == 8)
            return getString(R.string.august);
        if (month == 9)
            return getString(R.string.september);
        if (month == 10)
            return getString(R.string.october);
        if (month == 11)
            return getString(R.string.november);
        if (month == 12)
            return getString(R.string.december);

        // Default
        return getString(R.string.january);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
    // ---------------------------------------------------------------------------------------------

    // TIME PICKER
    // ---------------------------------------------------------------------------------
    public void openTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                timeText.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle(getString(R.string.select_time));
        timePickerDialog.show();
    }
    // ---------------------------------------------------------------------------------------------

    // API Call
    private void createPost() {
        String selectedDate = convertDate(dateText.getText().toString());
        String selectedTime = convertTime(timeText.getText().toString());

        if (imageChanged) {
            // if it is an image or video
            if (imageUri.toString().contains("image")) {

                MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "onStart: " + "started");
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "onStart: " + "uploading");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // get the image url
                        String imageUrl = (String) resultData.get("url");
                        // convert to https
                        imageUrl = imageUrl.replace("http", "https");
                        // post the data to the API
                        postData(createTitle.getText().toString(), createDescription.getText().toString(), imageUrl,
                                selectedDate, selectedTime);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG, "onError: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d(TAG, "onStart: " + error);
                    }
                }).dispatch();
            } else {
                // if it is a video
                MediaManager.get().upload(imageUri).option("resource_type", "video").callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "onStart: " + "started");
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "onStart: " + "uploading");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // get the image url
                        String imageUrl = (String) resultData.get("url");
                        // convert to https
                        imageUrl = imageUrl.replace("http", "https");
                        // post the data to the API
                        postData(createTitle.getText().toString(), createDescription.getText().toString(), imageUrl,
                                selectedDate, selectedTime);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG, "onError: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d(TAG, "onStart: " + error);
                    }
                }).dispatch();
            }
        } else {
            postData(createTitle.getText().toString(), createDescription.getText().toString(), "",
                    selectedDate, selectedTime);
        }
    }

    private void postData(String title, String description, String image, String eventDate, String eventHour) {
        String url = "https://server-ubi-touch.herokuapp.com/events/create";
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("title", title);
            jsonBody.put("description", description);
            jsonBody.put("image", image);
            jsonBody.put("idUser", userId);
            jsonBody.put("eventDate", eventDate);
            jsonBody.put("eventHour", eventHour);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.i(TAG, "CreateActivity: createPost(): onResponse(): " + response.toString());

                    // go to feed
                    Intent intent = new Intent(CreateActivity.this, FeedActivity.class);
                    startActivity(intent);
                },
                error -> Log.i(TAG, "CreateActivity: createPost(): onErrorResponse(): " + error.toString())) {
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

    // convert date to mysql format
    private String convertDate(String date) {
        String[] dateArray = date.split(" ");
        String day = dateArray[0];
        String month = dateArray[1];
        String year = dateArray[2];

        if (month.equals(getString(R.string.january)))
            month = "01";
        else if (month.equals(getString(R.string.february)))
            month = "02";
        else if (month.equals(getString(R.string.march)))
            month = "03";
        else if (month.equals(getString(R.string.april)))
            month = "04";
        else if (month.equals(getString(R.string.may)))
            month = "05";
        else if (month.equals(getString(R.string.june)))
            month = "06";
        else if (month.equals(getString(R.string.july)))
            month = "07";
        else if (month.equals(getString(R.string.august)))
            month = "08";
        else if (month.equals(getString(R.string.september)))
            month = "09";
        else if (month.equals(getString(R.string.october)))
            month = "10";
        else if (month.equals(getString(R.string.november)))
            month = "11";
        else if (month.equals(getString(R.string.december)))
            month = "12";

        return year + "-" + month + "-" + day;
    }

    // convert time to mysql format
    private String convertTime(String time) {
        String[] timeArray = time.split(":");
        String hour = timeArray[0];
        String minute = timeArray[1];

        return hour + ":" + minute + ":00";
    }

    private void getImageOrVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "image/*", "video/*" });
        activityResultLauncher.launch(intent);
        imageChanged = true;
    }

    private void getUserData() {
        String url = "https://server-ubi-touch.herokuapp.com/users/" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.i(TAG, "CreateActivity: getUserData(): onResponse(): " + response.toString());
                    try {
                        Picasso.get().load(response.getString("picture")).into(profilePicture);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.i(TAG, "CreateActivity: getUserData(): onErrorResponse(): " + error.toString())) {
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
}