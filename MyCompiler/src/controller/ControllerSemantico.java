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
import semantico.Variavel;

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
        Global g = Global.getInstance();
        
        //Variavel v = g.getClasse("a").getMetodo("metodo").getVariavel("moto");
        
        Variavel v = g.getClasse("a").getMetodo("metodo").getVariaveis().get(0);
        
        System.out.println("name: "+v.getNome());
        //System.out.println("nome: "+v.getNome()+" tipo: "+v.getTipo());
    }
    
}
