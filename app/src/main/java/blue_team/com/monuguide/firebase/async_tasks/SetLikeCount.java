package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import blue_team.com.monuguide.firebase.FireHelper;

public class SetLikeCount extends AsyncTask<Void, Void, Void>
{
    private int mLikeCount;
    private String mMonumentId;
    private String mNoteId;
    private FireHelper mFireHelper;

    public SetLikeCount(int likeCount, String monumentId, String noteId, FireHelper fh) {
        this.mLikeCount = likeCount;
        this.mMonumentId = monumentId;
        this.mNoteId = noteId;
        this.mFireHelper = fh;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("likeCount").setValue(mLikeCount);
        return null;
    }
}
