package edu.michaelszeler.homebudget.HomeBudgetMobile.view.card.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.listener.DeleteRegularExpenseOnClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.listener.ShowRegularExpenseChartOnClickListener
import java.util.*

class RegularExpenseCardRecyclerViewAdapter(private val regularExpenseList: List<RegularExpenseEntry>, private val fragmentManager: FragmentManager?) : RecyclerView.Adapter<RegularExpenseCardViewHolder>() {

    private lateinit var deleteRegularExpenseOnClickListener: DeleteRegularExpenseOnClickListener

    fun setDeleteRegularExpenseOnClickListener (listener: DeleteRegularExpenseOnClickListener) {
        this.deleteRegularExpenseOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegularExpenseCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.card_regular_expense, parent, false)
        return RegularExpenseCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: RegularExpenseCardViewHolder, position: Int) {
        if (position < regularExpenseList.size) {
            val product = regularExpenseList[position]
            val startCalendar = Calendar.getInstance()
            startCalendar.time = product.startDate
            holder.regularExpenseName.text = product.name
            holder.regularExpenseCategory.text = product.category
            holder.regularExpenseAmount.text = product.amount.toString()
            holder.regularExpenseStartDate.text = String.format("%d-%d-%d", startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH))
            holder.regularExpenseMonths.text = product.months.toString()
            holder.regularExpenseButton.setOnClickListener(ShowRegularExpenseChartOnClickListener(fragmentManager!!, product.amount.toDouble(), product.months, calculateCurrentMonth(startCalendar)))
            if (this::deleteRegularExpenseOnClickListener.isInitialized) {
                holder.regularExpenseDeleteButton.setOnClickListener {
                    deleteRegularExpenseOnClickListener.setRegularExpense(regularExpenseList[position])
                    deleteRegularExpenseOnClickListener.onClick(it)
                }
            } else {
                holder.regularExpenseDeleteButton.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return regularExpenseList.size
    }

    private fun calculateCurrentMonth(startCalendar: Calendar) : Int {
        var i = 1
        val currentCalendar = Calendar.getInstance()
        while (!(currentCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR) && currentCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH))) {
            i++
            startCalendar.add(Calendar.MONTH, 1)
        }
        return i
    }

}