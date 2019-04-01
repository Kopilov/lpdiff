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

    var numberOfSimilarConstraints = 0;
    var numberOfUniqueConstraints1 = 0;
    var numberOfUniqueConstraints2 = 0;

    for (varCombination in allVariablesCombinations) {
        outputPrinter.printEmptySpace();
        //step 2, find the most similar coefficients and right side
        outputPrinter.printConstraintHeader(listOf("Model", "Constraint"), varCombination, listOf("Sign", "Right side"));
        val constraintsGroup1 = constraintsGroups1.get(varCombination);
        val constraintsGroup2 = constraintsGroups2.get(varCombination);
        if (constraintsGroup1 != null && constraintsGroup2 != null) {
            val similarConstraintPairs = printSimilarConstraintPairs(varCombination, constraintsGroup1, model1SrcPath, constraintsGroup2, model2SrcPath, outputPrinter);
            val (newSimilarConstraints, newUniqueConstraints1, newUniqueConstraints2) = similarConstraintPairs;
            numberOfSimilarConstraints += newSimilarConstraints;
            numberOfUniqueConstraints1 += newUniqueConstraints1;
            numberOfUniqueConstraints2 += newUniqueConstraints2;
            continue;
        }
        if (constraintsGroup1 != null) {
            printUniqueConstraintGroup(varCombination, constraintsGroup1, model1SrcPath, outputPrinter)
            numberOfUniqueConstraints1 += constraintsGroup1.size;
        }
        if (constraintsGroup2 != null) {
            printUniqueConstraintGroup(varCombination, constraintsGroup2, model2SrcPath, outputPrinter)
            numberOfUniqueConstraints2 += constraintsGroup2.size;
        }
    }

    outputPrinter.printEmptySpace();
    outputPrinter.printVariablesNames(arrayListOf("Number of similar pairs", "$numberOfSimilarConstraints"));
    outputPrinter.printVariablesNames(arrayListOf("Number of unique constraints in model 1", "$numberOfUniqueConstraints1"));
    outputPrinter.printVariablesNames(arrayListOf("Number of unique constraints in model 2", "$numberOfUniqueConstraints2"));
}

fun printUniqueConstraintGroup(varCombination: Collection<String>, constraintsGroup: Collection<LinearConstraint>, modelSrcPath: String, outputPrinter: OutputPrinterTextSeparated) {
    for (constraint1 in constraintsGroup) {
        outputPrinter.printFunctionValuesForNames(constraint1.leftSide.items, varCombination,
                listOf(modelSrcPath, constraint1.name),
                listOf(constraint1.sign.name, constraint1.rightSide.toString())
        );
    }
}

fun printSimilarConstraintPairs(
        varCombination: Collection<String>,
        constraintsGroup1: Collection<LinearConstraint>,
        model1SrcPath: String,
        constraintsGroup2: Collection<LinearConstraint>,
        model2SrcPath: String,
        outputPrinter: OutputPrinterTextSeparated
): Matches {
    val constraintsGroup2Mutable = ArrayList(constraintsGroup2);
    var numberOfSimilarConstraints = 0;
    var numberOfUniqueConstraints1 = 0;
    var numberOfUniqueConstraints2 = 0;
    //todo: optimize full cross join
    for (constraint1 in constraintsGroup1) {
        //print constraint1
        outputPrinter.printFunctionValuesForNames(constraint1.leftSide.items, varCombination,
                listOf(model1SrcPath, constraint1.name),
                listOf(constraint1.sign.name, constraint1.rightSide.toString())
        );
        //try to find a similar pair
        var similarConstraint: LinearConstraint? = null;
        var smallestDifference = -1.0;
        for (constraint2 in constraintsGroup2Mutable) {
//                    println(constraint2);
            val difference = compareConstraints(constraint1, constraint2);
//                    println(difference);
            if (smallestDifference < 0 || difference < smallestDifference) {
                smallestDifference = difference;
                similarConstraint = constraint2;
            }
        }
        if (similarConstraint != null) { //pair is found
            outputPrinter.printFunctionValuesForNames(similarConstraint.leftSide.items, varCombination,
                    listOf(model2SrcPath, similarConstraint.name),
                    listOf(similarConstraint.sign.name, similarConstraint.rightSide.toString())
            );
            constraintsGroup2Mutable.remove(similarConstraint);
            numberOfSimilarConstraints++;
        } else { //pair is not found
            numberOfUniqueConstraints1++;
        }
    }
    for (constraint2 in constraintsGroup2Mutable) {
        //print constraint2 without pairs
        outputPrinter.printFunctionValuesForNames(constraint2.leftSide.items, varCombination,
                listOf(model1SrcPath, constraint2.name),
                listOf(constraint2.sign.name, constraint2.rightSide.toString())
        );
        numberOfUniqueConstraints2++;
    }
    return Matches(numberOfSimilarConstraints, numberOfUniqueConstraints1, numberOfUniqueConstraints2);
}
