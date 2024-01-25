package com.example.hygieia_customer.ui.promos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Promo
import com.google.android.material.imageview.ShapeableImageView

class PromoAdapter(private val promosList: ArrayList<Promo>) :
    RecyclerView.Adapter<PromoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.promo_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = promosList[position]

        Glide.with(holder.itemView)
            .load(currentItem.img_url)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.prodPhoto)

        holder.promoName.text = currentItem.name
        holder.product.text = "Product: ${currentItem.product}"
        holder.discount.text = "Discount: ${currentItem.discount.toString()}%"
        holder.discPrice.text = "Discounted Price: ₱${currentItem.disc_price.toString()}"
        holder.pointsReq.text = "Points Required: ${currentItem.pts_req}"
        holder.dateEnd.text = currentItem.promo_end
        holder.store.text = currentItem.storeName
    }

    override fun getItemCount(): Int {
        return promosList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodPhoto: ShapeableImageView = itemView.findViewById(R.id.photo)
        val promoName: TextView = itemView.findViewById(R.id.promo_name)
        val product: TextView = itemView.findViewById(R.id.product)
        val discount: TextView = itemView.findViewById(R.id.discount)
        val discPrice: TextView = itemView.findViewById(R.id.disc_price)
        val pointsReq: TextView = itemView.findViewById(R.id.points_required)
        val dateEnd: TextView = itemView.findViewById(R.id.promoEnd)
        val store : TextView = itemView.findViewById(R.id.store)
    }
}