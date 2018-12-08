package com.github.kopilov.lpdiff;

import java.text.DecimalFormat

fun main(args: Array<String>) {
//    if (args.size != 2) {
//        println("usage: lpdiff model1.lp model2.lp");
//        return;
//    }
//
//    val model1 = args[0];
//    val model2 = args[1];
//
    val f = "%f";
    val ff = DecimalFormat("#.#####");
    val v1 = 2.34;
    println(v1);
    println(f.format(v1));
    println(ff.format(v1));
    val v2 = 2.3456789;
    println(v2);
    println(f.format(v2));
    println(ff.format(v2));
}
