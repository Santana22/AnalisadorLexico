/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import compiler.ALexico;
import java.io.File;

/**
 *
 * @author Santana
 */
public class ControllerLexico {
    private ALexico analisador_lexico;
    
    public ControllerLexico(){
        this.analisador_lexico = new ALexico();
    }
    
    public void iniciarLexico(File arquivo){
        this.analisador_lexico.init(arquivo);
    }
}
