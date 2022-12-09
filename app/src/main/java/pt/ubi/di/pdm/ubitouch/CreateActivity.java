package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {

    ImageView profilePicture;
    Button btnCreatePost, btnAttachFile, btnPickDate, btnPickTime ;
    TextInputEditText createTitle, createDescription;
    TextView dateText, timeText, msgError;
    DatePickerDialog datePickerDialog;

    // DEBUG
    private final String TAG = "USER";

    // Time
    int hour, minute;

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

        initDatePicker();

        /* BACKEND STUFF
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
        */
    }

    // DATE PICKER ---------------------------------------------------------------------------------
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

    // TIME PICKER ---------------------------------------------------------------------------------
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


}