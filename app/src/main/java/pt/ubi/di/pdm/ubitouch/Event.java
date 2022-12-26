package pt.ubi.di.pdm.ubitouch;

public class Event {

    // private final String username;
    private final String title;
    private final String eventDate;
    private final String image;
    private final String description;
    private final String eventHour;
    private final String verifiedFlag;
    private final String unverifiedFlag;
    private final String latitude;
    private final String longitude;
    private final String name;
    private final String username;
    private final String eventId;
    private final String isInterested;

    public Event(String title, String image, String description, String eventHour, String eventDate,
            String verifiedFlag, String unverifiedFlag, String latitude, String longitude, String name,
            String username, String eventId, String isInterested) {
        this.title = title;
        this.eventDate = eventDate;
        this.image = image;
        this.description = description;
        this.eventHour = eventHour;
        this.verifiedFlag = verifiedFlag;
        this.unverifiedFlag = unverifiedFlag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.username = username;
        this.eventId = eventId;
        this.isInterested = isInterested;
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

    public String getVerifiedFlag() {
        return verifiedFlag;
    }

    public String getUnverifiedFlag() {
        return unverifiedFlag;
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