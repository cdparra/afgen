/*
 * AutomataException.java
 *
 * Created on 8 de noviembre de 2008, 05:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package exceptions;

/**
 *
 * @author Administrador
 */
public class AutomataException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>AutomataException</code> without detail message.
     */
    public AutomataException() {
    }
    
    
    /**
     * Constructs an instance of <code>AutomataException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AutomataException(String msg) {
        super(msg);
    }
}
