package com.example.financemanager3.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.financemanager3.R
import com.example.financemanager3.data.CategoryDataSource
import com.example.financemanager3.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionDialogFragment : DialogFragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var etDate: EditText
    private lateinit var categorySpinner: Spinner
    private var transactionType: TransactionType = TransactionType.INCOME

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_transaction, container, false)

        val etName = view.findViewById<EditText>(R.id.et_transaction_name)
        val etAmount = view.findViewById<EditText>(R.id.et_transaction_amount)
        etDate = view.findViewById(R.id.et_transaction_date)
        categorySpinner = view.findViewById(R.id.spinner_category)

        val categories = if (transactionType == TransactionType.INCOME) {
            CategoryDataSource.incomeCategories
        } else {
            CategoryDataSource.expenseCategories
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_add).setOnClickListener {
            val name = etName.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull()
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(etDate.text.toString())
            val category = categorySpinner.selectedItem.toString()

            if (name.isNotEmpty() && amount != null && date != null) {
                viewModel.addTransaction(name, amount, date, transactionType, category)
                dismiss()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                etDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    companion object {
        fun newInstance(type: TransactionType): AddTransactionDialogFragment {
            val fragment = AddTransactionDialogFragment()
            fragment.transactionType = type
            return fragment
        }
    }
}