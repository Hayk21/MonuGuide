package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class SubLike extends AsyncTask<Void, Void, Void>
{
    private String mNoteId;
    private String mUserID;
    private String mMonumentId;
    private FireHelper mFireHelper;
    private Query mSubLikeCountQuery;
    private HashMap<String, Note> mNote;

    private ValueEventListener subLikeCountValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNote.clear();
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNoteId = key;
                mNote.put(key, addVal);
            }
            if(mNote != null) {
                if (!mNote.isEmpty()) {
                    int likeCount = mNote.get(mNoteId).getLikeCount();
                    --likeCount;
                    mFireHelper.setLikeCount(likeCount, mMonumentId, mNoteId);
                }
            }
            mSubLikeCountQuery.removeEventListener(subLikeCountValueEventListener);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public SubLike(String noteId, String userID, String monumentId, FireHelper fh) {
        this.mNoteId = noteId;
        this.mUserID = userID;
        this.mMonumentId = monumentId;
        this.mFireHelper = fh;
        mNote = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").child(mUserID).removeValue();
        mSubLikeCountQuery = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
        mSubLikeCountQuery.addValueEventListener(subLikeCountValueEventListener);
        return null;
    }
}

