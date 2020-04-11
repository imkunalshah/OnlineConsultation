package com.kunal.onlineconsultation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kunal.onlineconsultation.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private List<String> mName;
    private  List<String> mQualification;
    private  List<String> mImage;
    private  List<String> mRating;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public DoctorListAdapter(Context context, List<String> mName, List<String> mQualification, List<String> mImage, List<String> mRating) {
        this.mInflater = LayoutInflater.from(context);
        this.mName = mName;
        this.mQualification = mQualification;
        this.mImage = mImage;
        this.mRating = mRating;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.doctors_home, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String drname = mName.get(position);
        String drqualification = mQualification.get(position);
        String drImage = mImage.get(position);
        String rating = mRating.get(position);
        holder.drname.setText(drname);
        holder.drqualification.setText(drqualification);
        Glide.with(mInflater.getContext()).load(drImage).into(holder.profile_image);
        holder.rating.setText(rating);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mName.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView drname , drqualification , rating;
        CircleImageView profile_image;

        ViewHolder(View itemView) {
            super(itemView);
            drname = itemView.findViewById(R.id.drname);
            drqualification = itemView.findViewById(R.id.drqualification);
            profile_image = itemView.findViewById(R.id.profile_image);
            rating = itemView.findViewById(R.id.rating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mName.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
