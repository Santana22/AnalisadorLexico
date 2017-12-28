/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;
import java.util.List;
import semantico.Classe;
import semantico.ClasseFilha;
import semantico.Global;
import semantico.Metodo;
import semantico.Variavel;

/**
 *
 * @author Emerson
 */
public class ASemantico {
    
    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private boolean umaClasse = false;
    private int umaMain = 0;
    private int posicao = -1;
    private Classe classeAtual = null;
    private Metodo metodoAtual = null;
    private Variavel variavelAtual = null;
    private List <Variavel> parametrosAtuais;
    
    private boolean proximoToken() {
        if (posicao + 1 < tokens.size()) {
            posicao++;
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
                variavelAtual = null;
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
            tipoVazio();
            tratamentoVariavel();
        } else {
        }
        variavelAtual = null;
    }

    private void classe() {
        if (aceitarToken("class")) {
            if(!umaClasse){
                umaClasse =  true;
            }
            if (aceitarToken("Identificador")) {
                classeAtual = new Classe(tokenAnterior.getNome());
                herancaNao();
                if (aceitarToken("{")) {
                    Global.addClasse(classeAtual);
                    variavelConstanteObjeto();
                    metodo();
                    if (aceitarToken("}")) {
                        Global.addClasse(classeAtual);
                        classeAtual = null;
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

    private void tipoVazio() {
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
            variavelAtual = null;
            variavelConstanteObjeto();
        }
    }

    private void geradorConstante() {
        if (aceitarToken(",")) {
            if(!Global.addVariavel(variavelAtual)){
                //constante já existente exception
            }
            tratamentoConstante();
        }
    }

    private void herancaNao() {
        if (aceitarToken("<")) {
            if (aceitarToken("-")) {
                if (aceitarToken(">")) {
                    if (aceitarToken("Identificador")) {
                        String nome = classeAtual.getNome();
                        Classe mae = Global.getClasse(tokenAnterior.getNome());
                        if(mae==null){
                            //classe mãe inexistente exception
                        }else{
                            classeAtual = new ClasseFilha(nome, mae);
                        }
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
            variavelAtual = new Variavel(tokenAnterior.getNome(), null, false);
            criarVariavel();
        } else if (aceitarToken("-")) {
            classificarVariavel();
        } else if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                if (tokenAtual.getNome().equals(">")) {
                    instancia();
                } else {
                    operation();
                    if (aceitarToken(";")) {

                    }
                }
            } else if (aceitarToken("Identificador")) {
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
        metodoAtual = new Metodo();
        if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                comSemRetorno();
            }
        }
    }

    private void comSemRetorno() {
        if (aceitarToken("Identificador")) {
            metodoAtual.setNome(tokenAnterior.getNome());
            if (aceitarToken("(")) {
                parametrosAtuais = new ArrayList();
                parametros();
                if (aceitarToken(")")) {
                    metodoAtual.setParametros(parametrosAtuais);
                    parametrosAtuais = null;
                    if (aceitarToken("{")) {
                        program();
                        if (aceitarToken("}")) {
                            addMetodo();
                            metodoAtual = null;
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
                    parametrosAtuais = new ArrayList();
                    parametros();
                    if (aceitarToken(")")) {
                        metodoAtual.setParametros(parametrosAtuais);
                        parametrosAtuais = null;
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                addMetodo();
                                metodoAtual = null;
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
                    parametrosAtuais = new ArrayList();
                    parametros();
                    if (aceitarToken(")")) {
                        metodoAtual.setParametros(parametrosAtuais);
                        parametrosAtuais = null;
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                addMetodo();
                                metodoAtual = null;
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
            variavelAtual = new Variavel();
            variavelAtual.setTipo(tokenAnterior.getNome());
            tipoVazio();
            if (aceitarToken("Identificador")) {
                variavelAtual.setNome(tokenAnterior.getNome());
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
            variavelAtual = null;
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
            variavelAtual = null;
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
            if(!Global.addVariavel(variavelAtual)){
                //erro ao add variavel
            }
        }else{
            if(metodoAtual==null){
                if(!classeAtual.addVariavel(variavelAtual)){
                    //erro ao add variavel
                }
            }else{
                if(!metodoAtual.addVariavel(variavelAtual)){
                    //erro ao add variavel
                }
            }
        }
    }

    private void addParametro() {
        if(!parametrosAtuais.contains(variavelAtual)){
            parametrosAtuais.add(variavelAtual);
        }else{
            //erro parametro já existe com esse nome
        }
    }

    private void addMetodo() {
        if(classeAtual==null){
            //tentando declarar metodo fora da classe erro
        }else{
            classeAtual.addMetodo(metodoAtual);
        }
    }
}
