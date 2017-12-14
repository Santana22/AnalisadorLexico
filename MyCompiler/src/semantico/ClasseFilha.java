/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import java.util.List;

/**
 *
 * @author Emerson
 */
public class ClasseFilha extends Classe{
    private final Classe mae;
    
    public ClasseFilha(String nome, Classe mae) {
        super(nome);
        this.mae = mae;
    }
    
    @Override
    public boolean addVariavel(Variavel v){
        if(mae.contains(v)){
            return false;
        }
        return super.addVariavel(v);
    }
    
    @Override
    public boolean addMetodo(Metodo m){
        if(mae.contains(m)){
            return false;
        }
        return super.addMetodo(m);
    }
    
    @Override
    public List<Metodo> getMetodos(String identificador){
        List <Metodo> m = mae.getMetodos(identificador);
        m.addAll(super.getMetodos(identificador));
        return m;
    }
    
    @Override
    public List<Metodo> getMetodos(String identificador, String tipo){
        List <Metodo> m = mae.getMetodos(identificador, tipo);
        m.addAll(super.getMetodos(identificador, tipo));
        return m;
    }
    
    @Override
    public Variavel getVariavel(String identificador){
        Variavel v = mae.getVariavel(identificador);
        if(v!=null){
            return v;
        }
        return super.getVariavel(identificador);
    }
    
    @Override
    public boolean contains(Variavel v){
        if(mae.contains(v)){
            return true;
        }
        return super.contains(v);
    }
    
    @Override
    public boolean contains(Metodo m){
        if(mae.contains(m)){
            return true;
        }
        return super.contains(m);
    }
}
