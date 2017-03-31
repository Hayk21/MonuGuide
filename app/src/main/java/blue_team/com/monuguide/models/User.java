package blue_team.com.monuguide.models;


import java.util.HashMap;

public class User {
    private String uID;
    private String name;
    private String email;
    private String photoUrl;
    private HashMap<String,Monument> favoriteMon = new HashMap<>();

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {

        this.photoUrl = photoUrl;
    }

    public HashMap<String, Monument> getFavoriteMon() {
        return favoriteMon;
    }

    public void setFavoriteMon(HashMap<String, Monument> favoriteMon) {
        this.favoriteMon = favoriteMon;
    }
}
