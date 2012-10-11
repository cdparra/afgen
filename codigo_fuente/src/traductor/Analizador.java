package traductor;

import afgenjava.*;
import exceptions.LexicalError;
import exceptions.SyntaxError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * El traductor es el encargado de implementar los procedimientos necesarios
 * para llevar a cabo el proceso de traducción <br> <br>
 * 
 * El traductor está basado en el siguiente BNF para definir un lenguaje de 
 * expresiones regulares. <br><br>
 * <ol TYPE=i>
 *   <li>&nbsp RE => resimple A         </li>
 *   <li>&nbsp A  => “|” resimple A | Є </li>
 *   <li>&nbsp resimple => rebasico B   </li>
 *   <li>&nbsp B => rebasico B | Є      </li>
 *   <li>&nbsp rebasico => list op      </li>
 *   <li>&nbsp op => * | + | ? | Є      </li>
 *   <li>&nbsp list => grupo | leng     </li>
 *   <li>&nbsp grupo => “(” RE “)”      </li>
 *   <li>&nbsp leng => [alfabeto del lenguaje] </li>
 * </ol> <br><br>
 * 
 * Se implementa un Traductor Dirigido por la Sintaxis que sigue este BNF y 
 * produce el automata basándose en las construcciones de Thompson. <br><br>
 * 
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class Analizador {
 
    /**
     * Analizador Lexico
     */
    private Lex lexico;
    
    /**
     * Expresión regular a traducir
     */
    private String regex;
    
    /**
     * Token que contiene el simbolo que se está procesando actualmente
     */
    private Token preanalisis;
    
    /**
     * Alfabeto sobre el cual está definida la expresión regular.
     */
    private Alfabeto alfabeto;
    
    /**
     * Automata en el cual se guardará el resultado final de la traducción. 
     * Se trata de un Automata del tipo AFN. 
     */
    private Automata automata;
    
    /**
     * Simbolo especial utilizado para guardar recordar el símbolo operador
     * consumido por una producción, cuando se deba aplicar la misma en una 
     * producción superior
     */
    private String Special;
    
    /**
     * Contador de caracteres procesados
     */
    private int posicion;
    
    /**
     * Flag que indica la existencia o no de errores al final de la traducción
     */
    private boolean hayErrores = false;

    /**
     * Flag que indica la existencia o no de errores al final de la traducción
     */
    private String errMsg = ""; 
    /**
     * Constructor vacío de la clase <code>Analizador</code>
     */
    public Analizador() {
    }

    /**
     * Constructor del <code>Analizador</code> Sintáctico a partir de la 
     * expresión regular y el alfabeto de entrada. 
     * 
     * @param regex Expresión regular cuyo AFN queremos generar
     * @param alfabeto Alfabeto sobre el cual está definida la expresión regular
     */
    public Analizador(String regex, String alfabeto) {
        this.setPosicion(0);
        this.regex = regex;        
        this.alfabeto = new Alfabeto(alfabeto);        
        this.lexico = new Lex(regex, alfabeto); // creamos el analizador léxico
        try {
            // creamos el analizador léxico
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {
            
            this.hayErrores = true;            
            this.errMsg = 
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            
            System.out.println(this.getErrMsg());
            this.abort();
        }
        automata = new Automata();
        automata.setTipo(TipoAutomata.AFN);
    }

    /**
     * Implementación del procedimiento que se encarga de parear el símbolo de
     * preanálisis actual con la entrada esperada según la sintaxis del lenguaje
     * 
     * @param tok Símbolo esperado
     * @throws exceptions.SyntaxError Error de Sintaxis
     */
    private void Match(String simbolo) throws SyntaxError, LexicalError {
        
        Token tok = new Token(simbolo); // se crea un Token temporal para 
                                        // compararlo con preanalisis
        
        if ( getPreanalisis().compareTo(tok) == 0 ) {
            this.setPreanalisis(this.nextSymbol());
            this.Special = tok.getValor();
            this.incPosicion();
        } else {
            throw new SyntaxError(tok.getValor(),this.getPosicion());
        }
    }

    /**
     * Método que termina de manera instantánea el proceso de análisis y 
     * traducción cuando se produce un error. <br><br>
     * 
     * Inicialmente, el método solo consiste en llamar a la primitiva 
     * <code>System.exit(0)</code>, pero permite encapsular el comportamiento 
     * de esta acción para modificarla en el futuro de una sola vez. 
     */
    private void abort() {
        // Do nothing
    }
    
    /**
     * Llamada al analizador léxico para obtener el siguiente caracter de la 
     * cadena de entrada <br><br>
     * 
     * Si el analizador léxico encuentra un error (como que el caracter no 
     * pertenece al alfabeto) se atrapa la excepción, se informa en la salida y
     * se aborta el análisis. <br><br>
     * @return Token que contiene el símbolo siguiente a procesar
     */
    private Token nextSymbol() throws LexicalError {
        Token result = null; 
        result = this.lexico.next();
        return result;        
    }
    
    public Automata traducir() {
        this.automata = this.RE();
        
        if (!this.isHayErrores()) {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                this.hayErrores = true; 
                this.errMsg = "Quedaron caracteres sin analizar debido al siguiente Token no esperado["+
                        this.getPosicion()+"]: "+preanalisis.getValor();
            }
        }
        
        return this.automata;
    }
    
    /**
     * Método correspondiente al símbolo inicial de la gramática de expresiones 
     * regulares. <br><br>
     * 
     * Las producciones que pueden ser vacío, retornan un valor null en ese caso. 
     * Las demás producciones lanzan excepciones que se trasladan a los ámbitos 
     * de llamada superiores
     * 
     * @TODO
     * - Implementar Exception Management: Acciones a tomar a partir de los 
     *   distintos tipos de errores
     * 
     * @return Autoamata producido por la producción &nbsp RE => resimple A.  
     *
     */
    private Automata RE() {
        
        // automatas auxiliares de producciones llamadas
        Automata Aux1 = null;
        Automata Aux2;
        
        try {

            Aux1 = this.resimple();
            Aux2 = this.A();

            if (Aux2 != null) {
                Aux1.thompson_or(Aux2);
            }
        } catch (SyntaxError ex) {
            
            this.hayErrores = true;  
            this.errMsg = 
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            
            System.out.println(this.getErrMsg());
            this.abort();
        } catch (LexicalError ex) {
            
            this.hayErrores = true;  
            this.errMsg =
                    "Se produjo un error FATAL en el analizador léxico. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            System.out.println(this.getErrMsg());
            this.abort();
        } catch (Exception ex) {
            this.hayErrores = true;  
            this.errMsg =
                    "Se produjo un error FATAL de diseño. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            System.out.println(this.getErrMsg());
            this.abort();
        }
      
        if (!(this.hayErrores) ){
            this.setAutomata(Aux1); // Actualizar el Automata Global
            Aux1.setAlpha(this.alfabeto);
            Aux1.setRegex(this.regex);
        }
        return Aux1;
    }

    /**
     * Producción A, que permite la recursión necesaria para producir cadenas 
     * de expresiones regulares separadas por el operador "|" (disyunción) <br><br>
     * 
     * @return null si derivó en vacío, en caso contrario, el automata generado
     * @throws exceptions.SyntaxError
     */
    private Automata A() throws SyntaxError, LexicalError {        
        try {            
            Token or = new Token("|");            
            
            if (preanalisis.compareTo(or) == 0) {    
                this.Match("|"); // si preanalisis es el esperado, consumimos, 
                return RE();            
            } else {                 
                return null;    // si es vacío se analiza en otra producción
            }         
        } catch (SyntaxError ex) {
            this.hayErrores = true;  
            throw new SyntaxError("se esperaba '|' en lugar de -> "
                            +this.preanalisis.getValor(),this.getPosicion());            
        }
    }
    
    /**
     * Producción resimple
     * 
     * @return Automata producido por la producción
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Automata resimple() throws SyntaxError, LexicalError {
        Automata Aux1 = this.rebasico(); 
        Automata Aux2 = this.B();       
        
        if (Aux2 != null) {
            Aux1.thompson_concat(Aux2);
        }
        
        return Aux1;
    }

    /**
     * Producción rebasico. 
     * @return Automata generado luego de derivar la producción
     */
    private Automata rebasico() throws SyntaxError, LexicalError {
        
        Automata Aux1 = list();

        if (Aux1 != null) {
            char operator = op();

            switch (operator) {
                case '*':
                    Aux1.thompson_kleene();
                    break;
                case '+':
                    Aux1.thompson_plus();
                    break;
                case '?':
                    Aux1.thompson_cerouno();
                    break;
                case 'E':
                    break;
            }
        } /*else if (preanalisis.) {
            throw new SyntaxError("se esperaba un símbolo del lenguaje y se encontró: "
                            +this.preanalisis.getValor(),this.getPosicion());            
        }*/

        return Aux1;
    }
    
    /**
     * La producción B debe verificar si preanalisis está en el conjunto primero
     * de resimple, y si está, volver a ejecutar resimple. En caso contrario debe
     * retornar null. <br> <br>
     * 
     * El conjunto Primero de resimple es {"(",[alpha]}. 
     * 
     * @return Automata el automata producido por la producción, o null si la 
     *                  producción deriva en vacío. 
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Automata B() throws SyntaxError, LexicalError {
        
        String current = preanalisis.getValor();
        Automata result = null;
       
        if ( (preanalisis.getTipo() != TipoToken.FIN) &&
             (this.alfabeto.contiene(current) || current.compareTo("(")==0)
           ) {
            result = this.resimple();
        }
        
        return result;
    }
    
    private Automata list() throws SyntaxError, LexicalError {
        
        Token grupofirst = new Token("(");
        
        if(preanalisis.compareTo(grupofirst) == 0) {
            return this.grupo();            
        } else {
            return this.leng();
        }
    }
    
    private char op() throws SyntaxError, LexicalError {
        char operador = 'E';        
        
        if (preanalisis.getValor().compareTo("") != 0) {
            operador = preanalisis.getValor().charAt(0);

            switch (operador) {
                case '*':
                    this.Match("*");
                    break;
                case '+':
                    this.Match("+");
                    break;
                case '?':
                    this.Match("?");
                    break;
                default:
                    return 'E';
            }
        }
        return operador;
    }
    
    private Automata grupo() throws SyntaxError, LexicalError {
        try {
            this.Match("(");
        } catch (SyntaxError ex) {
            this.hayErrores = true;  
            throw new SyntaxError("se esperaba el símbolo -> '('",this.getPosicion());
        }
        
        Automata Aux1 = this.RE();
        
        try {
            this.Match(")");
        } catch (SyntaxError ex) {
            this.hayErrores = true;  
            throw new SyntaxError("se esperaba el símbolo -> ')'",this.getPosicion());
        }
        
        return Aux1;
    }
    
    /**
     * 
     * @return
     */
    private Automata leng() throws LexicalError {
        Automata nuevo = null;
        try {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                nuevo = new Automata(preanalisis.getValor(),TipoAutomata.AFN);
                this.Match(preanalisis.getValor());
            }
        } catch (LexicalError ex) {
            this.hayErrores = true;  
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: el símbolo no pertenece al alfabeto");
        } catch (Exception ex) {
            this.hayErrores = true;  
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: "+ex.getMessage());
        }
        
        return nuevo;
    }
    
    
    /* ----------------------- GETTERS Y SETTERS ------------------------ */
    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.setPosicion(0);
        this.regex = regex;        
        this.lexico = new Lex(regex, alfabeto); // creamos el analizador léxico

        try {
            // creamos el analizador léxico
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {
            this.hayErrores = true;
            this.errMsg = 
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            
            System.out.println(this.getErrMsg());
            this.abort();
        }
        automata = new Automata();
    }

    public Token getPreanalisis() {
        return preanalisis;
    }

    public void setPreanalisis(Token preanalisis) {
        this.preanalisis = preanalisis;
    }

    public Alfabeto getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
    }

    public void setAlfabetoString(String alpha) {
        this.alfabeto = new Alfabeto(alpha);
    }
    public Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata Aut) {
        this.automata = Aut;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    
    public void incPosicion() {
        this.setPosicion(this.posicion+1);
    }

    public boolean isHayErrores() {
        return hayErrores;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
