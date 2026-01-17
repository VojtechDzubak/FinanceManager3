package com.example.financemanager3.ui.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.financemanager3.R
import com.example.financemanager3.ui.TransactionAdapter
import com.example.financemanager3.ui.TransactionViewModel

class AllTransactionsFragment : Fragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_transactions, container, false)

        transactionAdapter = TransactionAdapter(emptyList())
        view.findViewById<RecyclerView>(R.id.rv_all_transactions).adapter = transactionAdapter

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateData(transactions)
        }

        return view
    }
}