package blue_team.com.monuguide.firebase;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;
import blue_team.com.monuguide.models.User;

public class
FireHelper {

    private static int count = 0;
    private int mNotesListSize;
    private double mLat;
    private double mLon;
    private double mRad;
    private String imageUrl;
    private String mUserID;
    private String mNoteId;
    private String mMonumentId;
    private String mUserName;
    private Monument mMonument;
    private Note note;
    private Query mQuery1;
    private Query mQuery2;
    private Query mQuery3;
    private Query mQuery4;
    private Query mQuery5;
    private Query mQuery6;
    private Query mQuery7;
    private Query mQuery8;
    private Query mQuery9;
    private Query mQuery10;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private IOnSuccessListener mOnSuccessListener;
    private IOnNoteSuccessListener mOnNoteSuccessListener;
    private IOnSearchSuccessListener mOnSearchSuccessListener;
    private IOnFavMonSuccessListener mOnFavMonSuccessListener;
    private IOnFindUserSuccessListener mOnFindUserSuccessListener;
    private IOnFindFavMonSuccessListener mOnFindFavMonSuccessListener;
    private IOnFindUserLikeSuccessListener mOnFindUserLikeSuccessListener;
    private HashMap<String,Monument> mMon = new HashMap<>();
    private HashMap<String,Note> mNote = new HashMap<>();
    private HashMap<String,User> mUser = new HashMap<>();
    private HashMap<String,String> mUserId = new HashMap<>();



    public FireHelper()
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

    private ValueEventListener monValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
            }
            count = 1;
            mDatabase1 = mQuery1.getRef();
            getMonuments(mLat,mLon,mRad);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener monValueEventListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            count = 0;
            mOnSuccessListener.onSuccess(mMon);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener monValueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key,addVal);
            }
            mOnSearchSuccessListener.onSuccess(mMon);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener noteValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNote.clear();
            for (DataSnapshot mySnapshot: dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNote.put(key,addVal);
            }
            mOnNoteSuccessListener.onSuccess(mNote);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener favMonValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mMon.clear();
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key, addVal);
            }
            mOnFavMonSuccessListener.onSuccess(mMon);
            mQuery5.removeEventListener(favMonValueEventListener);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener userValueEventListener = new ValueEventListener()
    {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                User addVal = mySnapshot.getValue(User.class);
                String key = mySnapshot.getKey();
                mUser.put(key, addVal);
            }
                mOnFindUserSuccessListener.onSuccess(mUser);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener findFavMonValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Monument addVal = mySnapshot.getValue(Monument.class);
                String key = mySnapshot.getKey();
                mMon.put(key, addVal);
            }
            mOnFindFavMonSuccessListener.onSuccess(mMon);
            mQuery7.removeEventListener(findFavMonValueEventListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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
                    setLikeCount(likeCount);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener subLikeCountValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNote.clear();
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                Note addVal = mySnapshot.getValue(Note.class);
                String key = mySnapshot.getKey();
                mNoteId = key;
                mNote.put(key, addVal);
            }
            if(mNote != null) {
                if (!mNote.isEmpty()) {
                    int likeCount = mNote.get(mNoteId).getLikeCount();
                    --likeCount;
                    setLikeCount(likeCount);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener findUserLikeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot mySnapshot : dataSnapshot.getChildren()) {
                String addVal = mySnapshot.getValue(String.class);
                String key = mySnapshot.getKey();
                mUserId.put(key, addVal);
            }
            mOnFindUserLikeSuccessListener.onSuccess(mUserId);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public String getCurrentUid() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if(u != null) {
            return u.getUid();
        }
        else{
            return null;
        }
    }

    public String getCurrentUserName(){
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if(u != null) {
            return u.getDisplayName();
        }
        else{
            return null;
        }
    }

    public void getMonuments(double pLatitude, double pLongitude, double pRadius)
    {
        if(count == 0) {
            mLat = pLatitude;
            mLon = pLongitude;
            mRad = pRadius;
        }
        GetMonumentList gml = new GetMonumentList();
        gml.execute();
    }
    private class GetMonumentList extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            if(count == 0) {
                mQuery1 = mDatabase.child("models").child("monuments").orderByChild("latitude").startAt(mLat - mRad).endAt(mLat + mRad);
                mQuery1.addValueEventListener(monValueEventListener);
            }
            else{
                mQuery2 = mDatabase1.orderByChild("longitude").startAt(mLon - mRad).endAt(mLon + mRad);
                mQuery2.addValueEventListener(monValueEventListener1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getSearchMonument(String s)
    {
        GetSearchMonument gsm = new GetSearchMonument();
        gsm.execute(s);
    }

    private class GetSearchMonument extends AsyncTask<String,Void,Void>
    {
        String monName;
        @Override
        protected Void doInBackground(String... params) {
            for(String s : params)
            {
                monName = s;
            }
            mQuery3 = mDatabase.child("models").child("monuments").orderByChild("name").startAt(monName).endAt(monName+"\uf8ff");
            mQuery3.addValueEventListener(monValueEventListener2);
            mMon.clear();
            return null;
        }
    }

    public void addUser(User user)
    {
        AddUser au = new AddUser();
        au.execute(user);
    }

    private class AddUser extends AsyncTask<User,Void,Void>
    {
        User user = new User();

        @Override
        protected Void doInBackground(User... params) {
            for(User u : params)
            {
                user = u;
            }
            mDatabase.child("models").child("users").child(user.getuID()).setValue(user);
            return null;
        }

    }

    public void findUser(String userID)
    {
        mUserID = userID;
        FindUser fu = new FindUser();
        fu.execute();
    }

    private class FindUser extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            mQuery6 = mDatabase.child("models").child("users").orderByKey().equalTo(mUserID);
            mQuery6.addValueEventListener(userValueEventListener);
            return null;
        }
    }

    public void findFavMon(String userID,Monument monument)
    {
        mUserID = userID;
        mMonument = monument;
        FindFavMon ffm = new FindFavMon();
        ffm.execute();
    }

    private class FindFavMon extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            mQuery7 = mDatabase.child("models").child("users").child(mUserID).child("favoriteMon").orderByKey().equalTo(mMonument.getId());
            mQuery7.addValueEventListener(findFavMonValueEventListener);
            return null;
        }
    }
    public void addNote(Bitmap bitmap, Monument monument,String userID,String userName, int size)
    {
        mUserID = userID;
        mUserName = userName;
        mMonument = monument;
        mNotesListSize = size;
        AddNote sn = new AddNote();
        sn.execute(bitmap);
    }

    private class AddNote extends AsyncTask<Bitmap,Void,Void>
    {
        Bitmap bitmap;
        @Override
        protected Void doInBackground(Bitmap... params) {
            for(Bitmap b : params)
            {
                bitmap = b;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data = baos.toByteArray();
            String s = mMonument.getId()+"Note";
            s+=(mNotesListSize+1);
            note = new Note();
            note.setId(s);
            note.setAutorName(mUserName);
            note.setUid(mUserID);
            UploadTask uploadTask = mStorageRef.child("noteImages/"+s).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageUrl=downloadUrl.toString();
                    note.setImage(imageUrl);
                    note.setLikeCount(0);

                    mDatabase.child("models").child("monuments").child(mMonument.getId()).child("notes").child(note.getId()).setValue(note);

                }
            });

            return null;
        }
    }

    public void addFavoriteMon(Monument monument,String userID)
    {
        mUserID = userID;
        mMonument = monument;
        AddFavoriteMon afm= new AddFavoriteMon();
        afm.execute();
    }

    private class AddFavoriteMon extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            mDatabase.child("models").child("users").child(mUserID).child("favoriteMon").child(mMonument.getId()).setValue(mMonument);
            return null;
        }
    }

    public void removeFavoriteMon(Monument monument, String userID)
    {
        mUserID = userID;
        mMonument = monument;
        RemoveFavoriteMon rfm = new RemoveFavoriteMon();
        rfm.execute();
    }

    private class RemoveFavoriteMon extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            mDatabase.child("models").child("users").child(mUserID).child("favoriteMon").child(mMonument.getId()).removeValue();
            return null;
        }
    }

    public void getFavMonList(String userID)
    {
        GetFavMonList gfml = new GetFavMonList();
        gfml.execute(userID);
    }

    private class GetFavMonList extends AsyncTask<String,Void,Void>
    {
        String myuser;
        @Override
        protected Void doInBackground(String... params) {
            for (String b : params) {
                myuser = b;
            }
            mQuery5 = mDatabase.child("models").child("users").child(myuser).child("favoriteMon");
            mQuery5.addValueEventListener(favMonValueEventListener);
            return null;

        }
    }

    public void getNotesList(String monId)
    {
        GetNotes gn = new GetNotes();
        gn.execute(monId);
    }
    private class GetNotes extends AsyncTask<String,Void,Void> {
        String monId;

        @Override
        protected Void doInBackground(String... params) {
            for (String b : params) {
                monId = b;
            }
            mQuery3 = mDatabase.child("models").child("monuments").child(monId).child("notes");
            mQuery3.addValueEventListener(noteValueEventListener);
            return null;
        }
    }

    public void addLike(String noteID, String userID, String monumentID)
    {
        mUserID = userID;
        AddLike al = new AddLike();
        al.execute(noteID, monumentID);
    }

    public class AddLike extends AsyncTask<String, Void, Void>
    {
        int i = 0;
        @Override
        protected Void doInBackground(String... params) {
            for (String b : params) {
                if (i == 0) {
                    mNoteId = b;
                    i++;
                }
                else{
                    mMonumentId = b;
                }
            }
            mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").child(mUserID).setValue(mUserID);
            mQuery8 = mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
            mQuery8.addValueEventListener(addLikeCountValueEventListener);
            return null;
        }
    }

    public void subLike(String noteID, String userID, String monumentID)
    {
        mUserID = userID;
        SubLike sl = new SubLike();
        sl.execute(noteID, monumentID);
    }

    public class SubLike extends AsyncTask<String, Void, Void>
    {
        int i = 0;
        @Override
        protected Void doInBackground(String... params) {
            for (String b : params) {
                if (i == 0) {
                    mNoteId = b;
                    i++;
                }
                else{
                    mMonumentId = b;
                }
            }
            mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").child(mUserID).removeValue();
            mQuery9 = mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").orderByKey().equalTo(mNoteId);
            mQuery9.addValueEventListener(subLikeCountValueEventListener);
            return null;
        }
    }

    private void setLikeCount(int likeCount)
    {
        SetLikeCount alc = new SetLikeCount();
        alc.execute(likeCount);
    }

    private class SetLikeCount extends AsyncTask<Integer, Void, Void>
    {
        int likeCount;
        @Override
        protected Void doInBackground(Integer... params) {
            for (int b : params) {
                likeCount = b;
            }
            mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("likeCount").setValue(likeCount);
            return null;
        }
    }

    public void findLikeUser(String noteID, String userID, String monumentID)
    {
        mUserID = userID;
        mMonumentId = monumentID;
        mNoteId = noteID;
        FindLikeUser flu = new FindLikeUser();
        flu.execute();

    }

    private class FindLikeUser extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            mQuery10 = mDatabase.child("models").child("monuments").child(mMonumentId).child("notes").child(mNoteId).child("like").orderByKey().equalTo(mUserID);
            mQuery10.addValueEventListener(findUserLikeValueEventListener);
            return null;
        }
    }

    public void setOnSuccessListener(IOnSuccessListener onSuccessListener) {
        mOnSuccessListener = onSuccessListener;
    }

    public interface IOnSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public void setOnNoteSuccessListener(IOnNoteSuccessListener onNoteSuccessListener){
        mOnNoteSuccessListener = onNoteSuccessListener;
    }

    public interface IOnNoteSuccessListener {
        void onSuccess(HashMap<String,Note> mMap);
    }

    public void setOnSearchSuccessListener(IOnSearchSuccessListener onSearchSuccessListener){
        mOnSearchSuccessListener = onSearchSuccessListener;
    }

    public interface IOnSearchSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public void setOnFavMonSuccessListener(IOnFavMonSuccessListener onFavMonSuccessListener) {
        mOnFavMonSuccessListener = onFavMonSuccessListener;
    }

    public interface IOnFavMonSuccessListener{
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public void setOnFindUserSuccessListener(IOnFindUserSuccessListener onFindUserSuccessListener) {
        mOnFindUserSuccessListener = onFindUserSuccessListener;
    }

    public interface IOnFindUserSuccessListener{
        void onSuccess(HashMap<String,User> mMap);
    }

    public void setOnFindFavMonSuccessListener(IOnFindFavMonSuccessListener onFindFavMonSuccessListener) {
        mOnFindFavMonSuccessListener = onFindFavMonSuccessListener;
    }

    public interface IOnFindFavMonSuccessListener{
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public void setOnFindUserLikeSuccessListener(IOnFindUserLikeSuccessListener onFindUserLikeSuccessListener) {
        mOnFindUserLikeSuccessListener = onFindUserLikeSuccessListener;
    }

    public interface IOnFindUserLikeSuccessListener{
        void onSuccess(HashMap<String,String> mMap);
    }


}