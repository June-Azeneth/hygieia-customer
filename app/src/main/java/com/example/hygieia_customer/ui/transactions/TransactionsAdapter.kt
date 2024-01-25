package com.example.hygieia_customer.ui.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Transaction

class TransactionsAdapter(
    private val transactionList: ArrayList<Transaction>
) :
    RecyclerView.Adapter<TransactionsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transactionList[position]

        holder.store.text = currentItem.storeName
        holder.type.text = currentItem.type
        holder.date.text = currentItem.date

        when (currentItem.type) {
            "grant" -> {
                holder.type.text = "Received Points"
                holder.points.text = "+${currentItem.points_earned.toString()} pts"
                holder.points.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.green
                    )
                )
            }
            "redeem" -> {
                holder.type.text = "Redeemed ${currentItem.product}"
                holder.points.text = "-${currentItem.points_spent.toString()} pts"
                holder.points.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.red
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val store: TextView = itemView.findViewById(R.id.storeName)
        val type: TextView = itemView.findViewById(R.id.transactionType)
        val date: TextView = itemView.findViewById(R.id.date)
        val points: TextView = itemView.findViewById(R.id.pointsSubtractedOrAdded)
    }
}