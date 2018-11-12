package com.github.kopilov_ad.lpdiff

import java.io.File

enum class LpPart {
    START, OBJECTIVE, EXPRESSIONS, BOUNDS, VARIABLES, END
}

fun parceLpFile(path: String): LinearModel {
    val objectiveBody = StringBuilder();

    fun parceBound(line: String) {

    }

    fun parceExpressionPart(line: String) {

    }

    fun parceLine(line: String, stage: LpPart) {
        when (stage) {
            LpPart.OBJECTIVE -> objectiveBody.append(line);
            LpPart.EXPRESSIONS -> parceExpressionPart(line);
            LpPart.BOUNDS -> parceBound(line)
            else -> {} //do not parce variables, ignore begin and end
        }
    }

    val reader = File(path).bufferedReader();
    val lines = reader.lineSequence();
    var stage = LpPart.START;

    for (line in lines) {
        when (line) {
            "Minimize", "Maximize" -> stage = LpPart.OBJECTIVE;
            "Subject To" -> stage = LpPart.EXPRESSIONS;
            "Bounds" -> stage = LpPart.BOUNDS;
            "Binaries", "Generals" -> stage = LpPart.VARIABLES;
            "End" -> stage = LpPart.END;
            else -> parceLine(line, stage);
        }
    }
    assert(LpPart.END.equals(stage));
    return LinearModel();
}
