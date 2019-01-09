package com.github.kopilov.lpdiff

interface OutputPrinter {
    fun printFunctionsPair(itemsInPairs: Collection<DoubleLinearItem>)
}