package com.kunal.onlineconsultation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kunal.onlineconsultation.Model.DoctorModel;
import com.kunal.onlineconsultation.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    // List with String type
    private ArrayList<DoctorModel> doctorlist;

    // Constructor for adapter class
    // which takes a list of String type
    public Adapter(Context context ,ArrayList<DoctorModel> doctorlist)
    {
        this.context = context;
        this.doctorlist = doctorlist;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_home, parent, false);

        // return itemView
        return new ViewHolder(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 final int position)
    {

        DoctorModel doctorModel = doctorlist.get(position);
        // Set the text of each item of
        // Recycler view with the list items
        holder.qualification.setText(doctorModel.getDegree());
        holder.rating.setText(doctorModel.getRating());
        holder.drname.setText(doctorModel.getName());
        Glide.with(context).load(doctorModel.getImage()).into(holder.profile_image);
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return doctorlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        // Text View
        TextView drname,rating,qualification;
        CircleImageView profile_image;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public ViewHolder(View view)
        {
            super(view);

            // initialise TextView with id
            profile_image = view.findViewById(R.id.profile_image);
            drname = view.findViewById(R.id.drname);
            rating = view.findViewById(R.id.rating);
            qualification = view.findViewById(R.id.drqualification);
        }

    }
}
