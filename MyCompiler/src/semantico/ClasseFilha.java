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
    
    /**
     * Procura metodo nessa classe ou na classe mãe
     * @param identificador
     * @return null caso não seja encontrado
     */
    @Override
    public Metodo getMetodo(String identificador){
        Metodo m = super.getMetodo(identificador);
        if(m!=null){
            return m;
        }
        return mae.getMetodo(identificador);
    }
    
    /**
     * Procura a variavel nessa classe ou na classe mãe
     * @param identificador
     * @return null caso não seja encontrada
     */
    @Override
    public Variavel getVariavel(String identificador){
        Variavel v = mae.getVariavel(identificador);
        if(v!=null){
            return v;
        }
        return super.getVariavel(identificador);
    }
    
    /**
     * Verifica se a variavel existe na classe mãe ou filha
     * @param v
     * @return 
     */
    @Override
    public boolean contains(Variavel v){
        if(mae.contains(v)){
            return true;
        }
        return super.contains(v);
    }
    
    /**
     * Verifica se o metodo existe nesta classe, caso não procura na classe mãe
     * @param m
     * @return 
     */
    @Override
    public boolean contains(Metodo m){
        if(mae.contains(m)){
            return true;
        }
        return super.contains(m);
    }
}
