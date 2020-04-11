package com.kunal.onlineconsultation.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.kunal.onlineconsultation.R;
import com.kunal.onlineconsultation.SettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
TextView person_name,person_contact;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        person_contact = view.findViewById(R.id.person_contact);
        person_name = view.findViewById(R.id.person_name);
        ImageView settings = view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        CircleImageView profile_image = view.findViewById(R.id.profile_image);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Glide.with(getActivity()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile_image);
        }
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()){
            person_name.setText("");
            person_contact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        else {
            person_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            person_contact.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }
}
