package com.kunal.onlineconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorDetailsActivity extends AppCompatActivity {
TextView rating,doc_name;
CircleImageView profile_image;
LinearLayout consult_online;
String uid ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);
        rating = findViewById(R.id.rating);
        doc_name = findViewById(R.id.drname);
        profile_image = findViewById(R.id.profile_image);
        uid = "";

        uid = getIntent().getExtras().getString("dr_uid");
        try {
            DatabaseReference doctor_ref = FirebaseDatabase.getInstance().getReference("Doctors").child(uid);
            doctor_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String doc_rating = dataSnapshot.child("rating").getValue(String.class);
                    String Doc_name = dataSnapshot.child("name").getValue(String.class);
                    String doq_qual = dataSnapshot.child("Degree").getValue(String.class);
                    String doc_image = dataSnapshot.child("image").getValue(String.class);
                    rating.setText(doc_rating);
                    doc_name.setText(Doc_name);
                    Glide.with(getApplicationContext()).load(doc_image).into(profile_image);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }

        consult_online = findViewById(R.id.consultonline);



        consult_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> users = new ArrayList<>();
                users.add(getIntent().getExtras().getString("dr_uid"));
                users.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                List<String> operators = new ArrayList<>();
                operators.add(getIntent().getExtras().getString("dr_uid"));

                GroupChannelParams params = new GroupChannelParams()
                        .setPublic(false)
                        .setEphemeral(false)
                        .setDistinct(true)
                        .addUserIds(users)
                        .setOperatorUserIds(operators)  // Or .setOperators(List<User> operators)
                        .setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
                    @Override
                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Consultations").child(getIntent().getExtras().getString("dr_uid"));
                        DatabaseReference doctorRFef =FirebaseDatabase.getInstance().getReference("Doctors").child(getIntent().getExtras().getString("dr_uid")).child("Consultations").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String,Object> Users = new HashMap<>();
                        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName()==null){
                            Users.put("patient",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            Users.put("doctor",doc_name.getText().toString());
                        }
                        else {
                            Users.put("patient",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            Users.put("doctor",doc_name.getText().toString());
                        }
                        patientRef.updateChildren(Users);
                        doctorRFef.updateChildren(Users);
                        Intent intent = new Intent(DoctorDetailsActivity.this,ChatActivity.class);
                        intent.putExtra("url",groupChannel.getUrl());
                        startActivity(intent);
                    }
                });




            }
        });

    }
}
