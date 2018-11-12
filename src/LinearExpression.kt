package com.github.kopilov.lpdiff;

import java.util.*

/**
 * Linear programming expression containing sum of items
 */
class LinearExpression(items: Collection<LinearItem>) {
    val items = TreeSet<LinearItem>(items);

    fun append(item: LinearItem): LinearExpression {
        items.add(item);
        return this;
    }
}

/**parse [source] string like `5 x1 + 10 x2 - 1.5 y` to [LinearExpression] object */
fun parseLinearExpression(source: String): LinearExpression {
    val splittedSource = source.replace(" + ", " | ").replace(" - ", " | - ").split(" | ");
    val items = ArrayList<LinearItem>();
    for (itemSource in splittedSource) {
        items.add(parseLinearItem(itemSource));
    }
    return LinearExpression(items);
}
