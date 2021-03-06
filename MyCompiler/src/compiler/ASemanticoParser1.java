package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import semantico.Classe;
import semantico.ClasseFilha;
import semantico.Global;
import semantico.Metodo;
import semantico.Variavel;

/**
 * Esta classe modela a 1ª análise semântica para a Linguagem Jussara.
 *
 * @author Emerson e Vinicius
 */
public class ASemanticoParser1 {
    
    private Token tokenAtual, tokenAnterior, tokenAnteriorAnterior;
    private ArrayList<Token> tokens;
    private boolean umaClasse = false;
    private int umaMain = 0;
    private BufferedWriter saidaSematico;
    private int errosSemanticos = 0;
    private int posicao = -1;
    private Classe classeAtual = null;
    private Metodo metodoAtual = null;
    private Variavel variavelAtual = null;
    private Variavel parametroAtual = null;
    private List <Variavel> parametrosAtuais;
    private Global global = Global.getInstance();
    
    private boolean proximoToken() {
        if (posicao + 1 < tokens.size()) {
            posicao++;
            tokenAnteriorAnterior = tokenAnterior;
            tokenAnterior = tokenAtual;
            tokenAtual = tokens.get(posicao);
            aceitarToken("Comentário de Linha"); // Pulando comentarios
            aceitarToken("Comentário de Bloco");
            return true;
        }
        return false;
    }

    private boolean aceitarToken(String tipo) {
        if (tokenAtual.getTipo().equals(tipo) || tokenAtual.getNome().equals(tipo)) {
            System.out.println(tokenAtual);
            proximoToken();
            return true;
        }
        return false;
    }
    
    /**
     * Método que inicia a 1ª análise semântica.
     *
     * @param tokens lista de tokens extraídos da análise léxica
     * @param file diretório para armazenar os resultados
     */

