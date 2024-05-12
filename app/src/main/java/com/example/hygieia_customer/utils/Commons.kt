package com.example.hygieia_customer.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hygieia_customer.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import java.util.Date
import java.util.Locale

class Commons {

    fun formatDecimalNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            String.format("%.0f", number)
        } else {
            String.format("%.1f", number)
        }
    }

//    private fun getCurrentDate(): Timestamp {
//        val currentDate = Calendar.getInstance().time
//        val calendar = Calendar.getInstance()
//        calendar.time = currentDate
//        calendar.set(Calendar.HOUR_OF_DAY, 0)
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 0)
//        calendar.set(Calendar.MILLISECOND, 0)
//        return Timestamp(Date(calendar.timeInMillis))
//    }

    fun getDateAndTime(): Timestamp {
        val currentDateTime = Calendar.getInstance().time
        return Timestamp(Date(currentDateTime.time))
    }

//    fun dateFormatMMMDDYYYY(): String? {
//        val currentDate = getCurrentDate().toDate() // Convert Timestamp to Date
//        val dateFormat = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
//        return dateFormat.format(currentDate)
//    }

    fun dateFormatMMMDDYYYY(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun dateFormatMMMDDYYYYDate(date: Date?): String {
        val dateFormat = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun dateFormatMMMDD(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

//    fun dateFormatMMMDD(date: Long): String {
//        val dateFormat = SimpleDateFormat("MMM-dd", Locale.getDefault())
//        return dateFormat.format(date)
//    }

//    fun dateFormatMMMDDYYYY(calendar: Calendar): String {
//        val dateFormat = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
//        return dateFormat.format(calendar)
//    }

    fun dateFormatMMMDDYYYY(date: Long): String {
        val dateFormat = SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

//    fun setPageTitle(title: String, root: View) {
//        val customToolbar = root.findViewById<View>(R.id.header)
//        val ref: TextView = customToolbar.findViewById(R.id.pageTitle)
//        ref.text = title
//    }

//    fun setToolbarIcon(icon: Int, root: View) {
//        val customToolbar = root.findViewById<View>(R.id.header)
//        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
//        iconRef.setImageResource(icon)
//    }

//    fun setBackAction(root: View, action: () -> Unit) {
//        val customToolbar = root.findViewById<View>(R.id.header)
//        val icon : ShapeableImageView = customToolbar.findViewById(R.id.back)
//        icon.visibility = View.VISIBLE
//        icon.setOnClickListener {
//            action.invoke() // Invoke the lambda function when the icon is clicked
//        }
//    }

//    fun setToolBarIconAction(root: View, action: () -> Unit) {
//        val customToolbar = root.findViewById<View>(R.id.header)
//        val iconRef: ShapeableImageView = customToolbar.findViewById(R.id.icon)
//        iconRef.setOnClickListener {
//            action.invoke() // Invoke the lambda function when the icon is clicked
//        }
//    }

    fun setOnRefreshListener(refreshLayout: SwipeRefreshLayout, refreshAction: () -> Unit) {
        refreshLayout.setOnRefreshListener {
            refreshAction.invoke()
            refreshLayout.isRefreshing = false
        }
    }

//    fun showAlertDialogWithCallback(
//        fragment: Fragment,
//        title: String,
//        message: String,
//        positiveButton: String,
//        positiveButtonCallback: (() -> Unit)? = null,
//    ) {
//        val builder = AlertDialog.Builder(fragment.requireContext())
//        builder.setTitle(title)
//            .setMessage(message)
//            .setPositiveButton(positiveButton) { dialog, _ ->
//                dialog.dismiss()
//                positiveButtonCallback?.invoke()
//            }
//        val dialog = builder.create()
//        dialog.show()
//    }

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

    fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+$")
        return emailRegex.matches(email)
    }
}