package blue_team.com.monuguide.models;

/**
 * Created by seda on 3/12/17.
 */

public class Note {
    private String id;
    private String image;
    private double likeCount;

    public Note() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(double likeCount) {
        this.likeCount = likeCount;
    }
}
