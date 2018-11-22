package com.github.kopilov.lpdiff;
/**
 * Linear programming expression containing sum of items
 */
class LinearExpression(items: Collection<LinearItem>) {
    val items = items;

    fun append(item: LinearItem): LinearExpression {
        val newItems = ArrayList<LinearItem>(items);
        newItems.add(item);
        return LinearExpression(newItems);
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
