package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.User;

public class FindUser extends AsyncTask<Void, Void, Void>
{
    private static final String TAG = "FindUser";
    private String mUserID;
    private FireHelper mFireHelper;
    private Query mFindUserQuery;
    private HashMap<String,User> mUser;

    private ValueEventListener userValueEventListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getUser(dataSnapshot);
            mFireHelper.getOnFindUserSuccessListener().onSuccess(mUser);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG,"userValueEventListener on cancelled");
        }
    };

    private void getUser(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
            User addVal = mySnapshot.getValue(User.class);
            String key = mySnapshot.getKey();
            mUser.put(key, addVal);
        }
    }

    public FindUser(String userID, FireHelper fh) {
        this.mUserID = userID;
        this.mFireHelper = fh;
        mUser = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        findUser();
        return null;
    }

    private void findUser()
    {
        mFindUserQuery = mFireHelper.getmDatabase().child("models").child("users").orderByKey().equalTo(mUserID);
        mFindUserQuery.addValueEventListener(userValueEventListener);
    }
}
