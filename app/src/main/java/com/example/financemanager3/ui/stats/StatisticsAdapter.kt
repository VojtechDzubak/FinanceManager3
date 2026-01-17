package com.example.financemanager3.ui.stats

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financemanager3.R
import com.example.financemanager3.model.MonthlyStat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ZMĚNA 1: Přidali jsme 'private val onMonthClick: (MonthlyStat) -> Unit' do konstruktoru
class StatisticsAdapter(
    private var stats: List<MonthlyStat>,
    private val onMonthClick: (MonthlyStat) -> Unit
) : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_stat, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val stat = stats[position]
        holder.bind(stat)

        // ZMĚNA 2: Nastavení reakce na kliknutí
        holder.itemView.setOnClickListener {
            onMonthClick(stat)
        }
    }

    override fun getItemCount(): Int = stats.size

    fun updateData(newStats: List<MonthlyStat>) {
        stats = newStats
        notifyDataSetChanged()
    }

    class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ... (zbytek třídy zůstává stejný)
        private val monthYearTextView: TextView = itemView.findViewById(R.id.tv_month_year)
        private val incomeTextView: TextView = itemView.findViewById(R.id.tv_stat_income)
        private val expensesTextView: TextView = itemView.findViewById(R.id.tv_stat_expenses)
        private val balanceTextView: TextView = itemView.findViewById(R.id.tv_stat_balance)

        fun bind(stat: MonthlyStat) {
            val calendar = Calendar.getInstance()
            calendar.set(stat.year, stat.month, 1)
            val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("cs", "CZ"))
            monthYearTextView.text = monthFormat.format(calendar.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            val numberFormat = NumberFormat.getNumberInstance(Locale("cs", "CZ"))
            incomeTextView.text = "+ ${numberFormat.format(stat.totalIncome)} Kč"
            expensesTextView.text = "- ${numberFormat.format(stat.totalExpenses)} Kč"
            balanceTextView.text = "${numberFormat.format(stat.balance)} Kč"

            if (stat.balance >= 0) {
                balanceTextView.setTextColor(Color.GREEN)
            } else {
                balanceTextView.setTextColor(Color.RED)
            }
        }
    }
}