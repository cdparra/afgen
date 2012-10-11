/*
 * CONSTANS.java
 *
 * Created on 10 de noviembre de 2008, 05:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package afgenjava;

/**
 *
 * @author Operador
 */
public class CONSTANS {

    private static String vacio = "(vacio)";
    private static String graphViz = "/usr/bin/dot";

    public static String getVacio() {
        
        
        return vacio;
    }

    public static void setVacio(String aVacio) {
        vacio = aVacio;
    }

    public static String getGraphViz() {
        return graphViz;
    }

    public static void setGraphViz(String aGraphViz) {
        graphViz = aGraphViz;
    }
    
    
    /** Creates a new instance of CONSTANS */
    public CONSTANS() {
    }
    
}
