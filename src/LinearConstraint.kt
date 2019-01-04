package com.github.kopilov.lpdiff

/**
 * Linear programming named constraint (equality or inequality) contains sign (more, equal, or less),
 * [LinearFunction] in left side and constant in right side.
 */
data class LinearConstraint constructor(
        val name: String,
        val leftSide: LinearFunction,
        val sign: ConstraintSign,
        val rightSide: Double
) {

}

enum class ConstraintSign {
    MORE, LESS, EQUAL
}

/** Put all [LinearItem]s that have variables to left side and all constants to right side */
fun normalizeLinearConstraint(name: String, leftSide: LinearFunction, sign: ConstraintSign, rightSide: LinearFunction): LinearConstraint {
    val normalizedLeftSide = LinearFunction();
    var normalizedRightSide = 0.0;

    for (leftItem in leftSide.items) {
        if (leftItem.name == null) {
            normalizedRightSide -= leftItem.coefficient;
        } else {
            normalizedLeftSide.append(LinearItem(leftItem.coefficient, leftItem.name));
        }
    }

    for (rightItem in rightSide.items) {
        if (rightItem.name == null) {
            normalizedRightSide += rightItem.coefficient;
        } else {
            normalizedLeftSide.append(LinearItem(-rightItem.coefficient, rightItem.name));
        }
    }

    return LinearConstraint(name, normalizedLeftSide, sign, normalizedRightSide);
}

/** Parse [source] string like `less_then_hundred: 10 x + 20 y < 100` to [LinearConstraint] object*/
fun parseLinearConstraint(source: String): LinearConstraint {
    var sourceParsing = source;

    val nameAndBody= sourceParsing.split(":");
    val name = nameAndBody.first().trim();
    sourceParsing = nameAndBody.last();

    /**
     * Assert that we have exactly one "<", "<=", "=", "==", ">" or ">=" sign in [sourceParsing] variable value.
     * Replace it with '|' delimiter and return corresponding [ConstraintSign].
     */
    fun parseAndValidateSign(): ConstraintSign {
        if (sourceParsing.count{c: Char -> c == '|'} > 0) {
            throw IllegalArgumentException("Source has unexpected char, '|'");
        }

        val lessSignCount = sourceParsing.count{c: Char -> c == '<'};
        sourceParsing = sourceParsing.replace("<=", "|").replace("=<", "|").replace("<", "|");
        val moreSignCount = sourceParsing.count{c: Char -> c == '>'};
        sourceParsing = sourceParsing.replace(">=", "|").replace("=>", "|").replace(">", "|");
        val equalSignCount = sourceParsing.count{c: Char -> c == '='};
        sourceParsing = sourceParsing.replace("==", "|").replace("=", "|");
        val splitterCount = sourceParsing.count{c: Char -> c == '|'};

        if (splitterCount == 0) {
            throw IllegalArgumentException("Constraint signs ('<', '=' or '>') not found in source");
        }

        if (splitterCount > 1) {
            throw IllegalArgumentException("Source has too many constraint signs ('<', '=' or '>')");
        }

        if (lessSignCount > 0) {
            return ConstraintSign.LESS;
        }
        if (moreSignCount > 0) {
            return ConstraintSign.MORE;
        }
        assert(equalSignCount > 0);
        return ConstraintSign.EQUAL;
    }

    val sign = parseAndValidateSign();
    val leftAndRight = sourceParsing.split("|");
    val leftSrc = leftAndRight.first();
    val rightSrc = leftAndRight.last();
    val left = parseLinearFunction(leftSrc);
    val right = parseLinearFunction(rightSrc);
    return normalizeLinearConstraint(name, left, sign, right);
}
