package controller;

import compiler.ALexico;
import java.io.File;

/**
 * Controller para o Analisador Léxico
 * @author Emerson e Santana
 */

public class ControllerLexico {
    private ALexico analisadorLexico;
    
    /**
     * Construtor da classe.
     */
    
    public ControllerLexico(){
        this.analisadorLexico = new ALexico();
    }
    
    /**
     * Método que inicializa o analisador léxico
     * @param arquivo 
     */
    
    public void iniciarLexico(File arquivo){
        this.analisadorLexico.iniciar(arquivo);
    }
}
