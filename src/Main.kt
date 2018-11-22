package com.github.kopilov.lpdiff;

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("usage: lpdiff model1.lp model2.lp");
        return;
    }

    val model1 = args[0];
    val model2 = args[1];


}
