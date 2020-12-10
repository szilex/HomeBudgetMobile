package edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.expense

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class RegularExpenseCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var regularExpenseName: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_name)
    var regularExpenseCategory: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_category)
    var regularExpenseAmount: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_amount)
    var regularExpenseStartDate: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_start_date)
    var regularExpenseMonths: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_months)
    var regularExpenseButton: MaterialButton = itemView.findViewById(R.id.button_regular_expense_card)
}