package com.example.financemanager3.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financemanager3.R
import com.example.financemanager3.model.Transaction
import com.example.financemanager3.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    fun getTransactionAt(position: Int): Transaction {
        return transactions[position]
    }
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_transaction_name)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tv_transaction_category)
        private val amountTextView: TextView = itemView.findViewById(R.id.tv_transaction_amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_transaction_date)

        fun bind(transaction: Transaction) {
            nameTextView.text = transaction.name
            categoryTextView.text = transaction.category
            val numberFormat = NumberFormat.getNumberInstance(Locale("cs", "CZ"))
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(transaction.date)

            if (transaction.type == TransactionType.INCOME) {
                amountTextView.text = "+ ${numberFormat.format(transaction.amount)} Kč"
                amountTextView.setTextColor(Color.GREEN)
            } else {
                amountTextView.text = "- ${numberFormat.format(transaction.amount)} Kč"
                amountTextView.setTextColor(Color.RED)
            }
        }
    }
}