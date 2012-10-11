/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
public class LexicalError extends Exception {

    /**
     * Creates a new instance of <code>LexicalError</code> without detail message.
     */
    public LexicalError() {
    }


    /**
     * Constructs an instance of <code>LexicalError</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LexicalError(String msg) {
        super(msg);
    }
}
