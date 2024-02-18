package com.example.hygieia_customer.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hygieia_customer.R
import com.google.android.material.imageview.ShapeableImageView

class Commons {

    fun setPageTitle(title: String, root: View) {
        val customToolbar = root.findViewById<View>(R.id.header)
        val ref: TextView = customToolbar.findViewById(R.id.pageTitle)
        ref.text = title
    }

    fun setToolbarIcon(icon: Int, root: View) {
        val customToolbar = root.findViewById<View>(R.id.header)
        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
        iconRef.setImageResource(icon)
    }

    fun setBackAction(root: View, action: () -> Unit) {
        val customToolbar = root.findViewById<View>(R.id.header)
        val icon : ShapeableImageView = customToolbar.findViewById(R.id.back)
        icon.visibility = View.VISIBLE
        icon.setOnClickListener {
            action.invoke() // Invoke the lambda function when the icon is clicked
        }
    }

    fun setToolBarIconAction(root: View, action: () -> Unit) {
        val customToolbar = root.findViewById<View>(R.id.header)
        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
        iconRef.setOnClickListener {
            action.invoke() // Invoke the lambda function when the icon is clicked
        }
    }

    fun setOnRefreshListener(refreshLayout: SwipeRefreshLayout, refreshAction: () -> Unit) {
        refreshLayout.setOnRefreshListener {
            refreshAction.invoke()
            refreshLayout.isRefreshing = false
        }
    }

    fun setNavigationOnClickListener(view: View, actionId: Int) {
        view.setOnClickListener {
            val navController = Navigation.findNavController(view)
            navController.navigate(actionId)
        }
    }

    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun log(tag: String, message: String) {
        Log.e(tag, message)
    }
}