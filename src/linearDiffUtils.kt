package com.github.kopilov.lpdiff

import java.util.HashMap
import java.util.TreeSet
import kotlin.math.absoluteValue

fun compareByNullableNames(name1: String?, name2: String?): Int{
    if (name1 == null && name2 == null) {
        return 0;
    }
    if (name1 == null) {
        return 1;
    }
    if (name2 == null) {
        return -1;
    }
    return name1.compareTo(name2);
}

data class DoubleLinearItem(val name: String?, val coefficient1: Double?, val coefficient2: Double?) : Comparable<DoubleLinearItem> {
    override fun compareTo(other: DoubleLinearItem): Int {
        return compareByNullableNames(name, other.name)
    }

    fun calculateAbsoluteDifference(): Double? {
        if (coefficient1 is Double && coefficient2 is Double) {
            return (coefficient1 - coefficient2).absoluteValue;
        } else {
            return null;
        }
    }

    fun calculateRelativeDifference(): Double? {
        if (coefficient1 is Double && coefficient2 is Double) {
            val middle = (coefficient1 + coefficient2) / 2;
            val absoluteDifference = calculateAbsoluteDifference();
            return absoluteDifference?.div(middle);
        } else {
            return null;
        }
    }

    fun haveLostValue(): Boolean {
        return coefficient1 == null || coefficient2 == null;
    }
}

/** Find [LinearItem]s with same names in [fun1] and [fun2], put their names and values in the single collection */
fun stitchLinearFunctions(fun1: LinearFunction, fun2: LinearFunction): Collection<DoubleLinearItem> {
    val stitchedItems = TreeSet<DoubleLinearItem>();
    val index = HashMap<String?, DoubleLinearItem>();

    for (item1 in fun1.items) {
        val doubleItem = DoubleLinearItem(item1.name, item1.coefficient, null);
        stitchedItems.add(doubleItem);
        index.put(item1.name, doubleItem);
    }
    for (item2 in fun2.items) {
        if (index.containsKey(item2.name)) {
            val doubleItem = index[item2.name]
            stitchedItems.remove(doubleItem);
            stitchedItems.add(DoubleLinearItem(item2.name, doubleItem?.coefficient1, item2.coefficient));
        } else {
            stitchedItems.add(DoubleLinearItem(item2.name, null, item2.coefficient));
        }
    }
    return stitchedItems;
}
