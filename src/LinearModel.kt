package com.github.kopilov.lpdiff

import java.util.*

data class LinearModel (
        val target: FunctionTarget,
        val objective: LinearFunction,
        val constraints: TreeSet<LinearConstraint>,
        val bounds: TreeSet<LinearVariableBound>
) {
    private val variablesNamesSet = TreeSet<String>();
    fun getVariablesNamesSet(): Collection<String> {
        if (!variablesNamesSet.isEmpty()) {
            return variablesNamesSet;
        }
        for (constraint in constraints) {
            variablesNamesSet.addAll(constraint.getVariablesNamesSet());
        }
        for (bound in bounds) {
            variablesNamesSet.add(bound.name);
        }
        return variablesNamesSet;
    }

    private val constraintsByVariablesGroup = HashMap<Collection<String>, ArrayList<LinearConstraint>>();
    fun groupConstraintsByVariables(): Map<Collection<String>, Collection<LinearConstraint>> {
        if (!constraintsByVariablesGroup.isEmpty()) {
            return constraintsByVariablesGroup;
        }
        for (constraint in constraints) {
            val namesSet = constraint.getVariablesNamesSet();
            val group = if (constraintsByVariablesGroup.containsKey(namesSet)) {
                constraintsByVariablesGroup.get(namesSet);
            } else {
                val newGroup = ArrayList<LinearConstraint>();
                constraintsByVariablesGroup.put(namesSet, newGroup);
                newGroup;
            }
            group?.add(constraint);
        }
        return constraintsByVariablesGroup;
    }

    fun boundsToConstraints(): LinearModel {
        val newConstraints = TreeSet(constraints)
        val newBounds = TreeSet<LinearVariableBound>();
        for (bound in bounds) {
            newBounds.add(LinearVariableBound(bound.name, bound.type, null, null));

            if (bound.upperBound != null && bound.lowerBound != null && bound.upperBound == bound.lowerBound) {
                newConstraints.add(LinearConstraint(
                        "${bound.name}_const_value",
                        LinearFunction(LinearItem(1.0, bound.name)),
                        ConstraintSign.EQUAL,
                        bound.lowerBound
                ));
                continue;
            }

            if (bound.lowerBound != null) {
                newConstraints.add(LinearConstraint(
                        "${bound.name}_low_bound",
                        LinearFunction(LinearItem(1.0, bound.name)),
                        ConstraintSign.MORE,
                        bound.lowerBound
                ));
            }
            if (bound.upperBound != null) {
                newConstraints.add(LinearConstraint(
                        "${bound.name}_up_bound",
                        LinearFunction(LinearItem(1.0, bound.name)),
                        ConstraintSign.LESS,
                        bound.upperBound
                ));
            }
        }
        return LinearModel(target, objective, newConstraints, newBounds);
    }
}
