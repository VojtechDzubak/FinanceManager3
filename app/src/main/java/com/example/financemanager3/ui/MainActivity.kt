package com.example.financemanager3.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.financemanager3.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        val summaryCard = findViewById<MaterialCardView>(R.id.summary_card)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.statisticsFragment) {
                summaryCard.visibility = View.GONE
            } else {
                summaryCard.visibility = View.VISIBLE
            }
        }

        val tvTotalIncome = findViewById<TextView>(R.id.tv_total_income)
        val tvTotalExpenses = findViewById<TextView>(R.id.tv_total_expenses)
        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val tvSelectedMonth = findViewById<TextView>(R.id.tv_selected_month)
        val btnPreviousMonth = findViewById<ImageButton>(R.id.btn_previous_month)
        val btnNextMonth = findViewById<ImageButton>(R.id.btn_next_month)

        val numberFormat = NumberFormat.getNumberInstance(Locale("cs", "CZ"))
        val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("cs", "CZ"))

        viewModel.totalIncome.observe(this) { income ->
            tvTotalIncome.text = "+ ${numberFormat.format(income)} Kč"
            tvTotalIncome.setTextColor(ContextCompat.getColor(this, R.color.incomeColor))
        }

        viewModel.totalExpenses.observe(this) { expenses ->
            tvTotalExpenses.text = "- ${numberFormat.format(expenses)} Kč"
            tvTotalExpenses.setTextColor(ContextCompat.getColor(this, R.color.expenseColor))
        }

        viewModel.balance.observe(this) { balance ->
            tvBalance.text = "${numberFormat.format(balance)} Kč"
            if (balance >= 0) {
                tvBalance.setTextColor(ContextCompat.getColor(this, R.color.incomeColor))
            } else {
                tvBalance.setTextColor(ContextCompat.getColor(this, R.color.expenseColor))
            }
        }

        viewModel.selectedDate.observe(this) { calendar ->
            tvSelectedMonth.text = monthFormat.format(calendar.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        btnPreviousMonth.setOnClickListener {
            viewModel.selectPreviousMonth()
        }

        btnNextMonth.setOnClickListener {
            viewModel.selectNextMonth()
        }
    }
}