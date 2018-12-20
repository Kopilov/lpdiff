package com.github.kopilov.lpdiff

data class LinearBound (
        val lowerBound: Double?, //-inf if null
        val variableName: String,
        val upperBound: Double? //inf if null
) {

}