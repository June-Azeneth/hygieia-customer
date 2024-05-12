package com.example.hygieia_customer.pages.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.TransactionsFilterConfig
import com.example.hygieia_customer.utils.Commons
import java.util.Date
import java.util.Locale

class FilterModal(
    context: Context,
) : Dialog(context) {
    private lateinit var startDate: AppCompatButton
    private lateinit var endDate: AppCompatButton
    private lateinit var filter: AppCompatButton
    private lateinit var startDateConfig: Date
    private lateinit var endDateConfig: Date
    private lateinit var store: String
    private val transactionViewModel: TransactionViewModel = TransactionViewModel()

    // Track whether start date or end date button was clicked
    private var isStartDate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_filter_modal)

        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        filter = findViewById(R.id.filter)

        setUpListeners()
    }

    private fun setUpListeners() {
        startDate.setOnClickListener {
            isStartDate = true
            showDatePickerDialog()
        }
        endDate.setOnClickListener {
            isStartDate = false
            showDatePickerDialog()
        }
        filter.setOnClickListener {
            val currentFilterConfig =
                transactionViewModel.filterConfig.value ?: TransactionsFilterConfig(
//                    null,
//                    null,
                    ""
                )
            val updatedFilterConfig = currentFilterConfig.copy(store = "Summers Ice Cream")
            transactionViewModel.updateFilterConfig(updatedFilterConfig)
            dismiss()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("MMMM-dd-yyyy", Locale.US)
                val formattedDate = dateFormat.format(selectedDate.time)

                if (isStartDate) {
                    startDate.text = formattedDate
                    startDateConfig = selectedDate.time
                } else {
                    endDate.text = formattedDate
                    endDateConfig = selectedDate.time
                }
            }, year, month, day
        )
        datePickerDialog.show()
    }
}