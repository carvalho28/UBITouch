package pt.ubi.di.pdm.ubitouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity {

    //private final String username;
    private final String title;
    private final String date;
    private final String image;
    

    public PostActivity(String title, String date, String image)
    {
        // this.username = username;
        this.title = title;
        this.date = date;
        this.image = image;
    }

    // public String getUsername() {
    //     return username;
    // }

    public String getDate() {
        return date;
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