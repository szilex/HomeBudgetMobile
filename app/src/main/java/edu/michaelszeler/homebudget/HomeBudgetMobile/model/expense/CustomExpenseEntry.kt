package edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.*

class CustomExpenseEntry (val name: String, val category: String, val amount: BigDecimal, val date: Date, val months: Int) {
    companion object {
        fun convertToCustomExpenseList(content: String): List<RegularExpenseEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<RegularExpenseEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }
}