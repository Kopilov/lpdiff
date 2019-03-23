package com.github.kopilov.lpdiff;


fun main(args: Array<String>) {
//    if (args.size != 2) {
//        println("usage: lpdiff model1.lp model2.lp");
//        return;
//    }

    val model1SrcPath = "resource/model1.lp"; //args[0];
    val model2SrcPath = "resource/model2.lp"//args[1];

    val linearModel1 = parseLpFile(model1SrcPath);
    val linearModel2 = parseLpFile(model2SrcPath);
//    val linearModel1_orig = parseLpFile(model1SrcPath);
//    val linearModel2 = parseLpFile(model2SrcPath);
//
//    val addZeroItem = fun (item: LinearItem): LinearItem {
//        val newName = if (item.name is String) item.name + "_0" else null;
//        return LinearItem(item.coefficient, newName);
//    };
//
//    val addZeroBound = {bound: LinearVariableBound -> LinearVariableBound(bound.name + "_0", bound.type, bound.lowerBound, bound.upperBound)};
//
//    val linearModel1 = convertModelItems(linearModel1_orig, addZeroItem, addZeroBound);

    //step 0: compare objective functions
    val stitchedObjectives = stitchLinearFunctions(linearModel1.objective, linearModel2.objective);
    //step 1: find constraints with the same variables set
    val constraintsGroups1 = linearModel1.groupConstraintsByVariables();
    val constraintsGroups2 = linearModel2.groupConstraintsByVariables();
    val allVariablesCombinations = HashSet<Collection<String>>();
    allVariablesCombinations.addAll(constraintsGroups1.keys);
    allVariablesCombinations.addAll(constraintsGroups2.keys);
    val outputPrinter = OutputPrinterTextSeparated();

    outputPrinter.printFunctionsPair(stitchedObjectives);

    for (varCombination in allVariablesCombinations) {
        //step 2, find the most similar coefficients and right side
        outputPrinter.printConstraintHeader(listOf("Model", "Constraint"), varCombination, listOf("Sign", "Right side"));
        val constraintsGroup1 = constraintsGroups1.get(varCombination);
        val constraintsGroup2 = constraintsGroups2.get(varCombination);
        if (constraintsGroup1 != null && constraintsGroup2 != null) {
            //todo: optimize full cross join
            for (constraint1 in constraintsGroup1) {
                outputPrinter.printFunctionValuesForNames(constraint1.leftSide.items, varCombination,
                        listOf(model1SrcPath, constraint1.name),
                        listOf(constraint1.sign.name, constraint1.rightSide.toString())
                );
                var similarConstraint: LinearConstraint? = null;
                var smallestDifference = -1.0;
                for (constraint2 in constraintsGroup2) {
//                    println(constraint2);
                    val difference = compareConstraints(constraint1, constraint2);
//                    println(difference);
                    if (smallestDifference < 0 || difference < smallestDifference) {
                        smallestDifference = difference;
                        similarConstraint = constraint2;
                    }
                }
                if (similarConstraint != null) {
                    outputPrinter.printFunctionValuesForNames(similarConstraint.leftSide.items, varCombination,
                            listOf(model2SrcPath, similarConstraint.name),
                            listOf(similarConstraint.sign.name, similarConstraint.rightSide.toString())
                    );
                }
            }
            continue;
        }
        if (constraintsGroup1 != null) {
            for (constraint1 in constraintsGroup1) {
                outputPrinter.printFunctionValuesForNames(constraint1.leftSide.items, varCombination,
                        listOf(model1SrcPath, constraint1.name),
                        listOf(constraint1.sign.name, constraint1.rightSide.toString())
                );
            }
        }
        if (constraintsGroup2 != null) {
            for (constraint2 in constraintsGroup2) {
                outputPrinter.printFunctionValuesForNames(constraint2.leftSide.items, varCombination,
                        listOf(model2SrcPath, constraint2.name),
                        listOf(constraint2.sign.name, constraint2.rightSide.toString())
                );
            }
        }
    }
    //
//    val model1Variables = linearModel1.getVariablesNamesSet();
//    outputPrinter.printVariablesNames(model1Variables);
//    for (constraint in linearModel1.constraints) {
//        outputPrinter.printFunctionValuesForNames(constraint.leftSide.items, model1Variables);
//    }
}
