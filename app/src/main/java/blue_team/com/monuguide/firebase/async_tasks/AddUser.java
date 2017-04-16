package blue_team.com.monuguide.firebase.async_tasks;

import android.os.AsyncTask;

import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.User;

public class AddUser extends AsyncTask<Void,Void,Void>
{
    private User mUser;
    private FireHelper mFireHelper;

    public AddUser(User user, FireHelper fh) {
        this.mUser = user;
        this.mFireHelper = fh;
    }

    @Override
    protected Void doInBackground(Void... params) {
         addUser();
         return null;
    }

    private void addUser()
    {
        mFireHelper.getmDatabase().child("models").child("users").child(mUser.getuID()).setValue(mUser);
    }
}
