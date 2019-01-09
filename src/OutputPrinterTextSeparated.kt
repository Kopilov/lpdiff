package com.github.kopilov.lpdiff

import java.io.PrintStream

class OutputPrinterTextSeparated constructor(val stream: PrintStream, val delimiter: String) : OutputPrinter {

    constructor() : this(System.out, "\t") {}

    override fun printFunctionsPair(itemsInPairs: Collection<DoubleLinearItem>) {
        stream.println("variable_name${delimiter}coefficient_value_1${delimiter}coefficient_value_2");
        for (itemPair in itemsInPairs) {
            stream.println("${itemPair.name}$delimiter${itemPair.coefficient1}$delimiter${itemPair.coefficient2}");
        }
    }
}
