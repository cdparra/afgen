/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package afgenjava;

import exceptions.AutomataException;
import traductor.Analizador;

/**
 *
 * @author cparra
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws AutomataException{
        String regex = "(a|b)*";
        String alpha = "ab";
        
        System.out.println("Testing: Analizador.java (testAfGen)");
        System.out.println("--> Generaci�n de un AFN simple con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha); 
        
        Analizador t = new Analizador(regex, alpha);
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_simple = A.imprimir();
        System.out.println(salida_simple);
        
        //Alg de Subconjuntos 
        AlgSubconjuntos algSub = new AlgSubconjuntos(A);
        Automata AFD = algSub.ejecutar().convertAutomata();
        System.out.println("\nAFD\n____\n");
        System.out.println(AFD.imprimir());
        
        //Eliminar estados inalacanzables
        AFD = AlgSubconjuntos.eliminar_estados_inalcanzables(AFD);
        System.out.println("\nAFD sin estados inalcanzables\n___________\n");
        System.out.println(AFD.imprimir());        
        
        //Alg de Minimizacion
        AlgMinimizacion algMin = new AlgMinimizacion(AFD);
        Automata AFDM = algMin.minimizar();
        System.out.println("\nAFDM\n_____\n");
        System.out.println(AFDM.imprimir());

        //Eliminar estados muertos
        AFDM.eliminar_estados_muertos();
        System.out.println("\nAFDM sin estados muertos\n________________\n");
        System.out.println(AFDM.imprimir());
        
    }
    
    public static Automata unAutomata() throws AutomataException{
        String regex = "a*b?(ab|ba)*b?a*";
        String alpha = "ab";
        
        Analizador t = new Analizador(regex, alpha);
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        /**
        //Alg de Subconjuntos 
        AlgSubconjuntos algSub = new AlgSubconjuntos(A);
        Automata AFD = algSub.ejecutar().convertAutomata();
        
        //Eliminar estados inalacanzables
        AFD = AlgSubconjuntos.eliminar_estados_inalcanzables(AFD);
        
        //Alg de Minimizacion
        AlgMinimizacion algMin = new AlgMinimizacion(AFD);
        Automata AFDM = algMin.minimizar();

        //Eliminar estados muertos
        AFDM.eliminar_estados_muertos();**/
        return A;
    }

}
