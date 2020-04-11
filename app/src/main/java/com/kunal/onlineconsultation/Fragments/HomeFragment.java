package com.kunal.onlineconsultation.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sliderviewlibrary.SliderView;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.Adapter.Adapter;
import com.kunal.onlineconsultation.Adapter.SliderAdapter;
import com.kunal.onlineconsultation.DiagnosisActivity;
import com.kunal.onlineconsultation.DoctorDetailsActivity;
import com.kunal.onlineconsultation.DoctorList;
import com.kunal.onlineconsultation.Interface.ItemClickListener;
import com.kunal.onlineconsultation.Location.GetCity;
import com.kunal.onlineconsultation.Location.Location;
import com.kunal.onlineconsultation.MainActivity;
import com.kunal.onlineconsultation.MedicinesActivity;
import com.kunal.onlineconsultation.Model.DoctorModel;
import com.kunal.onlineconsultation.R;
import com.kunal.onlineconsultation.ViewHolder.DoctorViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView logout,location;
    List<String> image;
    SliderAdapter viewPagerAdapter ;
    private SliderAdapter adapter;


    // Recycler View object
    RecyclerView recyclerView;

    // Array list for recycler view data source
    ArrayList<DoctorModel> doctor_list;

    // Layout Manager
    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    // adapter class object
    Adapter doctor_adapter;

    // Linear Layout Manager
    LinearLayoutManager HorizontalLayout;

    View ChildView;
    int RecyclerViewItemPosition;

    int DELAY_MS=20000,PERIOD_MS=15000;
    int currentPage=0;

    View myFragment;



    FirebaseRecyclerAdapter<DoctorModel,DoctorViewHolder> Adapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_home, container, false);

        doctor_list = new ArrayList<>();

        ImageView medicines = myFragment.findViewById(R.id.medicines);
        medicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MedicinesActivity.class);
                startActivity(intent);
            }
        });

        ImageView diagnosis = myFragment.findViewById(R.id.diagnosis);
        diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiagnosisActivity.class);
                startActivity(intent);
            }
        });

        ImageView doctors = myFragment.findViewById(R.id.doctor);
        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DoctorList.class);
                startActivity(intent);
            }
        });
        recyclerView = (RecyclerView)myFragment.findViewById(
                R.id.doctors);

        RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getActivity());

        // Set LayoutManager on Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        //AddItemsToRecyclerViewArrayList();

        //doctor_adapter = new Adapter(getActivity(),doctor_list);

        HorizontalLayout
                = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);

        //recyclerView.setAdapter(doctor_adapter);




        final SliderView sliderView = myFragment.findViewById(R.id.imageSlider);

        FirebaseDatabase Database = FirebaseDatabase.getInstance();
        DatabaseReference mref = Database.getReference("Banners");

        image = new ArrayList<>();
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 1 ;i<=dataSnapshot.getChildrenCount();i++){
                    String images= dataSnapshot.child("image"+i).child("url").getValue(String.class);
                    image.add(images);
                }


                sliderView.setUrls((ArrayList<String>) image);

                TimerTask task = sliderView.getTimerTask();
                Timer timer = new Timer();

                timer.schedule(task,DELAY_MS,PERIOD_MS);

//                viewPagerAdapter = new SliderAdapter(MainActivity.this,image);
//                sliderView.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        location = myFragment.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Location.class);
                intent.putExtra("home","home");
                startActivity(intent);
                getActivity().finish();
            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String city = dataSnapshot.child("location").getValue().toString();
                    location.setText(city);
                    loadDoctors(city);
                }catch (Exception e){
                    FancyToast.makeText(getActivity(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logout = myFragment.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(getActivity(), Location.class);
                startActivity(intent);
                getActivity().finish();
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
            }
        });
        return myFragment;
    }


    private void loadDoctors(String location) {

        final DatabaseReference doctors = FirebaseDatabase.getInstance().getReference("Doctors-Location").child(location);
        doctors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()){
                        Adapter = new FirebaseRecyclerAdapter<DoctorModel, DoctorViewHolder>(
                                DoctorModel.class,
                                R.layout.doctors_home,
                                DoctorViewHolder.class,
                                doctors
                        ) {
                            @Override
                            protected void populateViewHolder(final DoctorViewHolder viewHolder, final DoctorModel model, int position) {

                                viewHolder.doctor_name.setText(model.getName());

                                Glide.with(getActivity()).load(model.getImage()).into(viewHolder.doctor_image);

                                viewHolder.qualification.setText(model.getDegree());

                                viewHolder.rating.setText(model.getRating());

                                viewHolder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
//                        FancyToast.makeText(getActivity(),String.format("%d|%s",adapter.getRef(position).getKey(),model.getName()),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                                        Intent startGame = new Intent(getActivity(), DoctorDetailsActivity.class);
                                        String rating = model.getRating() ;
                                        String qualification = model.getDegree();
                                        String name = model.getName();
                                        String location = model.getCity();
                                        String profile_pic = model.getImage();
                                        String uid = model.getUid();
                                        String email = model.getEmail();
                                        startGame.putExtra("rating",rating);
                                        startGame.putExtra("qualification",qualification);
                                        startGame.putExtra("name",name);
                                        startGame.putExtra("location",location);
                                        startGame.putExtra("image",profile_pic);
                                        startGame.putExtra("dr_uid",uid);
                                        startGame.putExtra("email",email);
                                        startActivity(startGame);
                                        //FancyToast.makeText(getActivity(),model.getName(),FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
                                    }
                                });




                            }
                        };
                        Adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(Adapter);
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    private void AddItemsToRecyclerViewArrayList() {
//        DatabaseReference doctors_ref = FirebaseDatabase.getInstance().getReference("Doctors");
//        doctors_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds :dataSnapshot.getChildren()){
//                    doctor_list.add(new DoctorModel(ds.child("profileimage").getValue(String.class),ds.child("email").getValue(String.class),ds.child("location").getValue(String.class),ds.child("name").getValue(String.class),ds.child("qualification").getValue(String.class),ds.child("rating").getValue(String.class),ds.child("uid").getValue(String.class)));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


}
