/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import compiler.ASintatico;
import java.util.ArrayList;

/**
 *
 * @author vinicius
 */

public class ControllerSintatico {
    
    private ASintatico analisadorSintatico;

    public ControllerSintatico() {
        this.analisadorSintatico = analisadorSintatico;
    }
    
    public void iniciarSintatico(ArrayList tokens){
        this.analisadorSintatico.iniciar(tokens);
    }  
}
