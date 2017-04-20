package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class DeleteNote extends AsyncTask<Void, Void, Void>{

    private Note mNote;
    private String mMonumentID;
    private FireHelper mFireHelper;
    private Query mDeleteNote;

    public DeleteNote(Note note, String monumentID, FireHelper fh)
    {
        this.mNote = note;
        this.mFireHelper = fh;
        this.mMonumentID = monumentID;
    }

    @Override
    protected Void doInBackground(Void... params) {
        deleteNote(mMonumentID,mNote);
        mFireHelper.getOnDeleteNoteListener().doingSomething();
        return null;
    }

    private void deleteNote(String mMonumentID, Note mNote)
    {
        mDeleteNote = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentID).child("notes").orderByKey().equalTo(mNote.getId());
    }
}
