package edu.michaelszeler.homebudget.mobile.model.budget

import edu.michaelszeler.homebudget.mobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.strategy.StrategyEntry
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.stream.Collectors

class BudgetSummaryEntry(val id: Int, val date: Date, val income: BigDecimal, val customExpenses: BigDecimal, val regularExpenses: BigDecimal, val strategies: BigDecimal) {

    companion object {
        fun convertToBudgetSummaryList(budgets: List<BudgetEntry>) : List<BudgetSummaryEntry> {
            return budgets.stream()
                    .map { budget ->  BudgetSummaryEntry(budget.id, budget.date, budget.income, budget.customExpenses.sumCustomExpenses(), budget.regularExpenses.sumRegularExpenses(), budget.strategies.sumStrategies()) }
                    .collect(Collectors.toList())
        }

        private fun Iterable<CustomExpenseEntry>.sumCustomExpenses(): BigDecimal {
            var sum: BigDecimal = BigDecimal.ZERO
            for (element in this) {
                sum += element.amount
            }
            return sum
        }

        private fun Iterable<RegularExpenseEntry>.sumRegularExpenses(): BigDecimal {
            var sum: BigDecimal = BigDecimal.ZERO
            for (element in this) {
                sum += element.amount
            }
            return sum
        }

        private fun Iterable<StrategyEntry>.sumStrategies(): BigDecimal {
            var sum: BigDecimal = BigDecimal.ZERO
            for (element in this) {
                sum += element.goal.divide(BigDecimal(element.months), 2, RoundingMode.HALF_UP)
            }
            return sum
        }
    }


}