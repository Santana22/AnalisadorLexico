/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import java.util.Objects;

/**
 *
 * @author Emerson
 */
public class Variavel{
    private String tipo;
    private String nome;
    private boolean constante = false;

    public Variavel() {
    }
    
    public Variavel(String tipo, String nome, boolean constante) {
        this.tipo = tipo;
        this.nome = nome;
        this.constante = constante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isConstante() {
        return constante;
    }

    public void setConstante(boolean constante) {
        this.constante = constante;
    }
    
    /**
     * Verifica se a variavel tem o mesmo nome
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Variavel){
            Variavel v = ((Variavel)o);
            if(v.getNome().equals(nome)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.nome);
        return hash;
    }


    
}
