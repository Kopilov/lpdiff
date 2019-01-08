package com.github.kopilov.lpdiff

import java.util.*

data class LinearModel (
        val target: FunctionTarget,
        val objective: LinearFunction,
        val constraints: TreeSet<LinearConstraint>,
        val bonds: TreeSet<LinearVariableBound>
) {
}
