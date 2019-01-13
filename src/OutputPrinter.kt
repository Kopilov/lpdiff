package com.github.kopilov.lpdiff

interface OutputPrinter {
    fun printVariablesNames(variablesNames: Iterable<String>)
    /**
     * Print variables (LinearItem coefficient) value for each name
     * or null if no variable with such name is presented
     */
    fun printFunctionValuesForNames(variables: Iterable<LinearItem>, names: Iterable<String>, rightSide: Double);
    fun printFunctionsPair(itemsInPairs: Iterable<DoubleLinearItem>);
}
