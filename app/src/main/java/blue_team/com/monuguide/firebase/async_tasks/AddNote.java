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
    private int mSize;
    private FireHelper mFireHelper;
    private Note mNote;
    public interface operationEndListener{
        void doingSomething();
    }
    private operationEndListener mOperationEndListener;

    public AddNote(Bitmap bitmap, Monument monument, String userID,String userName, int size, FireHelper fh,operationEndListener mOperationEndListener) {
        this.mBitmap = bitmap;
        this.mMonument = monument;
        this.mUserID = userID;
        this.mUserName = userName;
        this.mSize = size;
        this.mFireHelper = fh;
        this.mOperationEndListener = mOperationEndListener;
        mNote = new Note();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        mBitmap.recycle();
        mBitmap = null;
        byte[] data = baos.toByteArray();
        //String s = mMonument.getId() + "Note";
        //s += (mSize + 1);
        mNote = new Note();
        mNote.setAutorName(mUserName);
        mNote.setUid(mUserID);
        mNoteId = mFireHelper.getmDatabase().child("models").child("monuments").child(mMonument.getId()).child("notes").push().getKey();
        mNote.setId(mNoteId);
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
                mNote.setImage(imageUrl);
                mNote.setLikeCount(0);

                mFireHelper.getmDatabase().child("models").child("monuments").child(mMonument.getId()).child("notes").child(mNote.getId()).setValue(mNote);
                mOperationEndListener.doingSomething();
                }
            });

        return null;
    }

}
