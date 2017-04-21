package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import blue_team.com.monuguide.firebase.FireHelper;

public class DeleteNote extends AsyncTask<Void, Void, Void>{

    private String mNoteID;
    private String mMonumentID;
    private FireHelper mFireHelper;

    public DeleteNote(String noteID, String monumentID, FireHelper fh)
    {
        this.mNoteID = noteID;
        this.mFireHelper = fh;
        this.mMonumentID = monumentID;
    }

    @Override
    protected Void doInBackground(Void... params) {
        deleteNote(mMonumentID, mNoteID);
        mFireHelper.getOnDeleteNoteListener().doingSomething(mNoteID);
        return null;
    }

    private void deleteNote(String mMonumentID, String mNoteID)
    {
        mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentID).child("notes").child(mNoteID).removeValue();
    }
}
