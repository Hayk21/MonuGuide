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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import blue_team.com.monuguide.firebase.async_tasks.AddFavoriteMon;
import blue_team.com.monuguide.firebase.async_tasks.AddLike;
import blue_team.com.monuguide.firebase.async_tasks.AddNote;
import blue_team.com.monuguide.firebase.async_tasks.AddUser;
import blue_team.com.monuguide.firebase.async_tasks.FindFavMon;
import blue_team.com.monuguide.firebase.async_tasks.FindLikeUser;
import blue_team.com.monuguide.firebase.async_tasks.FindUser;
import blue_team.com.monuguide.firebase.async_tasks.GetFavMonList;
import blue_team.com.monuguide.firebase.async_tasks.GetLikeCount;
import blue_team.com.monuguide.firebase.async_tasks.GetMonumentList;
import blue_team.com.monuguide.firebase.async_tasks.GetNotes;
import blue_team.com.monuguide.firebase.async_tasks.GetSearchMonument;
import blue_team.com.monuguide.firebase.async_tasks.RemoveFavoriteMon;
import blue_team.com.monuguide.firebase.async_tasks.SetLikeCount;
import blue_team.com.monuguide.firebase.async_tasks.SubLike;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;
import blue_team.com.monuguide.models.User;

public class FireHelper {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private IOnGetMonumentListSuccessListener mOnGetMonumentListSuccessListener;
    private IOnNoteSuccessListener mOnNoteSuccessListener;
    private IOnSearchSuccessListener mOnSearchSuccessListener;
    private IOnFavMonSuccessListener mOnFavMonSuccessListener;
    private IOnFindUserSuccessListener mOnFindUserSuccessListener;
    private IOnFindFavMonSuccessListener mOnFindFavMonSuccessListener;
    private IOnFindUserLikeSuccessListener mOnFindUserLikeSuccessListener;
    private IOnGetLikeCountSuccessListener mOnGetLikeCountSuccessListener;
    private IOnOperationEndListener mOnOperationEndListener;

    public FireHelper()
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

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

    public void getMonuments(double latitude, double longitude, double radius)
    {
        GetMonumentList gml = new GetMonumentList(latitude,longitude,radius,this);
        gml.execute();
    }

    public void getSearchMonument(String monName)
    {
        GetSearchMonument gsm = new GetSearchMonument(monName, this);
        gsm.execute();
    }

    public void addUser(User user)
    {
        AddUser au = new AddUser(user,this);
        au.execute();
    }

    public void findUser(String userID)
    {
        FindUser fu = new FindUser(userID,this);
        fu.execute();
    }

    public void findFavMon(String userID,Monument monument)
    {
        FindFavMon ffm = new FindFavMon(userID, monument, this);
        ffm.execute();
    }

    public void addNote(Bitmap bitmap, Monument monument, String userID, String userName, int size)
    {
        AddNote sn = new AddNote(bitmap, monument, userID, userName, this);
        sn.execute();
    }

    public void addFavoriteMon(Monument monument,String userID)
    {
        AddFavoriteMon afm= new AddFavoriteMon(userID, monument, this);
        afm.execute();
    }

    public void removeFavoriteMon(Monument monument, String userID)
    {
        RemoveFavoriteMon rfm = new RemoveFavoriteMon(userID, monument, this);
        rfm.execute();
    }

    public void getFavMonList(String userID)
    {
        GetFavMonList gfml = new GetFavMonList(userID, this);
        gfml.execute();
    }

    public void getNotesList(String monId)
    {
        GetNotes gn = new GetNotes(monId, this);
        gn.execute();
    }

    public void addLike(String noteID, String userID, String monumentID)
    {
        AddLike al = new AddLike(noteID, userID, monumentID, this);
        al.execute();
    }

    public void subLike(String noteID, String userID, String monumentID)
    {
        SubLike sl = new SubLike(noteID, userID, monumentID, this);
        sl.execute();
    }

    public void setLikeCount(int likeCount, String monumentId, String noteId)
    {
        SetLikeCount alc = new SetLikeCount(likeCount, monumentId, noteId, mDatabase);
        alc.execute();
    }

