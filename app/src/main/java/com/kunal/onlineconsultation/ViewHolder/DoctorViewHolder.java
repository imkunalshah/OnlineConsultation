package com.kunal.onlineconsultation.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunal.onlineconsultation.Interface.ItemClickListener;
import com.kunal.onlineconsultation.R;

public class DoctorViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView doctor_name,rating,qualification;
    public ImageView doctor_image;

    private ItemClickListener itemClickListener;

    public DoctorViewHolder(@NonNull View itemView) {
        super(itemView);
        doctor_image = (ImageView) itemView.findViewById(R.id.profile_image);
        doctor_name =(TextView) itemView.findViewById(R.id.drname);
        rating = itemView.findViewById(R.id.rating);
        qualification = itemView.findViewById(R.id.drqualification);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
