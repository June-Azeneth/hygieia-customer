package com.example.hygieia_customer.pages.transactions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.RedeemedRewards
import com.example.hygieia_customer.utils.Commons


class RedeemedProductsAdapter(
    private val products: ArrayList<RedeemedRewards>,
) : RecyclerView.Adapter<RedeemedProductsAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: RedeemedRewards)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_product_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = products[position]
        val context = holder.itemView.context
        holder.name.text = currentItem.name
        holder.pointsRequired.text = "${currentItem.pointsRequired} pts"

        holder.discount.text = "${Commons().formatDecimalNumber(currentItem.discount)}%"

        holder.discountedPrice.text = context.getString(
            R.string.peso_sign,
            Commons().formatDecimalNumber(currentItem.discountedPrice)
        )
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val discount: TextView = itemView.findViewById(R.id.discount)
        val pointsRequired: TextView = itemView.findViewById(R.id.points)
        val discountedPrice: TextView = itemView.findViewById(R.id.price)
    }
}