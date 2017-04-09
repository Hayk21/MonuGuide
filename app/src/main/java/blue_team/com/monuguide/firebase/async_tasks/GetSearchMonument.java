package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class GetSearchMonument extends AsyncTask<Void,Void,Void>
{
    private static int count = 0;
    private String mMonName;
    private FireHelper mFireHelper;
    private Query mGetSearchMonumentsQuery;
    private HashMap<String, Monument> mMon;

    private ValueEventListener monSearchByName1ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            count = 1;
            mFireHelper.getSearchMonument(mMonName);
            mGetSearchMonumentsQuery.removeEventListener(monSearchByName1ValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener monSearchByName2ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            count = 0;
            mFireHelper.getOnSearchSuccessListener().onSuccess(mMon);
            mGetSearchMonumentsQuery.removeEventListener(monSearchByName2ValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public GetSearchMonument(String monName, FireHelper fh)
    {
        this.mMonName = monName;
        this.mFireHelper = fh;
        mMon = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if(count == 0) {
            mGetSearchMonumentsQuery = mFireHelper.getmDatabase().child("models").child("monuments").orderByChild("searchName1").startAt(mMonName).endAt(mMonName + "\uf8ff");
            mGetSearchMonumentsQuery.addValueEventListener(monSearchByName1ValueEventListener);
        }
        else{
            mGetSearchMonumentsQuery = mFireHelper.getmDatabase().child("models").child("monuments").orderByChild("searchName2").startAt(mMonName).endAt(mMonName + "\uf8ff");
            mGetSearchMonumentsQuery.addValueEventListener(monSearchByName2ValueEventListener);
        }

        return null;
    }
}
