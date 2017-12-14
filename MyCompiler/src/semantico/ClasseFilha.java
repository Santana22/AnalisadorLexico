/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

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
    public Metodo getMetodo(String identificador){
        Metodo m = super.getMetodo(identificador);
        if(m!=null){
            return m;
        }
        return mae.getMetodo(identificador);
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
