package pt.ubi.di.pdm.ubitouch;

public class Event {

    // private final String username;
    private final String title;
    private final String eventDate;
    private final String image;
    private final String description;
    private final String eventHour;
    private final String isVerified;
    private final String latitude;
    private final String longitude;
    private final String name;
    private final String username;
    private final String eventId;
    private final String isInterested;
    private final String userID;
    private final String imageOrVideo;

    public Event(String title, String image, String description, String eventHour, String eventDate,
            String isVerified, String latitude, String longitude, String name,
            String username, String eventId, String isInterested, String userID, String imageOrVideo) {
        this.title = title;
        this.eventDate = eventDate;
        this.image = image;
        this.description = description;
        this.eventHour = eventHour;
        this.isVerified = isVerified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.username = username;
        this.eventId = eventId;
        this.isInterested = isInterested;
        this.userID = userID;
        this.imageOrVideo = imageOrVideo;
    }

    // public String getUsername() {
    // return username;
    // }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventHour() {
        return eventHour;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEventId() {
        return eventId;
    }

    public String getIsInterested() {
        return isInterested;
    }

    public String getUserID() { return userID; }

    public String getImageOrVideo() {
        return imageOrVideo;
    }

    /*
     * get from database:
     * - creator name
     * - post title
     * - post description
     * - post image/video
     * - post date
     * - post title
     * - post verification flag
     */
}