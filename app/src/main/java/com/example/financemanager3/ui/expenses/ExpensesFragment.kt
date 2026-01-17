package com.example.financemanager3.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.financemanager3.R
import com.example.financemanager3.model.TransactionType
import com.example.financemanager3.ui.AddTransactionDialogFragment
import com.example.financemanager3.ui.TransactionAdapter
import com.example.financemanager3.ui.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpensesFragment : Fragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        transactionAdapter = TransactionAdapter(emptyList())
        view.findViewById<RecyclerView>(R.id.rv_expenses_transactions).adapter = transactionAdapter

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateData(transactions.filter { it.type == TransactionType.EXPENSE })
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_expense).setOnClickListener {
            AddTransactionDialogFragment.newInstance(TransactionType.EXPENSE)
                .show(parentFragmentManager, "AddTransactionDialog")
        }

        return view
    }
}