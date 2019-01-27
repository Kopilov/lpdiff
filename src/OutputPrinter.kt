package com.github.kopilov.lpdiff

interface OutputPrinter {
    /**print variables names (once, to print many values then)*/
    fun printVariablesNames(variablesNames: Iterable<String>)
    /**Same as [printVariablesNames] but add some common columns names (sign, right side, etc)*/
    fun printConstraintHeader(leftColumnsNames: Iterable<String>, variablesNames: Iterable<String>, rightColumnsNames: Iterable<String>)
    /**
     * Print variables (LinearItem coefficient) value for each name
     * or null if no variable with such name is presented
     */
    fun printFunctionValuesForNames(variables: Iterable<LinearItem>, names: Iterable<String>, moreLeftCells: Iterable<String>, moreRightCells: Iterable<String>);
    fun printFunctionsPair(itemsInPairs: Iterable<DoubleLinearItem>);
}
