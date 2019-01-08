package com.github.kopilov.lpdiff

import java.lang.IllegalArgumentException

enum class VariableType {
    CONTINUOUS, SEMICONTINUOUS, GENERAL, BINARY
}

data class LinearVariableBound (
        val name: String,
        val type: VariableType,
        val lowerBound: Double?, //-inf if null
        val upperBound: Double? //inf if null
) : Comparable<LinearVariableBound> {
    override fun compareTo(other: LinearVariableBound): Int {
        return name.compareTo(other.name);
    }

}

/**
 * Parse [line] like "10 <= a_1 <= 20" to [LinearVariableBound] object.
 * Argument should have variable name, at least one '<' or '>' sign or 'free' token.
 * Type is always set to continuous, it can be changed later.
 */
fun parseBound(line: String): LinearVariableBound {
    val boundSrc = line.trim().replace("<=", "<").replace(">=", ">").replace("==", "=");
    if (boundSrc.indexOf(" free", 0, true) >= 0) {
        val name = boundSrc.replace(" free", "", true).trim();
        return LinearVariableBound(name, VariableType.CONTINUOUS, null, null);
    }
    val splitByEquals = boundSrc.split('=');
    val numberOfEquals = splitByEquals.size - 1;
    val splitByLess = boundSrc.split('<');
    val numberOfLess = splitByLess.size - 1;
    val splitByMore = boundSrc.split('>');
    val numberOfMore = splitByMore.size - 1;
    //one type of sign must be used
    if (numberOfLess == 0 && numberOfMore == 0 && numberOfEquals == 0) {
        throw  IllegalArgumentException("No '<', '>', '=' or 'free' found in argument ($line)");
    }
    if (numberOfEquals > 0) {
        if (numberOfLess > 0 || numberOfMore > 0) {
            throw  IllegalArgumentException("'<' or '>' and '=' are used together in argument ($line)");
        }
        if (numberOfEquals > 1) {
            throw  IllegalArgumentException("bound line ($line) should not have 2 or more '=' signs");
        }
        return parseEquals(splitByEquals, line)
    }
    if (numberOfLess > 0 && numberOfMore > 0) {
        throw  IllegalArgumentException("'<' and '>' are used together in argument ($line)");
    }
    if (numberOfLess == 1) {
        return parseSingleLess(splitByLess, line)
    }
    if (numberOfLess == 2) {
        return parseDoubleLess(splitByLess, line)
    }
    if (numberOfLess > 2) {
        throw  IllegalArgumentException("bound line ($line) should not have 3 or more '<' signs");
    }
    if (numberOfMore == 1) {
        return parseSingleMore(splitByMore, line)
    }
    if (numberOfMore == 2) {
        return parseDoubleMore(splitByMore, line)
    }
    if (numberOfMore > 2) {
        throw  IllegalArgumentException("bound line ($line) should not have 3 or more '>' signs");
    }
    throw RuntimeException("Unreachable statement by design");
}

fun parseEquals(splitByEquals: List<String>, line: String): LinearVariableBound {
    val leftToken = splitByEquals[0].trim();
    val rightToken = splitByEquals[1].trim();
    //determine variable name and bound value
    val leftIsNumber = leftToken.toDoubleOrNull() != null;
    val rightIsNumber = rightToken.toDoubleOrNull() != null;
    if (leftIsNumber && rightIsNumber) {
        throw  IllegalArgumentException("No variable name found in argument ($line)");
    }
    if (!leftIsNumber && !rightIsNumber) {
        throw  IllegalArgumentException("No bound value found in argument ($line)");
    }
    if (leftIsNumber) {//10 = x
        return LinearVariableBound(rightToken, VariableType.CONTINUOUS, rightToken.toDouble(), leftToken.toDouble());
    } else {//x = 10
        return LinearVariableBound(leftToken, VariableType.CONTINUOUS, rightToken.toDouble(), rightToken.toDouble());
    }
}

private fun parseDoubleMore(splitByMore: List<String>, line: String): LinearVariableBound {
    val leftToken = splitByMore[0].trim();
    val middleToken = splitByMore[1].trim();
    val rightToken = splitByMore[2].trim();
    // 20 > x > 10
    if (middleToken.toDoubleOrNull() is Double) {
        throw IllegalArgumentException("No variable name found in argument ($line)");
    }
    if (leftToken.toDoubleOrNull() == null || rightToken.toDoubleOrNull() == null) {
        throw IllegalArgumentException("Illegal bounds values in argument ($line)");
    }
    return LinearVariableBound(middleToken, VariableType.CONTINUOUS, rightToken.toDouble(), leftToken.toDouble());
}

private fun parseSingleMore(splitByMore: List<String>, line: String): LinearVariableBound {
    val leftToken = splitByMore[0].trim();
    val rightToken = splitByMore[1].trim();
    //determine variable name and bound value
    val leftIsNumber = leftToken.toDoubleOrNull() != null;
    val rightIsNumber = rightToken.toDoubleOrNull() != null;
    if (leftIsNumber && rightIsNumber) {
        throw  IllegalArgumentException("No variable name found in argument ($line)");
    }
    if (!leftIsNumber && !rightIsNumber) {
        throw  IllegalArgumentException("No bound value found in argument ($line)");
    }
    if (leftIsNumber) {//10 > x
        return LinearVariableBound(rightToken, VariableType.CONTINUOUS, 0.0, leftToken.toDouble());
    } else {//x > 10
        return LinearVariableBound(leftToken, VariableType.CONTINUOUS, rightToken.toDouble(), null);
    }
}

private fun parseDoubleLess(splitByLess: List<String>, line: String): LinearVariableBound {
    val leftToken = splitByLess[0].trim();
    val middleToken = splitByLess[1].trim();
    val rightToken = splitByLess[2].trim();
    // 10 < x < 20
    if (middleToken.toDoubleOrNull() is Double) {
        throw IllegalArgumentException("No variable name found in argument ($line)");
    }
    if (leftToken.toDoubleOrNull() == null || rightToken.toDoubleOrNull() == null) {
        throw IllegalArgumentException("Illegal bounds values in argument ($line)");
    }
    return LinearVariableBound(middleToken, VariableType.CONTINUOUS, leftToken.toDouble(), rightToken.toDouble());
}

private fun parseSingleLess(splitByLess: List<String>, line: String): LinearVariableBound {
    val leftToken = splitByLess[0].trim();
    val rightToken = splitByLess[1].trim();
    //determine variable name and bound value
    val leftIsNumber = leftToken.toDoubleOrNull() != null;
    val rightIsNumber = rightToken.toDoubleOrNull() != null;
    if (leftIsNumber && rightIsNumber) {
        throw  IllegalArgumentException("No variable name found in argument ($line)");
    }
    if (!leftIsNumber && !rightIsNumber) {
        throw  IllegalArgumentException("No bound value found in argument ($line)");
    }
    if (leftIsNumber) {//10 < x
        return LinearVariableBound(rightToken, VariableType.CONTINUOUS, leftToken.toDouble(), null);
    } else {//x < 10
        return LinearVariableBound(leftToken, VariableType.CONTINUOUS, 0.0, rightToken.toDouble());
    }
}
