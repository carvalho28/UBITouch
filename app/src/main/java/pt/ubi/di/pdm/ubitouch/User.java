package pt.ubi.di.pdm.ubitouch;

public class User {
    private final String name;
    private final String username;
    private final String image;
    private final String userId;

    public User(String name, String username, String image, String userId)
    {
        this.name = name;
        this.username = username;
        this.image = image;
        this.userId = userId;
    }

    public String getName() { return name;}

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public String getUserId() {
        return userId;
    }
}
