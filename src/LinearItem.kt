package com.github.kopilov.lpdiff;

import java.util.regex.Pattern

/**
 * Linear variable with float coefficient or unnamed const
 */
class LinearItem(val coefficient: Double, val name: String?) {}

fun createLinearItem(sign: Boolean, coefficientSrc: String, name: String?): LinearItem {
    val coefficient = if (sign) coefficientSrc.toDouble() else coefficientSrc.toDouble() * -1;
    return LinearItem(coefficient, name);
}

/**Parse [source]string like `-50 Z` to [LinearItem] object*/
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
        val splittedSource = unsignedSource.split(Regex("[\\p{IsWhite_Space}]*"));
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
