package com.kunal.onlineconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.PhoneLogin.MobileNo;
import com.kunal.onlineconsultation.Sendbird.ConnectionManager;
import com.kunal.onlineconsultation.Sendbird.PreferenceUtils;
import com.kunal.onlineconsultation.Sendbird.SyncManagerUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
LinearLayout phone;
LinearLayout google;
LinearLayout facebook;
    private static final int RC_SIGN_IN =101;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    ConstraintLayout login_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       //FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        login_layout = findViewById(R.id.layout);

        SendBird.init("64509A71-5BCF-4393-BEE3-29BC9487AD1C",this);

        PreferenceUtils.init(getApplicationContext());

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        FancyToast.makeText(getApplicationContext(),"Canceled",FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        FancyToast.makeText(getApplicationContext(),exception.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });

        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.phone_no);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MobileNo.class);
                intent.putExtra("location",getIntent().getExtras().get("location").toString());
                startActivity(intent);
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);


        mAuth = FirebaseAuth.getInstance();

        google = findViewById(R.id.google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        facebook = findViewById(R.id.facebook);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            connect(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            FancyToast.makeText(getApplicationContext(),"User Registration Failed",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();

                        }

                        // ...
                    }
                });

    }

    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            PreferenceUtils.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            PreferenceUtils.setNickname(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                            connect(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            FancyToast.makeText(getApplicationContext(), "Authentication failed.",
                                    FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    // Displays a Snackbar from the bottom of the screen
    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(login_layout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void connect(String userId, String userNickname) {
        ConnectionManager.connect(LoginActivity.this, userId, userNickname, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e == null) {
                    SyncManagerUtils.setup(LoginActivity.this, PreferenceUtils.getUserId(), new CompletionHandler() {
                        @Override
                        public void onCompleted(SendBirdException e) {
                            if (e != null) {
                                FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                return;
                            }
                            PreferenceUtils.setConnected(true);
                        }
                    });
                } else {
                    FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    PreferenceUtils.setConnected(false);
                }
            }
        });
    }


    private void updateUI(FirebaseUser user) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                        FancyToast.makeText(getApplicationContext(),"User already registered as a Doctor",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        FirebaseAuth.getInstance().signOut();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                        finish();
                    }
                    else if (dataSnapshot.child("Admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                        FancyToast.makeText(getApplicationContext(),"User already registered as an Admin",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        FirebaseAuth.getInstance().signOut();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                        finish();
                    }

                    else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        map.put("name",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        map.put("location",getIntent().getExtras().get("location").toString());
                        userRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FancyToast.makeText(getApplicationContext(),"User details updated successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                                }
                                else {
                                    FancyToast.makeText(getApplicationContext(),"User details update unsuccessful",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                                }
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.buttonFacebookLogin).setVisibility(View.GONE);
//            findViewById(R.id.buttonFacebookSignout).setVisibility(View.VISIBLE);
        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
//            findViewById(R.id.buttonFacebookSignout).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//        if (isLoggedIn){
//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
         if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
