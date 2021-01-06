package edu.michaelszeler.homebudget.mobile.model.expense

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.*

class RegularExpenseEntry(val id: Int, val name: String, val category: String, val amount: BigDecimal, val startDate: Date, val months: Int) {

    companion object {
        fun convertToRegularExpenseList(content: String): List<RegularExpenseEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<RegularExpenseEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }
}