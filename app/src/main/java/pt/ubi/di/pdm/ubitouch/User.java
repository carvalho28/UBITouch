package pt.ubi.di.pdm.ubitouch;

public class User {
    private final String name;
    private final String username;
    private final String image;

    public User(String name, String username, String image)
    {
        this.name = name;
        this.username = username;
        this.image = image;
    }

    public String getName() { return name;}

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }
}