    public void iniciar(ArrayList <Token> tokens, File file){
        
        FileWriter output;
        try {
            output = new FileWriter(new File(file.getParent(), "output_sen_" + file.getName()));
            saidaSematico = new BufferedWriter(output);
            saidaSematico.write("1ª Análise Semântica iniciada para o arquivo " + file.getName());
            saidaSematico.newLine();
            System.out.println("1ª Análise Semântica iniciada para o arquivo " + file.getName());
            this.tokens = tokens;
            inicio();
            verificarHeranca();
            if (errosSemanticos == 0 && umaClasse && umaMain == 1) {
                System.out.println("1ª Análise Semântica finalizada com sucesso para o arquivo " + file.getName());
                saidaSematico.write("1ª Análise Semântica finalizada com sucesso para o arquivo " + file.getName());
            } else{
                if (!umaClasse) {
                    saidaSematico.write("Erro Grave: Deve existir, pelo menos, uma classe.");
                    saidaSematico.newLine();
                }
                if(umaMain != 1){
                    saidaSematico.write("Erro Grave: Deve existir somente um método main no arquivo.");
                    saidaSematico.newLine();
                }
                System.out.println("1ª Análise Semântica finalizada com erro para o arquivo " + file.getName());
                saidaSematico.write("1ª Análise Semântica finalizada com erro para o arquivo " + file.getName());
            }
            saidaSematico.close();

        } catch (IOException ex) {
            Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void inicio() {
        proximoToken();
        variavelConstanteObjeto();
        classe();
    }

    private void variavelConstanteObjeto() {
        variavelAtual = new Variavel();
        if (aceitarToken("final")) {
            variavelAtual.setConstante(true);
            tipo();
            tratamentoConstante();
            if (aceitarToken(";")) {
                addVariavel();
                variavelConstanteObjeto();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("Identificador")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("Identificador")) {
                variavelAtual.setNome(tokenAnterior.getNome());
                criarObjetos();
            } else if (aceitarToken("=")) {
                instancia();
            } else {
                chamadaMetodo();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            tratamentoVariavel();
        } else {
        }
        variavelAtual = new Variavel();
    }

    private void classe() {
        if (aceitarToken("class")) {
            if(!umaClasse){
                umaClasse = true;
            }
            if (aceitarToken("Identificador")) {
                classeAtual = new Classe(tokenAnterior.getNome());
                herancaNao();
                if (aceitarToken("{")) {
                    variavelConstanteObjeto();
                    metodo();
                    if (aceitarToken("}")) {
                        global.addClasse(classeAtual);
                        classe();
                    }
                } 
            }
        }
    }

    private boolean tipo() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            return true;
        }
        return false;
    }
    
    private void tratamentoConstante() {
        if (aceitarToken("Identificador")) {
            variavelAtual.setNome(tokenAnterior.getNome());
            if (aceitarToken("=")) {
                if (aceitarToken("Número")) {
                    geradorConstante();
                } 
            } 
        } 
    }

    private void tratamentoVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            addVariavel();
            variavelConstanteObjeto();
        }
    }

    private void geradorConstante() {
        if (aceitarToken(",")) {
            if(!global.addVariavel(variavelAtual)){
                //constante já existente exception
                 salvarMensagemArquivo("Constante já existente com esse nome. Linha: " + + tokenAtual.getLinha());
            }
            tratamentoConstante();
        }
    }

    private void herancaNao() {
        if (aceitarToken("<")) {
            if (aceitarToken("-")) {
                if (aceitarToken(">")) {
                    if (aceitarToken("Identificador")) {
                        classeAtual = new ClasseFilha(classeAtual.getNome(), tokenAnterior.getNome());
                    } 
                } 
            }
        }
    }

    private void variaveis() {
        if (aceitarToken("Identificador")) {
            variavelAtual.setNome(tokenAnterior.getNome());
            fatoracaoVariaveis();
        }
    }

    private void fatoracaoVariaveis() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                verificarTamanhoVetor();
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                }
            } 
        }
        acrescentar();
    }

    private void acrescentar() {
        if (aceitarToken(",")) {
            addVariavel();
            variaveis();
        }
    }

    private void fatoracaoFatoracaoVariaveis() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                verificarTamanhoVetor();
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                }
            }
        }
        acrescentar();
    }

    private void program() {
        if (aceitarToken("for")) {
            forConsumido();
        } else if (aceitarToken("if")) {
            ifConsumido();
        } else if (aceitarToken("scan")) {
            scanConsumido();

        } else if (aceitarToken("print")) {
            printConsumido();
        } else if (aceitarToken("<")) {
            if (aceitarToken("<")) {
                returnConsumido();
            }
        } else if (tipo()) {
            variavelAtual=new Variavel();
            variavelAtual.setNome(tokenAnterior.getNome());
            criarVariavel();
        } else if (aceitarToken("-")) {
            classificarVariavel();
        } else if (aceitarToken("Identificador")) {
            variavelAtual = new Variavel();
            variavelAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("=")) {
                if (tokenAtual.getNome().equals(">")) {
                    instancia();
                } else {
                    operation();
                    if (aceitarToken(";")) {

                    }
                }
            } else if (aceitarToken("Identificador")) {
                variavelAtual.setNome(tokenAnterior.getNome());
                criarObjetos();
            } else if (tokenAtual.getNome().equals("[")) {
                fatoracaoAcessoVetorMatriz();
                if (aceitarToken("=")) {
                    operation();
                    if (aceitarToken(";")) {

                    }
                }
            } else {
                chamadaMetodo();
            }
            program();
        }
    }

    private void returnConsumido() {
        tiposReturn();
        if (aceitarToken(";")) {
            program();
        } 
    }

    private void tiposReturn() {
        operation();
    }

    private void metodo() { 
        if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                metodoAtual = new Metodo();
                parametrosAtuais = new ArrayList();
                parametroAtual = new Variavel();
                comSemRetorno();
            }
        }
    }

    private void comSemRetorno() {
        if (aceitarToken("Identificador")) {
            metodoAtual.setNome(tokenAnterior.getNome());
            if (aceitarToken("(")) {
                parametros();
                if (aceitarToken(")")) {
                    metodoAtual.setParametros(parametrosAtuais);
                    if (aceitarToken("{")) {
                        program();
                        if (aceitarToken("}")) {
                            addMetodo();
                            variavelConstanteObjeto();
                            metodo();
                        }
                    }
                }
            } 
        } else if (aceitarToken("bool")) {
            metodoAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("main")) {
                metodoAtual.setNome(tokenAnterior.getNome());
                umaMain++;
                main();
            } else if (aceitarToken("Identificador")) {
                metodoAtual.setNome(tokenAnterior.getNome());
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        metodoAtual.setParametros(parametrosAtuais);
                        parametrosAtuais = null;
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                addMetodo();
                                metodo();
                            }
                        }
                    }
                }
            } 
        } else if (tipo()) {
            metodoAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("Identificador")) {
                metodoAtual.setNome(tokenAnterior.getNome());
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        metodoAtual.setParametros(parametrosAtuais);
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                addMetodo();
                                metodo();
                            }
                        } 
                    } 
                } 
            } 
        }
    }

    private void main() {
        if (aceitarToken("(")) {
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        metodo();
                    } 
                } 
            } 
        } 
    }

    private void parametros() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool") || aceitarToken("Identificador")) {
            parametroAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("Identificador")) {
                parametroAtual.setNome(tokenAnterior.getNome());
                acrescentarParametros();
            } 
        }
    }

    private void acrescentarParametros() {
        if (aceitarToken(",")) {
            addParametro();
            parametros();
        }
    }

    private void forConsumido() {
          if (aceitarToken("(")) {
            operationFor();
            if (aceitarToken(";")) {
                exLogicRelational(0);
                if (aceitarToken(";")) {
                    operationFor();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                program();
                            }
                        }
                    }
                }
            } 
        }
    }

    private void ifConsumido() {
        if (aceitarToken("(")) {
            exLogicRelational(0);
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        if (aceitarToken("else")) {
                            elseConsumido();
                        }
                        program();
                    }
                }
            } 
        }

    }

    private void elseConsumido() {
        if (aceitarToken("{")) {
            program();
            if (aceitarToken("}")) {
            }
        }
    }

    private void scanConsumido() {
        if (aceitarToken("(")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
                if (aceitarToken(")")) {
                    if (aceitarToken(";")) {
                        program();
                    }
                } 
            }
        } 
    }

    private void multiplasLeituras() {
        if (aceitarToken(",")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
            } 
        }
    }

    private void printConsumido() {
        if (aceitarToken("(")) {
            impressao();
            multiplasImpressoes();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                    program();
                } 
            } 
        } 
    }

    private void impressao() {
        operation();
    }

    private void multiplasImpressoes() {
        if (aceitarToken(",")) {
            impressao();
            multiplasImpressoes();
        }
    }

    private void classificarVariavel() {
        if (aceitarToken("-")) {
            if (aceitarToken(">")) {
                operationLine();
            }
        } else if (aceitarToken(">")) {
            operationLine();
        }
    }

    private void operationFor() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                operation();
            } 
        } else {
            acessoVetorMatriz();
            if (aceitarToken("=")) {
                operation();
            }
        }
    }

    private void operation() {
        selectOperation(0);
    }

    private void selectOperation(int parenteses) {
        if (aceitarToken("(")) {
            selectOperation(++parenteses);
        } else {
            value();
            if (aceitarToken("Operador Aritmético")) {
                exAritmeticas(parenteses);
            } else if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                exLogicRelational(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {

            }
        }
    }

    private int exAritmeticas(int parenteses) {
        if (aceitarToken("(")) {
            parenteses = exAritmeticas(++parenteses);
        } else {
            value();
            if (aceitarToken("Operador Aritmético")) {
                parenteses = exAritmeticas(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {
                do {
                    parenteses--;
                } while (parenteses > 0 && aceitarToken(")"));
                if (aceitarToken("Operador Aritmético")) {
                    parenteses = exAritmeticas(parenteses);
                } else {
                    return parenteses;
                }
            } else {
                return parenteses;
            }
        }
        return parenteses;
    }

    private void exLogicRelational(int parenteses) {
        if (aceitarToken("(")) {
            exLogicRelational(++parenteses);
        } else {
            parenteses = exAritmeticas(parenteses);
            if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                exLogicRelational(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {
                do {
                    parenteses--;
                } while (parenteses > 0 && aceitarToken(")"));
                if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                    exLogicRelational(parenteses);
                }
            }
        }
    }

    
    private void operationLine() {
        operationFor();
        if (aceitarToken(";")) {
            program();
        } 
    }

    private void acessoVetorMatriz() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("[")) {
                if (aceitarToken("Número")) {
                    if (aceitarToken("]")) {
                        fatoracaoAcessoVetorMatriz();
                    }
                } 
            }
        }
    }

    private void value() {
        if (aceitarToken("Identificador")) {
            if (tokenAtual.getNome().equals("(") || tokenAtual.getNome().equals(":")) {
                chamadaMetodo();
            } else if (tokenAtual.getNome().equals("[")) {
                fatoracaoAcessoVetorMatriz();
            }
        } else if (aceitarToken("Número") || aceitarToken("Cadeia de Caracteres") || aceitarToken("true") || aceitarToken("false")) {
        }
    }

    private void fatoracaoAcessoVetorMatriz() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoAcessoVetorMatriz();
                } 
            }
        }
    }

    private void criarVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            addVariavel();
            program();
        } 
    }

    private void instancia() {
        if (aceitarToken(">")) {
            if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    if (aceitarToken(")")) {
                        if (aceitarToken(";")) {
                        }
                    } else {
                        passagemParametros();
                        if (aceitarToken(")") && aceitarToken(";")) {
                        } 
                    }
                }
            }
        }
    }

    private void passagemParametros() {
        operation();
        if (aceitarToken(",")) {
            passagemParametros();
        }
    }

    private void criarObjetos() {
        if (aceitarToken(",")) {
            addVariavel();
            if (aceitarToken("Identificador")) {
                criarObjetos();
            }
        } else if (aceitarToken(";")) {
            addVariavel();
        }
    }

    private void chamadaMetodo() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken(":")) {
                if (aceitarToken(":")) {
                    if (aceitarToken("Identificador")) {
                        if (aceitarToken("(")) {
                            fatoracaoChamadaMetodo();
                        } else if (aceitarToken(";")) {
                        } 
                    }
                }
            }
        } else if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                if (aceitarToken("Identificador")) {
                    if (aceitarToken("(")) {
                        fatoracaoChamadaMetodo();
                    } else if (aceitarToken(";")) {
                    }
                }
            }
        } else if (aceitarToken("(")) {
            fatoracaoChamadaMetodo();
        }
    }

    private void fatoracaoChamadaMetodo() {
        if (aceitarToken(")")) {
            if (aceitarToken(";")) {
            }
        } else {
            passagemParametros();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                }
            }
        }
    }
    
    private void addVariavel(){
        if(classeAtual==null){
            if(!global.addVariavel(variavelAtual)){
                //erro ao add variavel
                salvarMensagemArquivo("Variável já existente com esse nome. Linha: " + tokenAnteriorAnterior.getLinha());
            }
        }else{
            if(metodoAtual==null){
                if(!classeAtual.addVariavel(variavelAtual)){
                    //erro ao add variavel
                     salvarMensagemArquivo("Variável já existente com esse nome. Linha: " + tokenAnteriorAnterior.getLinha());
                }
            }else{
                if(!metodoAtual.addVariavel(variavelAtual)){
                    //erro ao add variavel
                     salvarMensagemArquivo("Variável já existente com esse nome. Linha: " + tokenAnteriorAnterior.getLinha());
                }
            }
        }
    }

    private void addParametro() {
        if(!parametrosAtuais.contains(parametroAtual)){
            parametrosAtuais.add(parametroAtual);
        }else{
            //erro parametro já existe com esse nome
            salvarMensagemArquivo("Parâmetro já existente com esse nome. Linha: " + + tokenAtual.getLinha());
        }
    }

    private void addMetodo() {
        if(classeAtual==null){
            //tentando declarar metodo fora da classe erro
            salvarMensagemArquivo("Método declarado fora de uma classe. Linha: " + tokenAtual.getLinha());
        }else{
            classeAtual.addMetodo(metodoAtual);
        }
    }
    
    private void verificarHeranca(){
        List <Classe> classes = global.getClasses();
        for(Classe c: classes){
            if(c instanceof ClasseFilha){
                Classe mae = global.getClasse(((ClasseFilha) c).getNomeMae());
                if(mae!=null){
                    if(mae instanceof ClasseFilha){
                        System.out.println("erro não permitida herança em cadeia");
                        salvarMensagemArquivo("Erro! Herança em cadeia não permitida. Linha: " + + tokenAtual.getLinha());
                    }else{
                        ((ClasseFilha) c).setMae(mae);
                    }  
                }else{
                    System.out.println("mae não encontrada");
                    salvarMensagemArquivo("Erro! Classe Mãe não encontrada. Linha " + tokenAtual.getLinha());
                }
            }
        }
    }

    private void verificarTamanhoVetor() {
        String numero = tokenAnterior.getNome();
        if(numero.contains(".")){
            System.out.println("Erro! Somente permitidos numero inteiros para tamanho de vetor");
            salvarMensagemArquivo("Erro! Somente são permitidos números inteiros para tamanho de vetor. Linha: " + tokenAnterior.getLinha());
        }else {
            int num = Integer.parseInt(numero);
            if(num<1){
                System.out.println("Erro! Tamanho do vetor menor 1");
                salvarMensagemArquivo("Erro! Tamanho do vetor menor 1. Linha: " + tokenAnterior.getLinha());
            }
        }
    }
    
    private void salvarMensagemArquivo(String mensagem){
        try {
            saidaSematico.write(mensagem);
            saidaSematico.newLine();
            errosSemanticos++;
        } catch (IOException ex) {
            Logger.getLogger(ASemanticoParser1.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
