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
    
    /**
     * Construtor para o caso do metodo ser void
     * @param tipo
     * @param nome 
     */
    public Metodo(String tipo, String nome) {
        this.tipo = tipo;
        this.nome = nome;
        this.variaveis = new ArrayList<>();
        this.parametros = new ArrayList<>();
    }

    /**
     * Construtor para caso o metodo possua parametros
     * @param tipo
     * @param nome
     * @param parametros 
     */
    public Metodo(String tipo, String nome, List<Variavel> parametros) {
        this.tipo = tipo;
        this.nome = nome;
        this.variaveis = new ArrayList<>();
        this.parametros = parametros;
    }

    public String getNome() {
        return nome;
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
    
    /**
     * Adiciona uma variavel caso não exista nesse metodos
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
     * Verifica se aquela variavel foi declarada
     * @param identificador
     * @return 
     */
    public boolean contains(String identificador){
        for(Variavel v:variaveis){
            if(v.getNome().equals(identificador)){
                return true;
            }
        }
        return false;
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
            if(m.getNome().equals(nome)){ //verifica se o retorno e o nome são iguais
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.tipo);
        hash = 79 * hash + Objects.hashCode(this.nome);
        hash = 79 * hash + Objects.hashCode(this.parametros);
        return hash;
    }
}
