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
