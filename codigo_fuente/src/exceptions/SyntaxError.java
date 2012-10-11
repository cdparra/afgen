/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
public class SyntaxError extends Exception {

    /**
     * Creates a new instance of <code>SyntaxError</code> without detail message.
     */
    public SyntaxError() {
    }


    /**
     * Constructs an instance of <code>SyntaxError</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SyntaxError(String msg) {
        super(msg);
    }   
    
    
    /**
     * Construye una instancia de <code>SyntaxError</code> con el mensage 
     * detallada, anticipado por la información de la pos en la expresión 
     * regular donde se produce el error
     * 
     * 
     * @param msg el mensaje detallado
     * @param pos la posicion en la cadena de entrada donde se produjo la excepción
     */
    public SyntaxError(String msg,int pos) {
        super("Error de sintaxis en el símbolo ["+pos+"]: " +msg);
    }
}
