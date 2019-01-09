package com.github.kopilov.lpdiff

import java.io.PrintStream

class OutputPrinterTextSeparated constructor(val stream: PrintStream, val delimiter: String) : OutputPrinter {

    constructor() : this(System.out, "\t") {}

    override fun printFunctionsPair(itemsInPairs: Collection<DoubleLinearItem>) {
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
