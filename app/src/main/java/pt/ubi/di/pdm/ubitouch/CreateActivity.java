package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    ImageView profilePicture;
    Button btnCreatePost, btnAttachFile, btnPickDate, btnPickTime;
    TextInputEditText createTitle, createDescription;
    TextView dateText, timeText, msgError;
    DatePickerDialog datePickerDialog;

    // DEBUG
    private final String TAG = "Diogo";

    // Time
    int hour, minute;

    // Variables
    private String userId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // profilePicture = findViewById(R.id.imageViewProfilePicture);

        createTitle = findViewById(R.id.createTitle);
        createDescription = findViewById(R.id.createDescription);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        msgError = findViewById(R.id.msgError);
        btnCreatePost = findViewById(R.id.btnCreatePost);

        // shared preferences
        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = sharedPref.getString("id", "false");
        token = sharedPref.getString("token", "false");

        Log.i(TAG, "CreateActivity: onCreate(): User ID: " + userId);
        Log.i(TAG, "CreateActivity: onCreate(): Token: " + token);

        initDatePicker();

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
        String url = "https://server-ubi-touch.herokuapp.com/events/create";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            String selectedDate = convertDate(dateText.getText().toString());
            String selectedTime = convertTime(timeText.getText().toString());
            jsonBody.put("title", createTitle.getText().toString());
            jsonBody.put("description", createDescription.getText().toString());
            jsonBody.put("image", "");
            jsonBody.put("idUser", userId);
            jsonBody.put("eventDate", selectedDate);
            jsonBody.put("eventHour", selectedTime);
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
}