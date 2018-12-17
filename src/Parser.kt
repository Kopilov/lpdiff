package com.github.kopilov.lpdiff

import java.io.File

enum class LpPart {
    START, OBJECTIVE, CONSTRAINTS, BOUNDS, VARIABLES, END
}

fun parseLpFile(path: String): LinearModel {
    val objectiveBody = StringBuilder();

    fun parseBound(line: String) {

    }

    fun parseConstraints(line: String) {

    }

    fun parseLine(line: String, stage: LpPart) {
        when (stage) {
            LpPart.OBJECTIVE -> objectiveBody.append(line);
            LpPart.CONSTRAINTS -> parseConstraints(line);
            LpPart.BOUNDS -> parseBound(line)
            else -> {} //do not parse variables, ignore begin and end
        }
    }

    val reader = File(path).bufferedReader();
    val lines = reader.lineSequence();
    var stage = LpPart.START;

    for (line in lines) {
        when (line) {
            "Minimize", "Maximize" -> stage = LpPart.OBJECTIVE;
            "Subject To" -> stage = LpPart.CONSTRAINTS;
            "Bounds" -> stage = LpPart.BOUNDS;
            "Binaries", "Generals" -> stage = LpPart.VARIABLES;
            "End" -> stage = LpPart.END;
            else -> parseLine(line, stage);
        }
    }
    assert(LpPart.END.equals(stage));

    return LinearModel(parseLinearFunction(objectiveBody.toString()));
}
