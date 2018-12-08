package com.github.kopilov.lpdiff

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LinearExpressionTest{
    @Test
    fun linearExpressionTest() {
        assertEquals(
                "{ + 5.38045 x_1_2 + 9.86045 y_1_0 - 4.90045 z_1_1 - 10 }",
                parseLinearExpression("9.86045 y_1_0 - 10 - 4.90045 z_1_1 + 5.38045 x_1_2").toString()
        )
        assertEquals(
                "{ - 1.2 x_4 + 1.8 x_5 + 3 x_6 - 2.6 }",
                parseLinearExpression("5.6 x_4 - 0.5 x_5 + 9.4 x_6 - 4.7 + 2.3 x_5 - 6.4 x_6 - 6.8 x_4 + 2.1").toString()
        )
    }
}
