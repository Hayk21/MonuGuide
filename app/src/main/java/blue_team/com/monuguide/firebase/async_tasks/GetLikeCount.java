package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class GetLikeCount extends AsyncTask<String, Void, Void>
{
    private String mMonumentId;
    private String mNoteId;
    private FireHelper mFireHelper;
    private Query mGetLikeCountQuery;
    private HashMap<String, Note> mNote;

    private ValueEventListener getLikeCountValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNote.put(key, addVal);
            }
            if(mNote != null) {
                if (!mNote.isEmpty()) {
                    int likeCount = mNote.get(mNoteId).getLikeCount();
                    mFireHelper.getOnGetLikeCountSuccessListener().onSuccess(likeCount);
                }
                //mGetLikeCountQuery.removeEventListener(getLikeCountValueEventListener);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public GetLikeCount(String monumentId, String noteId, FireHelper fh) {
        this.mMonumentId = monumentId;
        this.mNoteId = noteId;
        this.mFireHelper = fh;
        mNote = new HashMap<>();
    }

    @Override
    protected Void doInBackground(String... params) {
        mGetLikeCountQuery = (mFireHelper.getmDatabase()).child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
        mGetLikeCountQuery.addValueEventListener(getLikeCountValueEventListener);
        return null;
    }
}
