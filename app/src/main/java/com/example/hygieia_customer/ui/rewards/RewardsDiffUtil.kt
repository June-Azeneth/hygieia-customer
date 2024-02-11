package com.example.hygieia_customer.ui.rewards

import androidx.recyclerview.widget.DiffUtil
import com.example.hygieia_customer.model.Reward

class RewardsDiffUtil(
    private val oldList: ArrayList<Reward>,
    private val newList: ArrayList<Reward>
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