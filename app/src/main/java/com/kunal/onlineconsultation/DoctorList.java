package com.kunal.onlineconsultation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.Interface.ItemClickListener;
import com.kunal.onlineconsultation.Model.DoctorCategoryModel;
import com.kunal.onlineconsultation.Model.DoctorModel;
import com.kunal.onlineconsultation.ViewHolder.DoctorCategoryViewHolder;
import com.kunal.onlineconsultation.ViewHolder.DoctorViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorList extends AppCompatActivity {

    FirebaseRecyclerAdapter<DoctorCategoryModel, DoctorCategoryViewHolder> Adapter;
    RecyclerView specialities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        specialities = findViewById(R.id.doctor_category_list);
        specialities.setLayoutManager(new GridLayoutManager(this, 2));
        loadDoctors();
    }

    private void loadDoctors() {

        DatabaseReference doctors = FirebaseDatabase.getInstance().getReference("DoctorCategory");
        Adapter = new FirebaseRecyclerAdapter<DoctorCategoryModel, DoctorCategoryViewHolder>(
                DoctorCategoryModel.class,
                R.layout.doctor_category_list,
                DoctorCategoryViewHolder.class,
                doctors
        ) {
            @Override
            protected void populateViewHolder(final DoctorCategoryViewHolder viewHolder, final DoctorCategoryModel model, int position) {
                viewHolder.category_name.setText(model.getCategory());

                Glide.with(getApplicationContext()).load(model.getImage()).into(viewHolder.category_image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        FancyToast.makeText(getActivity(),String.format("%d|%s",adapter.getRef(position).getKey(),model.getName()),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                        final Intent startGame = new Intent(DoctorList.this, Doctors.class);
                        final String doctorCategory = model.getCategory();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference doctorsRef = rootRef.child("Doctors");
                        Query categoryQuery = doctorsRef.orderByChild("category").equalTo(doctorCategory);
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> uidList = new ArrayList<>();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String uid = ds.getKey();
                                    uidList.add(uid);
                                }
                                startGame.putExtra("category",doctorCategory);
                                startGame.putStringArrayListExtra("doctor_list", uidList);
                                startActivity(startGame);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("TAG", databaseError.getMessage()); //Don't ignore errors!
                            }
                        };
                        categoryQuery.addListenerForSingleValueEvent(valueEventListener);

                        //FancyToast.makeText(getActivity(),model.getName(),FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
                    }
                });
            }
        };
        Adapter.notifyDataSetChanged();
        specialities.setAdapter(Adapter);
    }
}
