package com.github.kopilov.lpdiff

import java.io.PrintStream

class OutputPrinterTextSeparated constructor(val stream: PrintStream, val delimiter: String) : OutputPrinter {

    constructor() : this(System.out, "\t") {}

    override fun printVariablesNames(variablesNames: Iterable<String>) {
        stream.println(variablesNames.joinToString(delimiter));
    }

    override fun printFunctionValuesForNames(variables: Iterable<LinearItem>, names: Iterable<String>, rightSide: Double) {
        val index = HashMap<String?, Double>();
        for (variable in variables) {
            index.put(variable.name, variable.coefficient);
        }
        val rowCells = ArrayList<String>();
        fun printNameValue(name: String) {
            if (index.containsKey(name)) {
                rowCells.add(index.get(name).toString());
            } else {
                rowCells.add("");
            }
        }
        for (name in names) {
            printNameValue(name);
        }
        rowCells.add(rightSide.toString());
        stream.println(rowCells.joinToString(delimiter));
    }

    override fun printFunctionsPair(itemsInPairs: Iterable<DoubleLinearItem>) {
        val headerCells = arrayListOf(
                "variable_name",
                "coefficient_value_1",
                "coefficient_value_2",
                "is_lost_or_extraneous",
                "absolute_difference",
                "relative_difference"
        );
        stream.println(headerCells.joinToString(delimiter));
        for (itemPair in itemsInPairs) {
            val rowCells = arrayListOf(
                    itemPair.name.orEmpty(),
                    itemPair.coefficient1?: "",
                    itemPair.coefficient2?: "",
                    if (itemPair.haveLostValue()) "True" else "",
                    itemPair.calculateAbsoluteDifference()?: "",
                    itemPair.calculateRelativeDifference()?: ""
            );
            stream.println(rowCells.joinToString(delimiter));
        }
    }
}
