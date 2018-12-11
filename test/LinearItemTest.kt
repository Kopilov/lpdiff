package com.github.kopilov.lpdiff;

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals;

class LinearItemTest {
    @Test
    fun linearItemTest() {
        assertEquals(parseLinearItem("13.5 x_1"), LinearItem(13.5, "x_1"));
        assertEquals(parseLinearItem("10.4e2    s2"), LinearItem(1040.0, "s2"));
        assertEquals(parseLinearItem("- 35 f"), LinearItem(-35.0, "f"));
        assertEquals(parseLinearItem("+ 0.8 i_1"), LinearItem(0.8, "i_1"));
        assertEquals(parseLinearItem("-47e-3 abc"), LinearItem(-0.047, "abc"));
        assertEquals(parseLinearItem("123,4"), LinearItem(123.4, null));
        assertEquals(parseLinearItem("+973.6"), LinearItem(9736E-1, null));
        assertEquals(parseLinearItem("74a6"), LinearItem(1.0, "74a6"));
    }
}
