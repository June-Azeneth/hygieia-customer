package com.example.hygieia_customer.pages.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.utils.Commons
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionsAdapter(
    private val transactionList: ArrayList<Transaction>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<TransactionsAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Transaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transactionList[position]
        val context = holder.itemView.context

        holder.store.text = currentItem.storeName
        holder.type.text = currentItem.type

        val dateOfTransaction = currentItem.addedOn?.let { Commons().dateFormatMMMDDYYYY(it) }
        holder.date.text = dateOfTransaction

        when (currentItem.type) {
            "grant" -> {
                holder.type.text = context.getString(R.string.received_points)
                holder.points.text = context.getString(R.string.add_points, currentItem.pointsEarned.toString())
                holder.points.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.green
                    )
                )
            }
            "redeem" -> {
                holder.type.text = context.getString(R.string.redeemed, currentItem.product)
                holder.points.text = context.getString(R.string.subtract_points , currentItem.pointsSpent.toString())
                holder.points.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.red
                    )
                )
            }
        }

        holder.view.setOnClickListener {
            onItemClickListener.onItemClick(currentItem)
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
        val view : CardView = itemView.findViewById(R.id.view)
    }

    fun setData(newTransactionList: List<Transaction>) {
        val oldTransactionList = ArrayList(transactionList) // Create a copy of the old list
        transactionList.clear() // Clear the old list
        transactionList.addAll(newTransactionList) // Update the list with the new data

        val diffUtil = TransactionDiffUtil(oldTransactionList, transactionList) // Pass the old and new lists to the DiffUtil
        val diffResults = DiffUtil.calculateDiff(diffUtil) // Calculate the diff between the old and new lists
        diffResults.dispatchUpdatesTo(this) // Dispatch the updates to the adapter
    }
}