package blue_team.com.monuguide.firebase;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;
import blue_team.com.monuguide.models.User;

public class FireHelper {

    private static int count = 0;
    private int NotesListSize;
    private double mLat;
    private double mLon;
    private double mRad;
    private String imageUrl;
    private Monument mMonument;
    private Note note;
    private Query mQuery1;
    private Query mQuery2;
    private Query mQuery3;
    private Query mQuery4;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private IOnSuccessListener mOnSuccessListener;
    private IOnNoteSuccessListener mOnNoteSuccessListener;
    private IOnSearchSuccessListener mOnSearchSuccessListener;
    private HashMap<String,Monument> mMon = new HashMap<>();
    private HashMap<String,Note> mNote = new HashMap<>();



    public FireHelper()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }
    private ValueEventListener monValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
            }
            count = 1;
            mDatabase1 = mQuery1.getRef();
            getMonuments(mLat,mLon,mRad);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener monValueEventListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            count = 0;
            mOnSuccessListener.onSuccess(mMon);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener monValueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            mOnSearchSuccessListener.onSuccess(mMon);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener noteValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNote.put(key,addVal);
            }
            mOnNoteSuccessListener.onSuccess(mNote);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void getMonuments(double pLatitude, double pLongitude, double pRadius)
    {
        if(count == 0) {
            mLat = pLatitude;
            mLon = pLongitude;
            mRad = pRadius;
        }
        GetMonumentList gml = new GetMonumentList();
        gml.execute();
    }
    private class GetMonumentList extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            if(count == 0) {
                mQuery1 = mDatabase.child("models").child("monuments").orderByChild("latitude").startAt(mLat - mRad).endAt(mLat + mRad);
                mQuery1.addValueEventListener(monValueEventListener);
            }
            else{
                mQuery2 = mDatabase1.orderByChild("longitude").startAt(mLon - mRad).endAt(mLon + mRad);
                mQuery2.addValueEventListener(monValueEventListener1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getSearchMonument(String s)
    {
        GetSearchMonument gsm = new GetSearchMonument();
        gsm.execute(s);
    }

    private class GetSearchMonument extends AsyncTask<String,Void,Void>
    {
        String monName;
        @Override
        protected Void doInBackground(String... params) {
            for(String s : params)
            {
                monName = s;
            }
            mQuery3 = mDatabase.child("models").child("monuments").orderByChild("name").startAt(monName).endAt(monName+"\uf8ff");
            mQuery3.addValueEventListener(monValueEventListener2);
            return null;
        }
    }

    public void addUser(User user)
    {
        AddUser au = new AddUser();
        au.execute(user);
    }

    private class AddUser extends AsyncTask<User,Void,Void>
    {
        User user;

        @Override
        protected Void doInBackground(User... params) {
            for(User u : params)
            {
                user = u;
            }
            mDatabase.child("models").child("users").child(user.getuID()).setValue(user);
            return null;
        }

    }

    public void addNote(Bitmap bitmap, Monument monument,int size)
    {
        mMonument = monument;
        NotesListSize = size;
        AddNote sn = new AddNote();
        sn.execute(bitmap);
    }

    private class AddNote extends AsyncTask<Bitmap,Void,Void>
    {
        Bitmap bitmap;
        @Override
        protected Void doInBackground(Bitmap... params) {
            for(Bitmap b : params)
            {
                bitmap = b;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data = baos.toByteArray();
            String s = mMonument.getId()+"Note";
            s+=(NotesListSize+1);
            note = new Note();
            note.setId(s);
            UploadTask uploadTask = mStorageRef.child("noteImages/"+s).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageUrl=downloadUrl.toString();
                    note.setImage(imageUrl);
                    note.setLikeCount(0);
                    mDatabase.child("models").child("monuments").child(mMonument.getId()).child("notes").child(note.getId()).setValue(note);

                }
            });

            return null;
        }
    }

    public void getNotesList(String monId)
    {
        GetNotes gn = new GetNotes();
        gn.execute(monId);
    }
    private class GetNotes extends AsyncTask<String,Void,Void> {
        String monId;

        @Override
        protected Void doInBackground(String... params) {
            for (String b : params) {
                monId = b;
            }
            mQuery3 = mDatabase.child("models").child("monuments").child(monId).child("notes");
            mQuery3.addValueEventListener(noteValueEventListener);
            return null;
        }
    }

    public void setOnSuccessListener(IOnSuccessListener onSuccessListener) {
        mOnSuccessListener = onSuccessListener;
    }

    public interface IOnSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public void setOnNoteSuccessListener(IOnNoteSuccessListener onNoteSuccessListener){
        mOnNoteSuccessListener = onNoteSuccessListener;
    }

    public interface IOnNoteSuccessListener {
        void onSuccess(HashMap<String,Note> mMap);
    }

    public void setOnSearchSuccessListener(IOnSearchSuccessListener onSearchSuccessListener){
        mOnSearchSuccessListener = onSearchSuccessListener;
    }

    public interface IOnSearchSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }
}