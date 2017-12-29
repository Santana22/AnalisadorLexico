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
    private Classe mae;
    private String nomeMae;
    
    public ClasseFilha(String nome, String nomeMae) {
        super(nome);
        this.nomeMae = nomeMae;
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

    public Classe getMae() {
        return mae;
    }

    public void setMae(Classe mae) {
        this.mae = mae;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }
    
    /**
     * Procura a variavel nessa classe ou na classe mãe
     * @param identificador
     * @return null caso não seja encontrada
     */
    @Override
    public Variavel getVariavel(String identificador){
        Variavel v = super.getVariavel(identificador);
        if(v!=null){
            return v;
        }
        return mae.getVariavel(identificador);
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
