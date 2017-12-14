/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emerson
 */
public abstract class Global {
    private static List <Variavel> variaveis = new ArrayList<>();
    private static List <Classe> classes = new ArrayList<>();
    
    /**
     * Adiciona uma variavel caso não exista
     * @param v
     * @return 
     */
    public static boolean addVariavel(Variavel v){
        if(!variaveis.contains(v)){
            variaveis.add(v);
            return true;
        }
        return false;
    }
    
    /**
     * Adiciona uma variavel caso não exista
     * @param c
     * @return 
     */
    public static boolean addMetodo(Classe c){
        if(!classes.contains(c)){
            classes.add(c);
            return true;
        }
        return false;
    }
    
    /**
     * Retorna uma classe caso exista, caso não retorna null
     * @param identificador
     * @return 
     */
    public static  Classe getClasse(String identificador){
        for(Classe c:classes){
            if(c.getNome().equals(identificador)){
                return c;
            }
        }
        return null;
    }
    
    /**
     * Retorna uma variavel caso exista, caso não retorna null
     * @param identificador
     * @return 
     */
    public static Variavel getVariavel(String identificador){
        for(Variavel v:variaveis){
            if(v.getNome().equals(identificador)){
                return v;
            }
        }
        return null;
    }
    
}
