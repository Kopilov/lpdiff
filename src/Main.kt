package com.github.kopilov.lpdiff;


fun main(args: Array<String>) {
//    if (args.size != 2) {
//        println("usage: lpdiff model1.lp model2.lp");
//        return;
//    }

    val model1SrcPath = "resource/model1.lp"; //args[0];
    val model2SrcPath = "resource/model2.lp"//args[1];

    val linearModel1 = parseLpFile(model1SrcPath).boundsToConstraints();
    val linearModel2 = parseLpFile(model2SrcPath).boundsToConstraints();

    //step 0: compare objective functions
    val stitchedObjectives = stitchLinearFunctions(linearModel1.objective, linearModel2.objective);
    //step 1: find constraints with the same variables set
    val constraintsGroups1 = linearModel1.groupConstraintsByVariables();
    val constraintsGroups2 = linearModel2.groupConstraintsByVariables();
    val allVariablesCombinations = HashSet<Collection<String>>();
    allVariablesCombinations.addAll(constraintsGroups1.keys);
    allVariablesCombinations.addAll(constraintsGroups2.keys);
    val outputPrinter = OutputPrinterTextSeparated();
    for (varCombination in allVariablesCombinations) {
        //step 2, todo: find the most similar coefficients and right side
        outputPrinter.printVariablesNames(varCombination);
        val constraintsGroup1 = constraintsGroups1.get(varCombination);
        val constraintsGroup2 = constraintsGroups2.get(varCombination);
        if (constraintsGroup1 != null) {
            for (constraint1 in constraintsGroup1) {
                outputPrinter.printFunctionValuesForNames(constraint1.leftSide.items, varCombination, constraint1.rightSide);
            }
        }
        if (constraintsGroup2 != null) {
            for (constraint2 in constraintsGroup2) {
                outputPrinter.printFunctionValuesForNames(constraint2.leftSide.items, varCombination, constraint2.rightSide);
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
