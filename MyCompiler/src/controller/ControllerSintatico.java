/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
