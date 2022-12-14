package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity {

    //private final String username;
    private final String title;
    private final String eventDate;
    private final String image;
    private final String description;
    private final String eventHour;
    private final String verifiedFlag;
    private final String unverifiedFlag;



    public PostActivity(String title, String image, String description, String eventHour, String eventDate, String verifiedFlag, String unverifiedFlag)
    {
        // this.username = username;
        this.title = title;
        this.eventDate = eventDate;
        this.image = image;
        this.description = description;
        this.eventHour = eventHour;
        this.verifiedFlag = verifiedFlag;
        this.unverifiedFlag = unverifiedFlag;
    }

    // public String getUsername() {
    //     return username;
    // }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() { return description; }

    public String getEventDate() { return eventDate; }

    public String getEventHour() { return eventHour; }

    public String getVerifiedFlag() { return verifiedFlag; }

    public String getUnverifiedFlag() { return unverifiedFlag; }

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