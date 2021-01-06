package edu.michaelszeler.homebudget.mobile.view.card.strategy

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import edu.michaelszeler.homebudget.mobile.R

class StrategyCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var strategyName: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_name)
    var strategyDescription: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_description)
    var strategyCategory: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_category)
    var strategyGoal: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_goal)
    var strategyStartDate: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_start_date)
    var strategyMonths: MaterialTextView = itemView.findViewById(R.id.text_view_strategy_card_months)
    var strategyButton: MaterialButton = itemView.findViewById(R.id.button_strategy_card)
    var strategyDeleteButton: MaterialButton = itemView.findViewById(R.id.button_strategy_card_delete)
}