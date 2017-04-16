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

public class FindFavMon extends AsyncTask<Void, Void, Void>
{
    private static final String TAG = "FindFavMon";
    private String mUserID;
    private Monument mMonument;
    private FireHelper mFireHelper;
    private Query mFindFavMonQuery;
    private HashMap<String, Monument> mMon;

    private ValueEventListener findFavMonValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getFavMonument(dataSnapshot);
            mFireHelper.getOnFindFavMonSuccessListener().onSuccess(mMon);
            mFindFavMonQuery.removeEventListener(findFavMonValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG,"findFavMonValueEventListener on cancelled");
        }
    };

    private void getFavMonument(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
            Monument addVal = mySnapshot.getValue(Monument.class);
            String key = mySnapshot.getKey();
            mMon.put(key, addVal);
        }
    }

    public FindFavMon(String userID, Monument monument, FireHelper fh) {
        this.mUserID = userID;
        this.mMonument = monument;
        this.mFireHelper = fh;
        mMon = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        findFavMonument();
        return null;
    }

    private void findFavMonument()
    {
        mFindFavMonQuery = mFireHelper.getmDatabase().child("models").child("users").child(mUserID).child("favoriteMon").orderByKey().equalTo(mMonument.getId());
        mFindFavMonQuery.addValueEventListener(findFavMonValueEventListener);
    }
}
