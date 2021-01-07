package edu.michaelszeler.homebudget.mobile.view.card.budget

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import edu.michaelszeler.homebudget.mobile.R

class BudgetSummaryCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var budgetDate: MaterialTextView = itemView.findViewById(R.id.text_view_budget_summary_card_date)
    var budgetIncome: MaterialTextView = itemView.findViewById(R.id.text_view_budget_summary_card_income)
    var budgetCustomExpenses: MaterialTextView = itemView.findViewById(R.id.text_view_budget_summary_card_custom_expenses)
    var budgetRegularExpenses: MaterialTextView = itemView.findViewById(R.id.text_view_budget_summary_card_regular_expenses)
    var budgetStrategies: MaterialTextView = itemView.findViewById(R.id.text_view_budget_summary_card_strategies)
    var budgetShowChartButton: MaterialButton = itemView.findViewById(R.id.button_budget_summary_card)
    var budgetDeleteButton: MaterialButton = itemView.findViewById(R.id.button_budget_summary_card_delete)
}