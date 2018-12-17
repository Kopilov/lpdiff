package com.github.kopilov.lpdiff

class LinearModel(val objective: LinearFunction) {
    override fun toString(): String {
        return objective.toString();
    }
}
