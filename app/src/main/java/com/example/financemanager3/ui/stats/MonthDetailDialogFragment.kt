package com.example.financemanager3.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.financemanager3.R
import com.example.financemanager3.model.TransactionType
import com.example.financemanager3.ui.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonthDetailDialogFragment : DialogFragment() {

    // Použijeme 'activityViewModels', abychom sdíleli data s hlavní aktivitou (seznam transakcí)
    private val viewModel: TransactionViewModel by activityViewModels()

    private var year: Int = 0
    private var month: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Načtení argumentů (rok a měsíc), které jsme si poslali
        arguments?.let {
            year = it.getInt(ARG_YEAR)
            month = it.getInt(ARG_MONTH)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_month_detail, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_title)
        val tvIncomeList = view.findViewById<TextView>(R.id.tv_detail_income_list)
        val tvExpenseList = view.findViewById<TextView>(R.id.tv_detail_expense_list)
        val btnClose = view.findViewById<Button>(R.id.btn_close_detail)

        // Nastavení titulku (např. "Únor 2025")
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("cs", "CZ"))
        tvTitle.text = monthFormat.format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Načtení dat a výpočet statistik
        viewModel.allTransactions.observe(viewLifecycleOwner) { allTransactions ->

            // 1. Vyfiltrujeme transakce pouze pro tento měsíc a rok
            val monthTransactions = allTransactions.filter {
                val tDate = Calendar.getInstance().apply { time = it.date }
                tDate.get(Calendar.YEAR) == year && tDate.get(Calendar.MONTH) == month
            }

            // 2. Helper funkce pro formátování měny
            val formatMoney = { amount: Double ->
                NumberFormat.getNumberInstance(Locale("cs", "CZ")).format(amount) + " Kč"
            }

            // 3. Zpracování PŘÍJMŮ
            val incomeMap = monthTransactions
                .filter { it.type == TransactionType.INCOME }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second } // Seřadit od nejvyšší částky

            if (incomeMap.isNotEmpty()) {
                val sb = StringBuilder()
                for ((category, amount) in incomeMap) {
                    sb.append("$category: ${formatMoney(amount)}\n")
                }
                tvIncomeList.text = sb.toString().trim()
            } else {
                tvIncomeList.text = "Žádné příjmy"
            }

            // 4. Zpracování VÝDAJŮ
            val expenseMap = monthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }

            if (expenseMap.isNotEmpty()) {
                val sb = StringBuilder()
                for ((category, amount) in expenseMap) {
                    sb.append("$category: ${formatMoney(amount)}\n")
                }
                tvExpenseList.text = sb.toString().trim()
            } else {
                tvExpenseList.text = "Žádné výdaje"
            }
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // Aby bylo okno dostatečně široké
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        private const val ARG_YEAR = "year"
        private const val ARG_MONTH = "month"

        fun newInstance(year: Int, month: Int): MonthDetailDialogFragment {
            val fragment = MonthDetailDialogFragment()
            val args = Bundle()
            args.putInt(ARG_YEAR, year)
            args.putInt(ARG_MONTH, month)
            fragment.arguments = args
            return fragment
        }
    }
}