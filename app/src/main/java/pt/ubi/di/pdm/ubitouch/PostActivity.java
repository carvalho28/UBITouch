package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity {

    //private final String username;
    private final String title;
    private final String description;
    private final String date;
    private final String eventDate;
    private final String eventHour;
    private final String image;
    

    public PostActivity(String title, String description, String date, String eventDate, String eventHour, String image)
    {
        // this.username = username;
        this.title = title;
        this.description = description;
        this.date = date;
        this.eventDate = eventDate;
        this.eventHour = eventHour;
        this.image = image;
    }

    // public String getUsername() {
    //     return username;
    // }

    public String getDate() {
        return date;
    }

    public String getEventDescription() {
        return description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventHour() {
        return eventHour;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    /*
    get from database:
    - creator name
    - post title
    - post description
    - post image/video
    - post date
    - post title
    - post verification flag
    */
}