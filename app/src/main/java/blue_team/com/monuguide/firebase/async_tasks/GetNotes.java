package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class GetNotes extends AsyncTask<String,Void,Void> {

    private String mMonId;
    private FireHelper mFireHelper;
    private Query mGetNotesQuery;
    Long server_timestamp = new Date().getTime();
    private HashMap<String, Note> mNote;

    private ValueEventListener noteValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNote.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNote.put(key,addVal);
            }
            mFireHelper.getOnNoteSuccessListener().onSuccess(mNote);
            mGetNotesQuery.removeEventListener(noteValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public GetNotes(String monId, FireHelper fh) {
        this.mMonId = monId;
        this.mFireHelper = fh;
        mNote = new HashMap<>();
    }

    @Override
    protected Void doInBackground(String... params) {

        mGetNotesQuery = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonId).child("notes").orderByChild("datetime").startAt(0);
        mGetNotesQuery.addValueEventListener(noteValueEventListener);
        return null;
    }
}
