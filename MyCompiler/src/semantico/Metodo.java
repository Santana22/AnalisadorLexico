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
public class Metodo {
    private String tipo;
    private String nome;
    private List <Variavel> variaveis;
    private List <Variavel> parametros;
    
    public Metodo(){
        this.variaveis = new ArrayList<>();
    }
    
    /**
     * Construtor para caso o metodo possua parametros
     * @param tipo
     */
    public Metodo(String tipo) {
        this.tipo = tipo;
        this.variaveis = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }
    
    public Variavel getVariavel(String identificador){
        for(Variavel v:variaveis){
            if(v.getNome().equals(identificador)){
                return v;
            }
        }
        return null;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setParametros(List<Variavel> parametros) {
        this.parametros = parametros;
    }
    
    /**
     * Adiciona uma variavel caso não exista nesse metodo
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

    public List<Variavel> getVariaveis() {
        return variaveis;
    }

    public List<Variavel> getParametros() {
        return parametros;
    }

    /**
     * Verifica se os metodos são iguais: tem o mesmo nome
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Metodo){
            Metodo m = ((Metodo)o);
            if(m.getNome().equals(nome)){ //verifica se os nomes são iguais
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.nome);
        return hash;
    }

  
}
