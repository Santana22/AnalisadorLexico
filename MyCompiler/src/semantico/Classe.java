/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Emerson
 */
public class Classe {
    private String nome;
    private List <Variavel> variaveis;
    private List <Metodo> metodos;

    public Classe(String nome) {
        this.nome = nome;
        variaveis = new ArrayList<>();
        metodos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List getVariaveis() {
        return variaveis;
    }

    public List<Metodo> getMetodos() {
        return metodos;
    }

    public void setVariaveis(List<Variavel> variaveis) {
        this.variaveis = variaveis;
    }

    public void setMetodos(List<Metodo> metodos) {
        this.metodos = metodos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.nome);
        return hash;
    }
    
    /**
     * Verifica se a classe possui o mesmo nome.
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Classe){
            Classe c = (Classe)o;
            if(c.getNome().equals(nome)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adiciona um metodo caso não exista
     * @param m
     * @return 
     */
    public boolean addMetodo(Metodo m){
        if(!metodos.contains(m)){
            metodos.add(m);
            return true;
        }
        return false;
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
     * Retorna o metodo caso seja encontrado, caso contrario retorna null
     * @param identificador
     * @return 
     */
    public Metodo getMetodo(String identificador){
        for(Metodo m:metodos){
            if(m.getNome().equals(identificador)){
                return m;
            }
        }
        return null;
    }
    
    /**
     * Retorna  a variavel caso seja encontrada, caso contrario retorna null
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
    
    /**
     * Verifica se a lista tem aquela variavel
     * @param v
     * @return 
     */
    public boolean contains(Variavel v){
        return variaveis.contains(v);
    }
    
    /**
     * Verifica se aquele metodo existe
     * @param m
     * @return 
     */
    public boolean contains(Metodo m){
        return metodos.contains(m);
    }
}
