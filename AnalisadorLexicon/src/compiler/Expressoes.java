/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author emerson
 */
public abstract class Expressoes {
    
    private static List <Pattern> expressoes = new ArrayList<>();
    
    private static void addExpressoes(){
        Pattern palavraReservada = Pattern.compile("class|final|if|else|for|scan|print|int|float|bool|true|false|string");
        expressoes.add(palavraReservada);
        Pattern identificador = Pattern.compile("^[a-z|A-Z]\\w*");
        expressoes.add(identificador);
        String espacos = "(\\x09|\\x0A|\\x0B|\\x20)*";
        Pattern numero = Pattern.compile("-*"+espacos+"\\d*(\\.\\d+)?");
        expressoes.add(numero);
        Pattern operadorAritmetrico = Pattern.compile("\\+|\\-|\\*|/|%");
        expressoes.add(operadorAritmetrico);
        Pattern operadorRelacional = Pattern.compile("!=|=|<|<=|>|>=");
        expressoes.add(operadorRelacional);
        Pattern operadorLogico = Pattern.compile("!|&&|\\|\\|");
        expressoes.add(operadorLogico);
        Pattern delimitador = Pattern.compile(";|,|\\(|\\)|\\[|\\]|\\{|\\}");
        expressoes.add(delimitador);
        Pattern simbolos = Pattern.compile("\\x20|\\x21|\\x23|\\x24|\\x25|\\x26|\\x27|\\x28|\\x29|\\x2A|\\x2B|\\x2C|"
                + "\\x2D|\\x2E|\\x2F|\\x30|\\x31|\\x32|\\x33|\\x34|\\x35|\\x36|\\x37|\\x38|\\x39|\\x3A|\\x3B|\\x3C|\\x3D|\\x3E|\\x3F|"
                + "\\x40|\\x41|\\x42|\\x43|\\x44|\\x45|\\x46|\\x47|\\x48|\\x49|\\x4A|\\x4B|\\x4C|\\x4D|\\x4E|\\x4F"
                + "|\\x50|\\x51|\\x52|\\x53|\\x54|\\x55|\\x56|\\x57|\\x58|\\x59|\\x5A|\\|\\x5B|\\x5C|\\x5D|\\x5E"
                + "|\\x5F|\\x60|\\x61|\\x62|\\x63|\\x64|\\x65|\\x66|\\x67|\\x68|\\x69|\\x6A|\\x6B|\\x6C|\\x6D|\\x6E"
                + "|\\x6F|\\x70|\\x71|\\x72|\\x73|\\x74|\\x75|\\x76|\\x77|\\x78|\\x79|\\x7A|\\x7B|\\x7C|\\x7D|\\x7E");
        expressoes.add(simbolos);
        Pattern espaco = Pattern.compile("\\x09|\\x0A|\\x0B|\\x20");
        expressoes.add(espaco);  
    }
    
    public List <Pattern> getExpressões(){
        addExpressoes();
        return expressoes;
    }
    
    
    
}
