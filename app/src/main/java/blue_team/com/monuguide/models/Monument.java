package blue_team.com.monuguide.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Monument implements Parcelable {

        private String id;
        private String name;
        private String desc;
        private String image;
        private HashMap<String,Note> notes;
        private String urlMon;
        private double latitude;
        private double longitude;

        public Monument() {

        }


    protected Monument(Parcel in) {
        id = in.readString();
        name = in.readString();
        desc = in.readString();
        image = in.readString();
        urlMon = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Monument> CREATOR = new Creator<Monument>() {
        @Override
        public Monument createFromParcel(Parcel in) {
            return new Monument(in);
        }

        @Override
        public Monument[] newArray(int size) {
            return new Monument[size];
        }
    };

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public HashMap<String, Note> getNotes() {
            return notes;
        }

        public void setNotes(HashMap<String, Note> notes) {
            this.notes = notes;
        }

        public String getUrlMon() {
            return urlMon;
        }

        public void setUrlMon(String urlMon) {
            this.urlMon = urlMon;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(desc);
        parcel.writeString(image);
        parcel.writeString(urlMon);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
