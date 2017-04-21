package blue_team.com.monuguide.firebase.async_tasks;


import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Note;

public class FindNoteById extends AsyncTask<Void, Void, Void>{

    private String mNoteId;
    private String mMonumentId;
    private FireHelper mFireHelper;
    private Query mFindNoteById;
    private HashMap<String, Note> mNote;

    private ValueEventListener findNoteValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNote.put(key,addVal);
            }
            mFireHelper.getOnFindNoteListener().onSuccess(mNote);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public FindNoteById(String noteId, String monumentId, FireHelper fh) {
        this.mNoteId = noteId;
        this.mMonumentId = monumentId;
        this.mFireHelper = fh;
        mNote = new HashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        findNoteById(mNoteId, mMonumentId);
        return null;
    }

    private void findNoteById(String mNoteId , String mMonumentId)
    {
        mFindNoteById = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
        mFindNoteById.addValueEventListener(findNoteValueEventListener);
    }
}
