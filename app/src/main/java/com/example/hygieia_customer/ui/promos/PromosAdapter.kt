package com.example.hygieia_customer.ui.promos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.ui.rewards.RewardsDiffUtil
import com.google.android.material.imageview.ShapeableImageView
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class PromosAdapter(private val promosList: ArrayList<Promo>) :
    RecyclerView.Adapter<PromosAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.promo_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = promosList[position]
        val context = holder.itemView.context

        val start = currentItem.dateStart?.let { formatDateOnly(it) }
        val end = currentItem.dateEnd?.let { formatDateOnly(it) }
        val formattedString = context.getString(R.string.date_range, start, end)

        Glide.with(holder.itemView)
            .load(currentItem.photo)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.prodPhoto)

        holder.promoName.text = currentItem.promoName
        holder.product.text = context.getString(R.string.product_template, currentItem.product)
        holder.discount.text = context.getString(R.string.discount_template, formatDouble(currentItem.discountRate))
        holder.discPrice.text = context.getString(R.string.disc_price_template, formatDouble(currentItem.discountedPrice))
        holder.pointsReq.text = context.getString(R.string.points_req_template, formatDouble(currentItem.pointsRequired))
        holder.duration.text = formattedString
        holder.store.text = currentItem.storeName
    }

    override fun getItemCount(): Int {
        return promosList.size
    }

    private fun formatDateOnly(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun formatDouble(value: Double): String {
        return if (value % 1 == 0.0) {
            String.format("%.0f", value)
        } else {
            String.format("%.2f", value)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodPhoto: ShapeableImageView = itemView.findViewById(R.id.photo)
        val promoName: TextView = itemView.findViewById(R.id.promo_name)
        val product: TextView = itemView.findViewById(R.id.product)
        val discount: TextView = itemView.findViewById(R.id.discount)
        val discPrice: TextView = itemView.findViewById(R.id.disc_price)
        val pointsReq: TextView = itemView.findViewById(R.id.points_required)
        val duration: TextView = itemView.findViewById(R.id.promoDuration)
        val store : TextView = itemView.findViewById(R.id.store)
    }

    fun setData(newPromoList: ArrayList<Promo>) {
        val oldPromoList = ArrayList(promosList) // Create a copy of the old list
        promosList.clear() // Clear the old list
        promosList.addAll(newPromoList) // Update the list with the new data

        val diffUtil = PromosDiffUtil(oldPromoList, promosList) // Pass the old and new lists to the DiffUtil
        val diffResults = DiffUtil.calculateDiff(diffUtil) // Calculate the diff between the old and new lists
        diffResults.dispatchUpdatesTo(this) // Dispatch the updates to the adapter
    }
}