package blue_team.com.monuguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;


public class Note implements Parcelable {
    private String uid;
    private String id;
    private String image;
    private int likeCount;
    private boolean like;

    public Note() {

    }

    protected Note(Parcel in) {

        uid = in.readString();
        id = in.readString();
        image = in.readString();
        likeCount = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(image);
        parcel.writeInt(likeCount);

    }
}
