package main;

import controller.Facade;
import java.io.*;

/**
 * Classe responsável por executar o compilador.
 *
 * @author Emerson e Vinicius
 */

public class Exec {
    private static Facade facade = Facade.getInstance();

    /**
     * Método que obtem do diretório "entrada" os arquivos fontes. 
     * A saída está na mesma pasta com o prefixo "output_"
     */
    
    public static void main(String[] args) {
        File listaArquivos = new File("entrada");
        File[] arquivos = listaArquivos.listFiles();

        for (File file : arquivos) {
            if(!file.isDirectory() && !file.getName().contains("output_"))
                facade.analisadorLexico(file);
        }   
    }
}
