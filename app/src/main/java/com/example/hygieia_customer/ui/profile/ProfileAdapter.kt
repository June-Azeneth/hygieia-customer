package com.example.hygieia_customer.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.ProfileOptions
import com.google.android.material.imageview.ShapeableImageView

class ProfileAdapter(
    private val options: ArrayList<ProfileOptions>,
    private val onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<ProfileAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(request: ProfileOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_options_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = options[position]

        holder.imageView.setImageResource(currentItem.optionIcon)
        holder.textView.text = currentItem.optionTitle

        holder.container.setOnClickListener {
            onItemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ShapeableImageView = itemView.findViewById(R.id.optionIcon)
        val textView: TextView = itemView.findViewById(R.id.optionTitle)
        val container : ConstraintLayout = itemView.findViewById(R.id.container)
    }
}