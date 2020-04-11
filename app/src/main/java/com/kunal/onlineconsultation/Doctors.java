package com.kunal.onlineconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.Adapter.DoctorListAdapter;
import com.kunal.onlineconsultation.Model.DoctorModel;
import com.kunal.onlineconsultation.ViewHolder.DoctorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class Doctors extends AppCompatActivity implements DoctorListAdapter.ItemClickListener {
    FirebaseRecyclerAdapter<DoctorModel, DoctorViewHolder> Adapter;
    DoctorListAdapter doctorListAdapter ;
    RecyclerView doctors_list;
    TextView category;
    List<String> dr_name,dr_rating,dr_qualification,dr_photo,dr_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        category = findViewById(R.id.category);
        category.setText("Doctors - "+getIntent().getExtras().getString("category"));
        dr_name = new ArrayList<>();
        dr_rating = new ArrayList<>();
        dr_photo = new ArrayList<>();
        dr_qualification = new ArrayList<>();
        dr_uid = new ArrayList<>();
        loadDoctors();
        doctors_list = findViewById(R.id.doctors);
        doctors_list.setLayoutManager(new GridLayoutManager(this, 2));
        doctorListAdapter = new DoctorListAdapter(getApplicationContext(),dr_name,dr_qualification,dr_photo,dr_rating);
        doctorListAdapter.setClickListener(this);
        doctors_list.setAdapter(doctorListAdapter);
    }

    private void loadDoctors() {

        ArrayList<String> uid = getIntent().getStringArrayListExtra("doctor_list");

        for (int i =0;i<uid.size();i++){

            DatabaseReference doctors = FirebaseDatabase.getInstance().getReference("Doctors").child(uid.get(i));
            doctors.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String doctor_name = dataSnapshot.child("name").getValue(String.class);
                    String profile_image = dataSnapshot.child("image").getValue(String.class);
                    String uid = dataSnapshot.child("uid").getValue(String.class);
                    String rating = dataSnapshot.child("rating").getValue(String.class);
                    String qualification = dataSnapshot.child("Degree").getValue(String.class);

                    dr_name.add(doctor_name);
                    dr_photo.add(profile_image);
                    dr_qualification.add(qualification);
                    dr_rating.add(rating);
                    dr_uid.add(uid);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            DatabaseReference doctors = FirebaseDatabase.getInstance().getReference("Doctors").child(uid.get(i));
//            Adapter = new FirebaseRecyclerAdapter<DoctorModel, DoctorViewHolder>(
//                    DoctorModel.class,
//                    R.layout.doctors_home,
//                    DoctorViewHolder.class,
//                    doctors
//            ) {
//                @Override
//                protected void populateViewHolder(final DoctorViewHolder viewHolder, final DoctorModel model, int position) {
//                    viewHolder.doctor_name.setText(model.getName());
//
//                    Glide.with(Doctors.this).load(model.getProfileimage()).into(viewHolder.doctor_image);
//
//                    viewHolder.qualification.setText(model.getQualification());
//
//                    viewHolder.rating.setText(model.getRating());
//
//                }
//
//            };
//            Adapter.notifyDataSetChanged();
//            doctors_list.setAdapter(Adapter);
        }


    }

    @Override
    public void onItemClick(View view, int position) {
        String uid= dr_uid.get(position);
        Intent intent = new Intent(Doctors.this,DoctorDetailsActivity.class);
        intent.putExtra("dr_uid",uid);
        startActivity(intent);
    }
}
