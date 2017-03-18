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

public class FireHelper {

    private static int count = 0;
    private double mLat;
    private double mLon;
    private double mRad;
    private Query mQuery1;
    private Query mQuery2;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private IOnSuccessListener mOnSuccessListener;
    private HashMap<String,Monument> mMon = new HashMap<>();



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

    public void getMonuments(double pLatitude, double pLongitude, double pRadius)
    {
        if(count == 0) {
            mLat = pLatitude;
            mLon = pLatitude;
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

    public void addNote(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.child("noteImages/bbb").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
    public void setOnSuccessListener(IOnSuccessListener onSuccessListener) {
        mOnSuccessListener = onSuccessListener;
    }

    public interface IOnSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }
}
