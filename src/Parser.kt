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

    val boundsIndex = HashMap<String, LinearVariableBound>();

    fun parseAndCacheBound(line: String) {
        val linearVariableBound = parseBound(line);
        if (boundsIndex.containsKey(linearVariableBound.name)) {
            //upper and lower bounds can be on separate lines
            val oldBound = boundsIndex.get(linearVariableBound.name);
            if (linearVariableBound.lowerBound == null) {
                boundsIndex.put(linearVariableBound.name, LinearVariableBound(
                        linearVariableBound.name,
                        linearVariableBound.type,
                        oldBound?.lowerBound,
                        linearVariableBound.upperBound
                ));
            }
            if (linearVariableBound.upperBound == null) {
                boundsIndex.put(linearVariableBound.name, LinearVariableBound(
                        linearVariableBound.name,
                        linearVariableBound.type,
                        linearVariableBound.lowerBound,
                        oldBound?.upperBound
                ));
            }
        } else {
            boundsIndex.put(linearVariableBound.name, linearVariableBound);
        }
    }

    fun correctVariableType(varName: String, varType: VariableType) {
        if (varType == VariableType.BINARY) { //set 0 and 1 bounds in this case, ignore old values
            boundsIndex.put(varName, LinearVariableBound(varName, VariableType.BINARY, 0.0, 1.0));
            return;
        }
        if (boundsIndex.containsKey(varName)) {
            val oldBound = boundsIndex.get(varName);
            boundsIndex.put(varName, LinearVariableBound(varName, varType, oldBound?.lowerBound, oldBound?.upperBound));
        } else { //set zero and infinite if no info
            boundsIndex.put(varName, LinearVariableBound(varName, varType, 0.0, null));
        }
    }

    fun correctVariablesType(line: String, varType: VariableType) {
        for (varName in line.trim().split(Regex("[\\p{IsWhite_Space}]+"))) {
            correctVariableType(varName, varType);
        }
    }

    fun parseLine(line: String, stage: LpPart, varType: VariableType) {
        when (stage) {
            LpPart.OBJECTIVE -> objectiveBody.append(line);
            LpPart.CONSTRAINTS -> parseConstraints(line, false);
            LpPart.BOUNDS -> parseAndCacheBound(line)
            LpPart.VARIABLES -> correctVariablesType(line, varType);
            else -> {} //ignore begin and end
        }
    }

    val reader = File(path).bufferedReader();
    val lines = reader.lineSequence();
    var stage = LpPart.START;
    var target = FunctionTarget.MINIMIZE;
    var varType = VariableType.CONTINUOUS;

    for (line in lines) {
        when (line.toLowerCase()) {
            "minimize" -> {target = FunctionTarget.MINIMIZE; stage = LpPart.OBJECTIVE};
            "maximize" -> {target = FunctionTarget.MAXIMIZE; stage = LpPart.OBJECTIVE};
            "subject to", "such that", "st", "s.t." -> stage = LpPart.CONSTRAINTS;
            "bounds" -> stage = LpPart.BOUNDS;
            "binary", "binaries", "bin" -> {varType = VariableType.BINARY; stage = LpPart.VARIABLES};
            "general", "generals", "gen" -> {varType = VariableType.GENERAL; stage = LpPart.VARIABLES};
            "semi-continuous", "semis", "semi" -> {varType = VariableType.SEMICONTINUOUS; stage = LpPart.VARIABLES};
            "end" -> stage = LpPart.END;
            else -> parseLine(line, stage, varType);
        }
    }
    parseConstraints("", true);
    assert(LpPart.END.equals(stage));

    return LinearModel(target, parseLinearFunction(objectiveBody.toString()), constraintsParsed, boundsIndex.values);
}
