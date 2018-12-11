package com.github.kopilov.lpdiff

class LinearModel(val objective: LinearExpression) {
    override fun toString(): String {
        return objective.toString();
    }
}
