package com.example.financemanager3.ui.all

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_all_transactions)

        transactionAdapter = TransactionAdapter(emptyList())
        recyclerView.adapter = transactionAdapter

        // --- ZAČÁTEK KÓDU PRO MAZÁNÍ S ČERVENÝM POZADÍM ---
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Získáme pozici a transakci ke smazání
                val position = viewHolder.adapterPosition
                val transactionToDelete = transactionAdapter.getTransactionAt(position)

                // Smažeme přes ViewModel
                viewModel.deleteTransaction(transactionToDelete)

                // Info pro uživatele
                Toast.makeText(context, "Transakce smazána", Toast.LENGTH_SHORT).show()
            }

            // Vykreslení červeného pozadí při posunu
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)

                if (dX > 0) { // Swipe doprava
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                } else if (dX < 0) { // Swipe doleva
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                } else {
                    background.setBounds(0, 0, 0, 0)
                }

                background.draw(c)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        // --- KONEC KÓDU ---

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateData(transactions)
        }

        return view
    }
}