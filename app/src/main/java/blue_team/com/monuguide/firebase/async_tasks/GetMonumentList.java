package blue_team.com.monuguide.firebase.async_tasks;


import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class GetMonumentList extends AsyncTask<Void, Void, Void>
{
    private static int count = 0;
    private double mLatitude;
    private double mLongitude;
    private double mRadius;
    private FireHelper mFireHelper;
    private Query mGetMonumentsByLatitudeQuery;
    private Query mGetMonumentsByLongitudeQuery;
    private HashMap<String,Monument> mMon;

    private ValueEventListener getMonByLatitudeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
            }
            count = 1;
            mFireHelper.setmDatabase1(mGetMonumentsByLatitudeQuery.getRef());
            mFireHelper.getMonuments(mLatitude,mLongitude,mRadius);
            mGetMonumentsByLatitudeQuery.removeEventListener(getMonByLatitudeValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private ValueEventListener getMonByLongitudeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            count = 0;
            mFireHelper.getOnGetMonumentSuccessListener().onSuccess(mMon);
            mGetMonumentsByLongitudeQuery.removeEventListener(getMonByLongitudeValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public GetMonumentList(double latitude, double longitude, double radius, FireHelper fh) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mFireHelper = fh;
        mMon = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(count == 0) {
            mGetMonumentsByLatitudeQuery = mFireHelper.getmDatabase().child("models").child("monuments").orderByChild("latitude").startAt(mLatitude - mRadius).endAt(mLatitude + mRadius);
            mGetMonumentsByLatitudeQuery.addValueEventListener(getMonByLatitudeValueEventListener);
        }
        else{
            mGetMonumentsByLongitudeQuery = mFireHelper.getmDatabase1().orderByChild("longitude").startAt(mLongitude - mRadius).endAt(mLongitude + mRadius);
            mGetMonumentsByLongitudeQuery.addValueEventListener(getMonByLongitudeValueEventListener);
        }
        return null;
    }
}

