package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;

import blue_team.com.monuguide.firebase.FireHelper;

public class SetLikeCount extends AsyncTask<Void, Void, Void>
{
    private int mLikeCount;
    private String mMonumentId;
    private String mNoteId;
    private DatabaseReference mDatabase;

    public SetLikeCount(int likeCount, String monumentId, String noteId, DatabaseReference database) {
        this.mLikeCount = likeCount;
        this.mMonumentId = monumentId;
        this.mNoteId = noteId;
        this.mDatabase = database;
    }

    @Override
    protected Void doInBackground(Void... params) {
        setLikeCount();
        return null;
    }

    private void setLikeCount()
    {
        mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("likeCount").setValue(mLikeCount);
    }
}
