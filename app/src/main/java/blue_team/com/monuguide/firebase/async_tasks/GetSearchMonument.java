package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class GetSearchMonument extends AsyncTask<Void,Void,Void>
{
    private static final String TAG = "GetSearchMonument";
    private String mMonName;
    private FireHelper mFireHelper;
    private Query mGetSearchMonumentsQuery;
    private HashMap<String, Monument> mMon = new HashMap<>();;

    private ValueEventListener monSearchByName1ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            getMonument1(dataSnapshot);
            mGetSearchMonumentsQuery = mFireHelper.getmDatabase().child("models").child("monuments").orderByChild("searchName2").startAt(mMonName).endAt(mMonName + "\uf8ff");
            mGetSearchMonumentsQuery.addValueEventListener(monSearchByName2ValueEventListener);
            mGetSearchMonumentsQuery.removeEventListener(monSearchByName1ValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "monSearchByName1ValueEventListener on cancelled");
        }
    };

    private void getMonument1(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
            Monument addVal = mySnapshot.getValue(Monument.class);
            String key = mySnapshot.getKey();
            mMon.put(key,addVal);
        }
    }

    private ValueEventListener monSearchByName2ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getMonument2(dataSnapshot);
            mFireHelper.getOnSearchSuccessListener().onSuccess(mMon);
            mGetSearchMonumentsQuery.removeEventListener(monSearchByName2ValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "monSearchByName2ValueEventListener on cancelled");
        }
    };

    private void getMonument2(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
            Monument addVal = mySnapshot.getValue(Monument.class);
            String key = mySnapshot.getKey();
            mMon.put(key,addVal);
        }
    }

    public GetSearchMonument(String monName, FireHelper fh)
    {
        this.mMonName = monName;
        this.mFireHelper = fh;
    }

    @Override
    protected Void doInBackground(Void... params) {
        getSearchMonuments();
        return null;
    }

    private void getSearchMonuments()
    {
        mGetSearchMonumentsQuery = mFireHelper.getmDatabase().child("models").child("monuments").orderByChild("searchName1").startAt(mMonName).endAt(mMonName + "\uf8ff");
        mGetSearchMonumentsQuery.addValueEventListener(monSearchByName1ValueEventListener);
    }
}
