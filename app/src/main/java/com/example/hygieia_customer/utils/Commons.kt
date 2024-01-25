package com.example.hygieia_customer.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hygieia_customer.R
import com.google.android.material.imageview.ShapeableImageView

class Commons {
    fun setPageTitle(title: String, root: View) {
//        val customToolbar = root.findViewById<View>(R.id.header)
//        val titleTextView: TextView = customToolbar.findViewById(R.id.titleTextView)
//        titleTextView.text = title
    }

    fun setToolbarIcon(icon: Int, root: View) {
        val customToolbar = root.findViewById<View>(R.id.header)
        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
        iconRef.setImageResource(icon)
    }

    fun setToolBarIconAction(root : View){
        val customToolbar = root.findViewById<View>(R.id.header)
        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
    }

    fun setOnRefreshListener(refreshLayout: SwipeRefreshLayout, refreshAction: () -> Unit) {
        refreshLayout.setOnRefreshListener {
            refreshAction.invoke()
            refreshLayout.isRefreshing = false
        }
    }
    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }
}