    public void findLikeUser(String noteID, String userID, String monumentID)
    {
        FindLikeUser flu = new FindLikeUser(noteID, userID, monumentID, this);
        flu.execute();
    }

    public void getLikeCount(String monumentID, String noteID)
    {
        GetLikeCount glc = new GetLikeCount(monumentID, noteID,  this);
        glc.execute();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public DatabaseReference getmDatabase1() {
        return mDatabase1;
    }

    public void setmDatabase1(DatabaseReference mDatabase1) {
        this.mDatabase1 = mDatabase1;
    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
    }

    public void setOnGetMonumentListSuccessListener(IOnGetMonumentListSuccessListener onGetMonumentListSuccessListener) {
        mOnGetMonumentListSuccessListener = onGetMonumentListSuccessListener;
    }

    public IOnGetMonumentListSuccessListener getOnGetMonumentSuccessListener() {
        return mOnGetMonumentListSuccessListener;
    }

    public interface IOnGetMonumentListSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public IOnNoteSuccessListener getOnNoteSuccessListener() {
        return mOnNoteSuccessListener;
    }

    public void setOnNoteSuccessListener(IOnNoteSuccessListener onNoteSuccessListener){
        mOnNoteSuccessListener = onNoteSuccessListener;
    }

    public interface IOnNoteSuccessListener {
        void onSuccess(HashMap<String,Note> mMap);
    }

    public IOnSearchSuccessListener getOnSearchSuccessListener() {
        return mOnSearchSuccessListener;
    }

    public void setOnSearchSuccessListener(IOnSearchSuccessListener onSearchSuccessListener){
        mOnSearchSuccessListener = onSearchSuccessListener;
    }

    public interface IOnSearchSuccessListener {
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public IOnFavMonSuccessListener getOnFavMonSuccessListener() {
        return mOnFavMonSuccessListener;
    }

    public void setOnFavMonSuccessListener(IOnFavMonSuccessListener onFavMonSuccessListener) {
        mOnFavMonSuccessListener = onFavMonSuccessListener;
    }

    public interface IOnFavMonSuccessListener{
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public IOnFindUserSuccessListener getOnFindUserSuccessListener() {
        return mOnFindUserSuccessListener;
    }

    public void setOnFindUserSuccessListener(IOnFindUserSuccessListener onFindUserSuccessListener) {
        mOnFindUserSuccessListener = onFindUserSuccessListener;
    }

    public interface IOnFindUserSuccessListener{
        void onSuccess(HashMap<String,User> mMap);
    }

    public IOnFindFavMonSuccessListener getOnFindFavMonSuccessListener() {
        return mOnFindFavMonSuccessListener;
    }

    public void setOnFindFavMonSuccessListener(IOnFindFavMonSuccessListener onFindFavMonSuccessListener) {
        mOnFindFavMonSuccessListener = onFindFavMonSuccessListener;
    }

    public interface IOnFindFavMonSuccessListener{
        void onSuccess(HashMap<String,Monument> mMap);
    }

    public IOnFindUserLikeSuccessListener getOnFindUserLikeSuccessListener() {
        return mOnFindUserLikeSuccessListener;
    }

    public void setOnFindUserLikeSuccessListener(IOnFindUserLikeSuccessListener onFindUserLikeSuccessListener) {
        mOnFindUserLikeSuccessListener = onFindUserLikeSuccessListener;
    }

    public interface IOnFindUserLikeSuccessListener{
        void onSuccess(HashMap<String,String> mMap);
    }

    public IOnGetLikeCountSuccessListener getOnGetLikeCountSuccessListener() {
        return mOnGetLikeCountSuccessListener;
    }

    public void setOnGetLikeCountSuccessListener(IOnGetLikeCountSuccessListener onGetLikeCountSuccessListener) {
        mOnGetLikeCountSuccessListener = onGetLikeCountSuccessListener;
    }

    public interface IOnGetLikeCountSuccessListener{
        void onSuccess(int likeCount);
    }

    public IOnOperationEndListener getOnOperationEndListener() {
        return mOnOperationEndListener;
    }

    public void setOnOperationEndListener(IOnOperationEndListener onOperationEndListener) {
        mOnOperationEndListener = onOperationEndListener;
    }

    public interface IOnOperationEndListener{
        void doingSomething();
    }
}