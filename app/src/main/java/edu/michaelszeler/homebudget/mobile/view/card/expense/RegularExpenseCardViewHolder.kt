package edu.michaelszeler.homebudget.mobile.view.card.expense

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import edu.michaelszeler.homebudget.mobile.R

class RegularExpenseCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var regularExpenseName: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_name)
    var regularExpenseCategory: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_category)
    var regularExpenseAmount: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_amount)
    var regularExpenseStartDate: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_start_date)
    var regularExpenseMonths: MaterialTextView = itemView.findViewById(R.id.text_view_regular_expense_card_months)
    var regularExpenseShowChartButton: MaterialButton = itemView.findViewById(R.id.button_regular_expense_card)
    var regularExpenseDeleteButton: MaterialButton = itemView.findViewById(R.id.button_regular_expense_card_delete)
}