package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.adapter.strategy

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class StrategyCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var strategyName: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_name)
    var strategyDescription: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_description)
    var strategyCategory: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_category)
    var strategyGoal: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_goal)
    var strategyStartDate: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_start_date)
    var strategyMonths: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_months)
    var strategyButton: MaterialButton = itemView.findViewById(R.id.button_strategy_card)
}