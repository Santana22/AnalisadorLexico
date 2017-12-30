/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import compiler.ASemanticoParser1;
import compiler.ASemanticoParser2;
import compiler.Token;
import java.io.File;
import java.util.ArrayList;
import semantico.Classe;
import semantico.Global;
import semantico.Variavel;

/**
 *
 * @author Emerson
 */
public class ControllerSemantico {
    private ASemanticoParser1 analisadorSemanticop1;
    private ASemanticoParser2 analisadorSemanticop2;
    
    public ControllerSemantico(){
        analisadorSemanticop1=new ASemanticoParser1();
        analisadorSemanticop2=new ASemanticoParser2();
    }
    
    public void iniciar(ArrayList <Token> tokens, File file){
        this.analisadorSemanticop1.iniciar(tokens, file);
        this.analisadorSemanticop2.iniciar(tokens, file);
    }
}
