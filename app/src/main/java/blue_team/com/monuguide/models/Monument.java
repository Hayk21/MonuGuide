package blue_team.com.monuguide.models;


import java.util.HashMap;

public class Monument {

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
}
