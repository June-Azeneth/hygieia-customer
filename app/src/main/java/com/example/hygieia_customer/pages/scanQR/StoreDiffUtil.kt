package com.example.hygieia_customer.pages.scanQR

import androidx.recyclerview.widget.DiffUtil
import com.example.hygieia_customer.model.Store

class StoreDiffUtil(
    private val oldList: ArrayList<Store>,
    private val newList: ArrayList<Store>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].storeId == newList[newItemPosition].storeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}