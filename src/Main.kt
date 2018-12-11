package com.github.kopilov.lpdiff;


fun main(args: Array<String>) {
//    if (args.size != 2) {
//        println("usage: lpdiff model1.lp model2.lp");
//        return;
//    }

    val model1SrcPath = "resource/model1.lp"; //args[0];
    val model2SrcPath = "resource/model2.lp"//args[1];

    val linearModel1 = parseLpFile(model1SrcPath);
    print(linearModel1);
    val linearModel2 = parseLpFile(model2SrcPath);
    print(linearModel2);
}
