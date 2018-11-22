package com.github.kopilov.lpdiff;

import java.util.regex.Pattern

/**
 * Linear variable with float coefficient
 */
class LinearItem(
        sign: Boolean, //positive true, negative false
        coefficient: Double,
        name: String
) {
    val sign = sign;
    val coefficient = coefficient;
    val name = name;

}

/**Parse [source]string like `-50 Z` to [LinearItem] object*/
fun parseLinearItem(source: String): LinearItem {
    var sourceTrimmed = source.trim();

    //Parse sign. Positive if we do not have '-'. Then remove any (+ or -) sign.
    val sign = !sourceTrimmed.startsWith('-');
    if (sourceTrimmed.startsWith('-') || sourceTrimmed.startsWith('+')) {
        sourceTrimmed = sourceTrimmed.substring(1).trim();
    }

    //Coefficient, if it is presented, should be space-separated
    if (sourceTrimmed.contains(' ')) {
        val splittedSource = sourceTrimmed.split(Regex("[\\p{IsWhite_Space}]*"));
        val coefficient = splittedSource.first();
        val name = splittedSource.last();
        return LinearItem(sign, coefficient.toDouble(), name);
    } else {
        //This is variable name without coefficient
        return LinearItem(sign, 1.0, sourceTrimmed);
        //todo: constant without name can also be here
    }

}
