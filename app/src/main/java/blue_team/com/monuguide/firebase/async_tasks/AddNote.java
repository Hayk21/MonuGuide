package blue_team.com.monuguide.firebase.async_tasks;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;

public class AddNote extends AsyncTask<Void,Void,Void>
{
    private Bitmap mBitmap;
    private Monument mMonument;
    private String mUserID;
    private String mUserName;
    private String mNoteId;
    private FireHelper mFireHelper;
    private Note mNote;

    public AddNote(Bitmap bitmap, Monument monument, String userID,String userName, FireHelper fh) {
        this.mBitmap = bitmap;
        this.mMonument = monument;
        this.mUserID = userID;
        this.mUserName = userName;
        this.mFireHelper = fh;
        mNote = new Note();
    }

    @Override
    protected Void doInBackground(Void... params) {
        byte[] data = getBitmapImage();
        mNote = addNewNote();
        addImageInStorage(data);
        return null;
    }

    private byte[] getBitmapImage()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        mBitmap.recycle();
        mBitmap = null;
        byte[] data = baos.toByteArray();
        return data;
    }

    private Note addNewNote()
    {
        mNote = new Note();
        mNote.setAutorName(mUserName);
        mNote.setUid(mUserID);
        mNoteId = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonument.getId()).child("notes").push().getKey();
        mNote.setId(mNoteId);
        return mNote;
    }

    private void addImageInStorage(byte[] data)
    {
        UploadTask uploadTask = mFireHelper.getmStorageRef().child("noteImages/" + mNoteId).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mFireHelper.getmDatabase().child("models").child("monuments").child(mMonument.getId()).child("notes").child(mNoteId).removeValue();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String imageUrl = downloadUrl.toString();
                addNote(imageUrl);
                addNoteInDatabase();
            }
        });
    }

    private void addNote(String imageUrl)
    {
        mNote.setImage(imageUrl);
        mNote.setLikeCount(0);
        mNote.setDatetime(System.currentTimeMillis());
    }

    private void addNoteInDatabase()
    {
        mFireHelper.getmDatabase().child("models").child("monuments").child(mMonument.getId()).child("notes").child(mNote.getId()).setValue(mNote);
        mFireHelper.getOnOperationEndListener().doingSomething();

    }
}
