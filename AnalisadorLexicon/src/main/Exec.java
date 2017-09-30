/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controller.Facade;
import java.io.*;

/**
 * Classe responsável por executar o compilador.
 *
 * @author Emerson e Vinicius
 */

public class Exec {
    private static Facade facade = new Facade();

    public static void main(String[] args) {
        File listaArquivos = new File("entrada");
        File[] arquivos = listaArquivos.listFiles();

        for (File file : arquivos) {
            facade.analisadorLexico(file);
        }
    }
}
