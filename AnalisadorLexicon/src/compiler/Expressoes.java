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
        Pattern cadeiaCaracteres = Pattern.compile("\"[\\x20-\\x21\\x23-\\x7E]*\"");
        expressoes.add(cadeiaCaracteres);
        Pattern espaco = Pattern.compile("\\x09|\\x0A|\\x0B|\\x20");
        expressoes.add(espaco);  
    }
    
    public List <Pattern> getExpressões(){
        addExpressoes();
        return expressoes;
    }
    
    
    
}
