package com.example.financemanager3.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.financemanager3.R
import com.example.financemanager3.ui.TransactionViewModel

class StatisticsFragment : Fragment() {

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var statisticsAdapter: StatisticsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        // ZMĚNA: Do adaptéru posíláme funkci, která se provede při kliknutí
        statisticsAdapter = StatisticsAdapter(emptyList()) { clickedStat ->
            // Kód, který se provede po kliknutí na měsíc:
            MonthDetailDialogFragment.newInstance(clickedStat.year, clickedStat.month)
                .show(parentFragmentManager, "MonthDetailDialog")
        }

        view.findViewById<RecyclerView>(R.id.rv_statistics).adapter = statisticsAdapter

        viewModel.monthlyStats.observe(viewLifecycleOwner) { stats ->
            statisticsAdapter.updateData(stats)
        }

        return view
    }
}