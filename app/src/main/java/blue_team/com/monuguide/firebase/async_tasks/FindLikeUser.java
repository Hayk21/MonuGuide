package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;

public class FindLikeUser extends AsyncTask<Void, Void, Void>
{
    private static final String TAG = "FindLikeUser";
    private String mNoteId;
    private String mUserID;
    private String mMonumentId;
    private FireHelper mFireHelper;
    private Query mFindLikeUserQuery;
    private HashMap<String, String> mUserId;

    private ValueEventListener findUserLikeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getUserID(dataSnapshot);
            mFireHelper.getOnFindUserLikeSuccessListener().onSuccess(mUserId);
            mFindLikeUserQuery.removeEventListener(findUserLikeValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG,"findUserLikeValueEventListener on cancelled");
        }
    };

    private void getUserID(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
            String addVal = mySnapshot.getValue(String.class);
            String key = mySnapshot.getKey();
            mUserId.put(key, addVal);
        }
    }

    public FindLikeUser(String noteId, String userID, String monumentId, FireHelper fh) {
        this.mNoteId = noteId;
        this.mUserID = userID;
        this.mMonumentId = monumentId;
        this.mFireHelper = fh;
        mUserId = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        findLikeUser();
        return null;
    }

    private void findLikeUser()
    {
        mFindLikeUserQuery = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").orderByKey().equalTo(mUserID);
        mFindLikeUserQuery.addValueEventListener(findUserLikeValueEventListener);
    }
}
