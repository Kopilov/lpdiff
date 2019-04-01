package com.github.kopilov.lpdiff

import java.util.HashMap
import java.util.TreeSet
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
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
            if (middle == 0.0) {
                return absoluteDifference;
            } else {
                return absoluteDifference?.div(middle.absoluteValue);
            }
        } else {
            return null;
        }
    }

    fun haveLostValue(): Boolean {
        return coefficient1 == null || coefficient2 == null;
    }
}

data class Matches(val numberOfSimilar: Int, val numberOfUnique1: Int, val numberOfUnique2: Int){}

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

fun convertFunctionItems(function: LinearFunction, converter: (item: LinearItem) -> LinearItem): LinearFunction {
    val convertedFunction = LinearFunction();
    for (item in function.items) {
        convertedFunction.append(converter(item));
    }
    return convertedFunction;
}

fun convertModelItems(
        model: LinearModel,
        itemConverter: (item: LinearItem) -> LinearItem,
        boundConverter: (bound: LinearVariableBound) -> LinearVariableBound
): LinearModel {
    val executor = ForkJoinPool();
    val cache = Array<LinearConstraint?>(model.constraints.size, {null});
    var objective: LinearFunction? = null;
    executor.execute {
        objective = convertFunctionItems(model.objective, itemConverter);
    };
    val i = AtomicInteger(0);
    for (constraint in model.constraints) {
        executor.execute {
            val i_immutable = i.getAndIncrement();
            val leftSide = convertFunctionItems(constraint.leftSide, itemConverter);
            cache[i_immutable] = LinearConstraint(
                    constraint.name,
                    leftSide,
                    constraint.sign,
                    constraint.rightSide
            );
        };
    }
    executor.shutdown();
    while(!executor.isTerminated) {
        Thread.sleep(10);
    }
    val constraints = TreeSet<LinearConstraint>();
    for (constraint in cache) {
        constraints.add(constraint!!);
    }
    val bounds = TreeSet<LinearVariableBound>();
    for (bound in model.bounds) {
        bounds.add(boundConverter(bound));
    }
    return LinearModel(
            model.target,
            objective!!,
            constraints,
            bounds
    );
}

fun compareConstraints(constraint1: LinearConstraint, constraint2: LinearConstraint): Double {
    val stitchedConstraints = stitchLinearFunctions(constraint1.leftSide, constraint2.leftSide);
    var totalDifference = 0.0;
    for (itemPair in stitchedConstraints) {
        val itemDiffrence = itemPair.calculateRelativeDifference();
        if (itemDiffrence != null) {
            totalDifference += itemDiffrence;
        }
    }
    val rightSideDiffrence = DoubleLinearItem("rightSide", constraint1.rightSide, constraint2.rightSide).calculateRelativeDifference();
    if (rightSideDiffrence != null) {
        totalDifference += rightSideDiffrence;
    }
    return totalDifference;
}
