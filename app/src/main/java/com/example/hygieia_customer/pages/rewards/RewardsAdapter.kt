package com.example.hygieia_customer.pages.rewards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.utils.Commons
import com.google.android.material.imageview.ShapeableImageView

class RewardsAdapter(
    private var rewardList: ArrayList<Reward>,
    private val onItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(item: Reward) {
            // Default implementation
        }
    }
) :
    RecyclerView.Adapter<RewardsAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Reward)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rewardList[position]
        val context = holder.itemView.context

        Glide.with(holder.itemView)
            .load(currentItem.photo)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.prodPhoto)

        holder.product.text = currentItem.name
        holder.discount.text =
            context.getString(
                R.string.discount_template,
                Commons().formatDecimalNumber(currentItem.discount)
            )
        holder.pointsReq.text =
            context.getString(
                R.string.points_req_template,
                Commons().formatDecimalNumber(currentItem.pointsRequired)
            )
        holder.store.text = currentItem.storeName

        if (currentItem.discountedPrice.toInt() == 0) {
            holder.discPrice.text = context.getString(R.string.free)
        } else {
            holder.discPrice.text =
                context.getString(
                    R.string.disc_price_template,
                    Commons().formatDecimalNumber(currentItem.discountedPrice)
                )
        }

        holder.item.setOnClickListener {
            onItemClickListener.onItemClick(currentItem)
        }
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
        val store: TextView = itemView.findViewById(R.id.store)
        val item: CardView = itemView.findViewById(R.id.item)
    }

    fun setData(newRewardsList: List<Reward>) {
        val oldRewardList = ArrayList(rewardList) // Create a copy of the old list
        rewardList.clear() // Clear the old list
        rewardList.addAll(newRewardsList) // Update the list with the new data

        val diffUtil =
            RewardsDiffUtil(oldRewardList, rewardList) // Pass the old and new lists to the DiffUtil
        val diffResults =
            DiffUtil.calculateDiff(diffUtil) // Calculate the diff between the old and new lists
        diffResults.dispatchUpdatesTo(this) // Dispatch the updates to the adapter
    }
}