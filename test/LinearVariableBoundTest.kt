package com.github.kopilov.lpdiff

import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals;
import kotlin.test.assertFailsWith

class LinearVariableBoundTest {
    @Test
    fun linearVariableBoundTest() {
        val t = VariableType.CONTINUOUS;
        assertEquals(
                LinearVariableBound("x_1", t, 12.3, null),
                parseBound("  x_1 >= 12.3 ")
        );
        assertEquals(
                LinearVariableBound("x_2", t, 0.0, 13.0),
                parseBound("  x_2 < 13 ")
        );
        assertEquals(
                LinearVariableBound("x_3", t, 1.0, 1.3),
                parseBound("  1 < x_3 <= 1.3 ")
        );
        assertEquals(
                LinearVariableBound("free_x_4", t, null, null),
                parseBound("  free_x_4 free")
        );
        assertEquals(
                LinearVariableBound("x_5", t, 10.0, 10.0),
                parseBound("  x_5 = 10")
        );
        val e1 = assertFailsWith(
                IllegalArgumentException::class
        ) {
            print(parseBound("  x_5 10"));
        };
        assertEquals(e1.message, "No '<', '>', '=' or 'free' found in argument (  x_5 10)");

        val e2 = assertFailsWith(
                IllegalArgumentException::class
        ) {
            print(parseBound("  10 < 15"));
        };
        assertEquals(e2.message, "No variable name found in argument (  10 < 15)");
    }
}
