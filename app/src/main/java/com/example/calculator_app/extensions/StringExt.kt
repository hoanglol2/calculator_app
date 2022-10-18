package com.example.calculator_app.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun String?.formatDecimal(): String {
    val formatter = DecimalFormat()
    formatter.apply {
        groupingSize = 3
        decimalFormatSymbols = DecimalFormatSymbols().apply {
            groupingSeparator = ','
        }
    }
    return formatter.format(this?.toIntOrNull() ?: 0)
}