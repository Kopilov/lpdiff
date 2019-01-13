package com.github.kopilov.lpdiff

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LinearConstraintTest {
    @Test
    fun linearExpressionTest() {
        val linearConstraint = parseLinearConstraint("""
     constraint_0 : x_1 + 2 y_1 - x_4 + x_2
       + x_5 - y_4 + 10 y_5 <= 10 - 20 z
""");
        assertEquals("constraint_0", linearConstraint.name);
        assertEquals(
                "{ + 1 x_1 + 1 x_2 - 1 x_4 + 1 x_5 + 2 y_1 - 1 y_4 + 10 y_5 + 20 z }",
                linearConstraint.leftSide.toString()
        );
        assertEquals(ConstraintSign.LESS, linearConstraint.sign);
        assertEquals(10.0, linearConstraint.rightSide);
    }
}
