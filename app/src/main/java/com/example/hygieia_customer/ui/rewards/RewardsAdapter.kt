package com.example.hygieia_customer.ui.rewards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Reward
import com.google.android.material.imageview.ShapeableImageView

class RewardsAdapter(private val rewardList: ArrayList<Reward>) :
    RecyclerView.Adapter<RewardsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rewardList[position]

        Glide.with(holder.itemView)
            .load(currentItem.photo)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.prodPhoto)

        holder.product.text = currentItem.product
//        holder.storePrice.text = "Org. Price: ${currentItem.store_price.toString()}"
        holder.discount.text = "Discount: ${currentItem.discount.toString()}%"
        holder.discPrice.text = "Disc. Price: ₱${currentItem.discountedPrice.toString()}"
        holder.pointsReq.text = "Requires: ${currentItem.pointsRequired} pts"
        holder.store.text = currentItem.storeName
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodPhoto: ShapeableImageView = itemView.findViewById(R.id.photo)
        val product: TextView = itemView.findViewById(R.id.product)
        val discount: TextView = itemView.findViewById(R.id.discount)
        val discPrice: TextView = itemView.findViewById(R.id.disc_price)
        val pointsReq: TextView = itemView.findViewById(R.id.points_required)
        val store : TextView = itemView.findViewById(R.id.store)
    }
}