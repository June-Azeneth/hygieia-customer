package com.example.hygieia_customer.ui.profile.profilePhotos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.InAppProfilePhotos
import com.example.hygieia_customer.model.ProfileOptions
import com.google.android.material.imageview.ShapeableImageView

class ProfilePhotosAdapter(
    private val picture: ArrayList<InAppProfilePhotos>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ProfilePhotosAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(picture: InAppProfilePhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_pics_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = picture[position]

        Glide.with(holder.itemView)
            .load(currentItem.img_url)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.pictureOption)

        holder.pictureOption.setOnClickListener {
            onItemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return picture.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pictureOption: ShapeableImageView = itemView.findViewById(R.id.pictureOption)
    }
}