package blue_team.com.monuguide.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.User;

public class FacebookLoginActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "FacebookLogin";
    private LoginButton mLoginButton;
    private Button mSignOutButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireHelper.IOnFindUserSuccessListener mFindUserSuccessListener;
    private ProgressDialog mProgress;
    private FireHelper mFireHelper = new FireHelper();
    private User mMyUser;
    ImageView mUserImg;
    TextView mFaceTittle,mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login);
        setupActionBar();
        mMyUser = new User();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        findViewById(R.id.button_facebook_signout).setOnClickListener(this);
        mLoginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        mSignOutButton = (Button) findViewById(R.id.button_facebook_signout);
        mUserImg = (ImageView) findViewById(R.id.user_img);
        mFaceTittle = (TextView) findViewById(R.id.face_tittle);
        mUserName = (TextView) findViewById(R.id.user_name);

        mLoginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                updateUI(null);
                //FacebookLoginActivity.this.finish();
            }

            @Override
            public void onError(FacebookException error) {
                updateUI(null);
                error.getMessage();
               // FacebookLoginActivity.this.finish();
            }
        });

        mFindUserSuccessListener = new FireHelper.IOnFindUserSuccessListener() {
            @Override
            public void onSuccess(HashMap<String, User> mMap) {
                if(mMap.isEmpty())
                {
                    mFireHelper.addUser(mMyUser);
                }
                else
                {

                }
                mProgress.dismiss();
            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else
                {
                    // User is signed out

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "blue_team.com.monuguide",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("error", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("error1", e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mProgress.setMessage("Signing Up...");
        mProgress.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                mMyUser.setuID(user.getUid());
                                mMyUser.setName(user.getDisplayName());
                                mMyUser.setEmail(user.getEmail());
                                mMyUser.setPhotoUrl(user.getPhotoUrl().toString());
                                mFireHelper.setOnFindUserSuccessListener(mFindUserSuccessListener);
                                mFireHelper.findUser(mMyUser.getuID());
                                //FacebookLoginActivity.this.finish();
                            }
                    }
                });

    }
    public void signOut() {
        mAuth.signOut();
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
                updateUI(null);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void updateUI(FirebaseUser user) {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
        if (user != null) {
            mUserImg.setVisibility(View.VISIBLE);
            mFaceTittle.setVisibility(View.GONE);
            mUserName.setVisibility(View.VISIBLE);
            mUserName.setText(user.getDisplayName());
            if(user.getPhotoUrl()!=null) {
                Picasso.with(this).load(user.getPhotoUrl()).placeholder(R.mipmap.no_user).resize(500, 500).into(mUserImg);
            }
            mLoginButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
        } else {
            mUserImg.setVisibility(View.GONE);
            mFaceTittle.setVisibility(View.VISIBLE);
            mUserName.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_facebook_signout) {
            signOut();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.overridePendingTransition(R.anim.alpha_up,R.anim.alpha_down);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            this.overridePendingTransition(R.anim.alpha_up,R.anim.alpha_down);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
