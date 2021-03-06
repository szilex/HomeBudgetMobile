package edu.michaelszeler.homebudget.mobile.view.card.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.model.budget.BudgetSummaryEntry
import edu.michaelszeler.homebudget.mobile.view.listener.DeleteBudgetOnClickListener
import edu.michaelszeler.homebudget.mobile.view.listener.ShowBudgetSummaryChartOnClickListener
import java.util.*

class BudgetSummaryCardRecyclerViewAdapter(private val budgetList: List<BudgetSummaryEntry>, private val fragmentManager: FragmentManager?) : RecyclerView.Adapter<BudgetSummaryCardViewHolder>() {

    private lateinit var deleteBudgetOnClickListener: DeleteBudgetOnClickListener

    fun setDeleteBudgetOnClickListener (listener: DeleteBudgetOnClickListener) {
        this.deleteBudgetOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetSummaryCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.card_budget_summary, parent, false)
        return BudgetSummaryCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: BudgetSummaryCardViewHolder, position: Int) {
        if (position < budgetList.size) {
            val product = budgetList[position]
            val startCalendar = Calendar.getInstance()
            startCalendar.time = product.date
            holder.budgetDate.text = String.format("%d-%d", startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1)
            holder.budgetIncome.text = String.format("%10.2f", product.income)
            holder.budgetCustomExpenses.text = String.format("%10.2f", product.customExpenses)
            holder.budgetRegularExpenses.text = String.format("%10.2f", product.regularExpenses)
            holder.budgetStrategies.text = String.format("%10.2f", product.strategies)
            holder.budgetShowChartButton.setOnClickListener(ShowBudgetSummaryChartOnClickListener(fragmentManager!!, product))
            if (this::deleteBudgetOnClickListener.isInitialized) {
                holder.budgetDeleteButton.setOnClickListener {
                    deleteBudgetOnClickListener.setBudget(product)
                    deleteBudgetOnClickListener.onClick(it)
                }
            } else {
                holder.budgetDeleteButton.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return budgetList.size
    }
}