package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class GetFavMonList extends AsyncTask<Void,Void,Void>
{
    private String mMyuser;
    private FireHelper mFireHelper;
    private Query mGetFavMonListQuery;
    private HashMap<String, Monument> mMon;

    private ValueEventListener favMonValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key, addVal);
            }
            mFireHelper.getOnFavMonSuccessListener().onSuccess(mMon);
            mGetFavMonListQuery.removeEventListener(favMonValueEventListener);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public GetFavMonList(String myuser, FireHelper fh) {
        this.mMyuser = myuser;
        this.mFireHelper = fh;
        mMon = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mGetFavMonListQuery = mFireHelper.getmDatabase().child("models").child("users").child(mMyuser).child("favoriteMon");
        mGetFavMonListQuery.addValueEventListener(favMonValueEventListener);
        return null;

    }
}
