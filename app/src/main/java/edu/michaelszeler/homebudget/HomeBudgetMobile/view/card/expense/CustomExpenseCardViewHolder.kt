package edu.michaelszeler.homebudget.HomeBudgetMobile.view.card.expense

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R

class CustomExpenseCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var customExpenseName: MaterialTextView = itemView.findViewById(R.id.text_view_custom_expense_card_name)
    var customExpenseCategory: MaterialTextView = itemView.findViewById(R.id.text_view_custom_expense_card_category)
    var customExpenseAmount: MaterialTextView = itemView.findViewById(R.id.text_view_custom_expense_card_amount)
    var customExpenseStartDate: MaterialTextView = itemView.findViewById(R.id.text_view_custom_expense_card_date)
}