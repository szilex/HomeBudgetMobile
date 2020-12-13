package edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.CustomExpenseEntry
import java.util.*

class CustomExpenseCardRecyclerViewAdapter(private val customExpenseList: List<CustomExpenseEntry>, private val fragmentManager: FragmentManager?) : RecyclerView.Adapter<CustomExpenseCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomExpenseCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_custom_expense_card, parent, false)
        return CustomExpenseCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: CustomExpenseCardViewHolder, position: Int) {
        if (position < customExpenseList.size) {
            val product = customExpenseList[position]
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = product.date
            holder.customExpenseName.text = product.name
            holder.customExpenseCategory.text = product.category
            holder.customExpenseAmount.text = product.amount.toString()
            holder.customExpenseStartDate.text = String.format("%d-%d-%d", dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH))
        }
    }

    override fun getItemCount(): Int {
        return customExpenseList.size
    }
}