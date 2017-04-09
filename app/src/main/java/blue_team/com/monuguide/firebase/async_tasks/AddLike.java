package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class AddLike extends AsyncTask<Void, Void, Void>
{
    private String mNoteId;
    private String mUserID;
    private String mMonumentId;
    private FireHelper mFireHelper;
    private Query mAddLikeCountQuery;
    private HashMap<String, Note> mNote;

    private ValueEventListener addLikeCountValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNote.clear();
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNoteId = key;
                mNote.put(key, addVal);
            }
            if(mNote!=null) {
                if (!mNote.isEmpty()) {
                    int likeCount = mNote.get(mNoteId).getLikeCount();
                    ++likeCount;
                    mFireHelper.setLikeCount(likeCount, mMonumentId, mNoteId);
                }
            }
            mAddLikeCountQuery.removeEventListener(addLikeCountValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public AddLike(String noteId, String userID, String monumentId, FireHelper fh) {
        this.mNoteId = noteId;
        this.mUserID = userID;
        this.mMonumentId = monumentId;
        this.mFireHelper = fh;
        mNote = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").child(mUserID).setValue(mUserID);
        mAddLikeCountQuery = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
        mAddLikeCountQuery.addValueEventListener(addLikeCountValueEventListener);
        return null;
    }
}
