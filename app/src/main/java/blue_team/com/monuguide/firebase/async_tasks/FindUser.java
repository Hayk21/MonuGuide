package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.User;

public class FindUser extends AsyncTask<Void, Void, Void>
{
    private String mUserID;
    private FireHelper mFireHelper;
    private Query mFindUserQuery;
    private HashMap<String,User> mUser;

    private ValueEventListener userValueEventListener = new ValueEventListener()
    {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                User addVal = mySnapshot.getValue(User.class);
                String key = mySnapshot.getKey();
                mUser.put(key, addVal);
            }
            mFireHelper.getOnFindUserSuccessListener().onSuccess(mUser);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public FindUser(String userID, FireHelper fh) {
        this.mUserID = userID;
        this.mFireHelper = fh;
        mUser = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFindUserQuery = mFireHelper.getmDatabase().child("models").child("users").orderByKey().equalTo(mUserID);
        mFindUserQuery.addValueEventListener(userValueEventListener);
        return null;
    }
}
