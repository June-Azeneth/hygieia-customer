package com.example.hygieia_customer.pages.promos

import androidx.recyclerview.widget.DiffUtil
import com.example.hygieia_customer.model.Promo

class PromosDiffUtil(
    private val oldList: ArrayList<Promo>,
    private val newList: ArrayList<Promo>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return  oldList[oldItemPosition] == newList[newItemPosition]
    }
}