package com.github.kopilov.lpdiff;

import java.util.*

/**
 * Linear programming expression containing sum of items
 */
class LinearExpression constructor() {
    val items = TreeSet<LinearItem>();
    val index = HashMap<String?, LinearItem>();

    constructor(items: Collection<LinearItem>): this() {
        for (item in items) {
            append(item);
        }
    }

    constructor(item: LinearItem): this() {
        append(item);
    }

    fun append(item: LinearItem): LinearExpression {
        if (index.containsKey(item.name)) {
            val extractingItem = index.remove(item.name);
            if (extractingItem == null || extractingItem.name != item.name) {
                throw Exception("non-consistent LinearExpression");
            }
            items.remove(extractingItem);
            val newItem = LinearItem(item.coefficient + extractingItem.coefficient, item.name);
            items.add(newItem);
            index.put(item.name, newItem);
        } else {
            items.add(item);
            index.put(item.name, item);
        }
        return this;
    }

    fun format(locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
        val result = StringBuilder();
        for (item in items) {
            result.append(item.format(locale)).append(" ");
        }
        return result.toString();
    }

    override fun toString(): String {
        val result = StringBuilder("{ ");
        result.append(format(Locale.ROOT));
        return result.append("}").toString();
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
