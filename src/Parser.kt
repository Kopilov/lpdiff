package com.github.kopilov.lpdiff

import java.io.File

enum class LpPart {
    START, OBJECTIVE, CONSTRAINTS, BOUNDS, VARIABLES, END
}

fun parseLpFile(path: String): LinearModel {
    val objectiveBody = StringBuilder();

    val constraintsParsed = ArrayList<LinearConstraint>();

    val constraintSrc = StringBuilder();
    fun parseConstraints(line: String, flush: Boolean) {
        if (line.indexOf(':') >= 0 || flush) {
            //If line contains ':' delimiter, it is the beginning of new constraint.
            //else the previous still continues
            //Attention: we do not know the last line, have to call this function at the end to flush
            if (constraintSrc.isNotEmpty()) {
                constraintsParsed.add(parseLinearConstraint(constraintSrc.toString()));
                constraintSrc.clear();
            }
        }
        if (!flush) {
            constraintSrc.append(" ").append(line);
        }
    }

    val boundsParsed = ArrayList<LinearBound>();

    fun parseBound(line: String) {

    }

    fun parseLine(line: String, stage: LpPart) {
        when (stage) {
            LpPart.OBJECTIVE -> objectiveBody.append(line);
            LpPart.CONSTRAINTS -> parseConstraints(line, false);
            LpPart.BOUNDS -> parseBound(line)
            else -> {} //do not parse variables, ignore begin and end
        }
    }

    val reader = File(path).bufferedReader();
    val lines = reader.lineSequence();
    var stage = LpPart.START;
    var target = FunctionTarget.MINIMIZE;

    for (line in lines) {
        when (line) {
            "Minimize" -> {target = FunctionTarget.MINIMIZE; stage = LpPart.OBJECTIVE};
            "Maximize" -> {target = FunctionTarget.MAXIMIZE; stage = LpPart.OBJECTIVE};
            "Subject To" -> stage = LpPart.CONSTRAINTS;
            "Bounds" -> stage = LpPart.BOUNDS;
            "Binaries", "Generals" -> stage = LpPart.VARIABLES;
            "End" -> stage = LpPart.END;
            else -> parseLine(line, stage);
        }
    }
    parseConstraints("", true);
    assert(LpPart.END.equals(stage));

    return LinearModel(target, parseLinearFunction(objectiveBody.toString()), constraintsParsed, boundsParsed);
}
