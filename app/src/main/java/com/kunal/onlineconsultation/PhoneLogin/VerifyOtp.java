package com.kunal.onlineconsultation.PhoneLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.LoginActivity;
import com.kunal.onlineconsultation.MainActivity;
import com.kunal.onlineconsultation.R;
import com.kunal.onlineconsultation.Sendbird.ConnectionManager;
import com.kunal.onlineconsultation.Sendbird.PreferenceUtils;
import com.kunal.onlineconsultation.Sendbird.SyncManagerUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyOtp extends AppCompatActivity {
    TextView resend;
    private String VerificationId;
    private FirebaseAuth mAuth;
    private EditText otp;
    LinearLayout submit;
    PhoneAuthProvider.ForceResendingToken mResendToken ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);


        SendBird.init("64509A71-5BCF-4393-BEE3-29BC9487AD1C",this);

        PreferenceUtils.init(getApplicationContext());

        resend = findViewById(R.id.resend);
        mAuth = FirebaseAuth.getInstance();
        otp = findViewById(R.id.code);
        submit = findViewById(R.id.submit);

        final String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resendVerificationCode(phonenumber,mResendToken);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Code = otp.getText().toString().trim();
                if (Code.isEmpty() || Code.length()<6){
                    otp.setError("Enter Valid Code ...");
                    otp.requestFocus();
                    return;
                }
                verifyCode(Code);

            }
        });
    }



    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId,code);
        SignInWithCredential(credential);
    }

    private void SignInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Intent intent = new Intent(VerifyOtp.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    connect(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    updateUI(FirebaseAuth.getInstance().getCurrentUser());
                }
                else {
                    updateUI(null);
                    FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number){

        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(number,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);
        }
        catch (Exception e){
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    VerificationId = s;
                     mResendToken = forceResendingToken;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code!=null){
                        otp.setText(code);
                        verifyCode(code);
                    }
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    Intent intent =new Intent(VerifyOtp.this,MobileNo.class);
                    startActivity(intent);
                    finish();
                }

            };

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallBack,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        FancyToast.makeText(getApplicationContext(),"Code Resent",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
    }

    private void connect(String userId, String userNickname) {
        ConnectionManager.connect(VerifyOtp.this, userId, userNickname, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e == null) {
                    SyncManagerUtils.setup(VerifyOtp.this, PreferenceUtils.getUserId(), new CompletionHandler() {
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

        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                        FancyToast.makeText(getApplicationContext(),"User already registered as a Doctor",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        FirebaseAuth.getInstance().signOut();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        startActivity(new Intent(VerifyOtp.this,LoginActivity.class));
                        finish();
                    }
                    else if (dataSnapshot.child("Admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                        FancyToast.makeText(getApplicationContext(),"User already registered as an Admin",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        FirebaseAuth.getInstance().signOut();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        startActivity(new Intent(VerifyOtp.this,LoginActivity.class));
                        finish();
                    }
                    else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("phone",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
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
                        Intent intent = new Intent(VerifyOtp.this,MainActivity.class);
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

}
