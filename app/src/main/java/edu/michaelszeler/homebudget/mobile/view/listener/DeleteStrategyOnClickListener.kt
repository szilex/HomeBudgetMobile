package edu.michaelszeler.homebudget.mobile.view.listener

import android.view.View
import edu.michaelszeler.homebudget.mobile.model.strategy.StrategyEntry

interface DeleteStrategyOnClickListener : View.OnClickListener {
    fun setStrategy(strategy: StrategyEntry)
}