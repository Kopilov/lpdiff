package com.github.kopilov.lpdiff;

import java.text.DecimalFormat
import java.util.*
import kotlin.math.absoluteValue
import java.text.NumberFormat

/**
 * Linear variable ([name]) with float [coefficient] or unnamed const
 */
data class LinearItem(val coefficient: Double, val name: String?) : Comparable<LinearItem> {
    override fun compareTo(other: LinearItem): Int {
        if (name == null && other.name == null) {
            return 0;
        }
        if (name == null) {
            return 1;
        }
        if (other.name == null) {
            return -1;
        }
        return name.compareTo(other.name);
    }

    fun format(locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
        val sign = if (coefficient >= 0) "+" else "-";
        val coefficientAbsoluteValue = coefficient.absoluteValue;
        val coefficientFormatted = if (coefficientAbsoluteValue <= 10E-5 || coefficientAbsoluteValue >= 10E10) {
            "%e".format(locale, coefficientAbsoluteValue)
        } else {
            val nf = NumberFormat.getNumberInstance(locale);
            val decimalFormat = nf as DecimalFormat;
            decimalFormat.applyPattern("#.###############");
            decimalFormat.format(coefficientAbsoluteValue);
        }
        return if (name == null) {
            "$sign $coefficientFormatted";
        } else {
            "$sign $coefficientFormatted $name";
        }
    }
}

fun createLinearItem(sign: Boolean, coefficientSrc: String, name: String?): LinearItem {
    val coefficient = if (sign) coefficientSrc.toDouble() else coefficientSrc.toDouble() * -1;
    return LinearItem(coefficient, name);
}

/**Parse [sourceRaw] string like `-50 Z` to [LinearItem] object*/
fun parseLinearItem(sourceRaw: String): LinearItem {
    val source = sourceRaw.trim().replace(',', '.');

    //Parse sign. Positive if we do not have '-'. Then remove any (+ or -) sign.
    val sign = !source.startsWith('-');
    val unsignedSource = if (source.startsWith('-') || source.startsWith('+'))
        source.substring(1).trim();
    else
        source;

    //Coefficient, if it is presented, should be space-separated
    if (unsignedSource.contains(' ')) {
        val splittedSource = unsignedSource.split(Regex("[\\p{IsWhite_Space}]+"));
        val coefficient = splittedSource.first();
        val name = splittedSource.last();
        return createLinearItem(sign, coefficient, name);
    } else { //this can be unnamed number or name without presented coefficient (== 1)
        if (unsignedSource.contains(Regex("^[\\p{IsDigit}\\p{IsPunctuation}]"))) { //number should begin with digit or dot
            try { //nevertheless, try to parse it before
                val assertConst = unsignedSource.toDouble();
                return createLinearItem(sign, unsignedSource, null);
            } catch (e: NumberFormatException) {
                //if .toDouble() threw an exception, we have a name, but it is probably invalid()
                return createLinearItem(sign, "1.0", unsignedSource);
            }
        } else {
            return createLinearItem(sign, "1.0", unsignedSource);
        }
    }
}
