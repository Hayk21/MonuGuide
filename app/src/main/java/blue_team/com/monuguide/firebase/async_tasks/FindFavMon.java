package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class FindFavMon extends AsyncTask<Void, Void, Void>
{
    private String mUserID;
    private Monument mMonument;
    private FireHelper mFireHelper;
    private Query mFindeFavMonQuery;
    private HashMap<String, Monument> mMon;

    private ValueEventListener findFavMonValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key, addVal);
            }
            mFireHelper.getOnFindFavMonSuccessListener().onSuccess(mMon);
            mFindeFavMonQuery.removeEventListener(findFavMonValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public FindFavMon(String userID, Monument monument, FireHelper fh) {
        this.mUserID = userID;
        this.mMonument = monument;
        this.mFireHelper = fh;
        mMon = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFindeFavMonQuery = mFireHelper.getmDatabase().child("models").child("users").child(mUserID).child("favoriteMon").orderByKey().equalTo(mMonument.getId());
        mFindeFavMonQuery.addValueEventListener(findFavMonValueEventListener);
        return null;
    }
}
