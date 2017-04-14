package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class RemoveFavoriteMon extends AsyncTask<Void, Void, Void>
{
    private String mUserID;
    private Monument mMonument;
    private FireHelper mFireHelper;

    public RemoveFavoriteMon(String userID, Monument monument, FireHelper fh) {
        this.mUserID = userID;
        this.mMonument = monument;
        this.mFireHelper = fh;
    }
    @Override
    protected Void doInBackground(Void... params) {
        mFireHelper.getmDatabase().child("models").child("users").child(mUserID).child("favoriteMon").child(mMonument.getId()).removeValue();
        return null;
    }
}
