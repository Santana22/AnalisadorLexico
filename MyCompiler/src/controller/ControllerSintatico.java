package controller;

import compiler.ASintatico;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author vinicius
 */

public class ControllerSintatico {
    
    private ASintatico analisadorSintatico;

    public ControllerSintatico() {
        this.analisadorSintatico = new ASintatico();
    }
    
    public void iniciarSintatico(ArrayList tokens, File file){
        this.analisadorSintatico.iniciar(tokens, file);
    }  
}
