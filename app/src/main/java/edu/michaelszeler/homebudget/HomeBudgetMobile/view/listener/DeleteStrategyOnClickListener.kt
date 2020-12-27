package edu.michaelszeler.homebudget.HomeBudgetMobile.view.listener

import android.view.View
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy.StrategyEntry

interface DeleteStrategyOnClickListener : View.OnClickListener {
    fun setStrategy(strategy: StrategyEntry)
}