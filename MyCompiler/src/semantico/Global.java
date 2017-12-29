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
public class Global {
    private List <Variavel> variaveis;
    private List <Classe> classes;
    private static Global instance;
    
    /**
     * Construtor da classe
     */
    private Global(){
        variaveis = new ArrayList<>();
        classes = new ArrayList<>();
    }
    
    public static Global getInstance(){
        if(instance==null){
            instance = new Global();
        }
        return instance;
    }
    
    /**
     * Adiciona uma variavel caso não exista
     * @param v
     * @return 
     */
    public boolean addVariavel(Variavel v){
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
    public boolean addClasse(Classe c){
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
    public Classe getClasse(String identificador){
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
    public Variavel getVariavel(String identificador){
        for(Variavel v:variaveis){
            if(v.getNome().equals(identificador)){
                return v;
            }
        }
        return null;
    }

    public List<Classe> getClasses() {
        return classes;
    }
}
