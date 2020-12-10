package edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.expense

import android.content.res.Resources
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.math.BigDecimal
import java.util.*

class RegularExpenseEntry(val name: String, val category: String, val amount: BigDecimal, val startDate: Date, val months: Int) {

    companion object {

        fun convertToRegularExpenseList(resources: Resources): List<RegularExpenseEntry> {
            val inputStream = resources.openRawResource(R.raw.products)
            val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<RegularExpenseEntry>>() {}.type
            return gson.fromJson(jsonProductsString, productListType)
        }

        fun convertToRegularExpenseList(content: String): List<RegularExpenseEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<RegularExpenseEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }
}