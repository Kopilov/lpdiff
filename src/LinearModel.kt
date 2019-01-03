package com.github.kopilov.lpdiff

data class LinearModel (
        val target: FunctionTarget,
        val objective: LinearFunction,
        val constraints: Collection<LinearConstraint>,
        val bonds: Collection<LinearVariableBound>
) {
}
