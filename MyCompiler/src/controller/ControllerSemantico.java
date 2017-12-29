/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import compiler.ASemanticoParser1;
import compiler.Token;
import java.io.File;
import java.util.ArrayList;
import semantico.Classe;
import semantico.Global;

/**
 *
 * @author Emerson
 */
public class ControllerSemantico {
    private ASemanticoParser1 analisadorSemanticop1;
    
    public ControllerSemantico(){
        analisadorSemanticop1=new ASemanticoParser1();
    }
    
    public void iniciar(ArrayList <Token> tokens, File file){
        this.analisadorSemanticop1.iniciar(tokens, file);
        Classe c = Global.getClasse("a");
        
        System.out.println(c.getNome());
    }
    
}
