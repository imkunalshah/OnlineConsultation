package com.kunal.onlineconsultation.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunal.onlineconsultation.Interface.ItemClickListener;
import com.kunal.onlineconsultation.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CircleImageView category_image;
    public TextView category_name;
    private ItemClickListener itemClickListener;

    public DoctorCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        category_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
        category_name = itemView.findViewById(R.id.categoryname);
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
