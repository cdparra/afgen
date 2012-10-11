/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package afgenjava;

import exceptions.AutomataException;
import graphviz.GraphViz;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import traductor.Analizador;
import static org.junit.Assert.*;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
public class SimulacionTest {

    public SimulacionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of validar method, of class Simulacion.
     */
    @Test
    public void testValidar_AFD() throws AutomataException {
        System.out.println("# Test de 'validar_AFD'\n");
        
        //String regex        = "(ab|ba)*";   // expresión regular a probar
        String regex               = "(a|b)*abb";
        //String testExito    = "abbaabab";   // cadena de prueba exitosa
        //String testFracaso  = "abbbaaba";   // cadena de prueba no exitosa
        String testExito    = "abbabbaabb";   // cadena de prueba exitosa
        String testFracaso  = "ba";   // cadena de prueba no exitosa
        String alfabeto     = "ab";
        
        // Generación de automatas
        
        // 1. Inicialización del Traductor
        Analizador t = new Analizador(regex, alfabeto);
        
        // 2. Generación del AFN. 
        Automata afn = t.traducir();
        
        String salida_simple = afn.imprimirGraphViz();        
                        
        GraphViz gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        File out = new File("out_afn.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
        
        // 3. Inicialización del Algoritmo de subconjuntos 
        AlgSubconjuntos algsub = new AlgSubconjuntos(afn); 
        
        // 4. Generación del AFD a partir del AFN
        Automata afd = algsub.ejecutar().convertAutomata();
        
        
        salida_simple = afd.imprimirGraphViz();        
                        
        gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        out = new File("out_afd.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
                
        
        // 4. Generación del AFD a partir del AFN
        AlgMinimizacion minize = new AlgMinimizacion(afd); 
        
        Automata afdMin = minize.minimizar();
        
        salida_simple = afdMin.imprimirGraphViz();        
                        
        gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        out = new File("out_afdMin.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
        
        
        // 5. Validaciones
        
        
        System.out.println("# --> Inicializando simuladores...\n");
        Simulacion afnSim = new Simulacion(testExito, afn);
        Simulacion afdSim = new Simulacion(testExito, afd);
        Simulacion afdMinSim = new Simulacion(testExito, afdMin);
        
        System.out.println("# --> Validando cadena exitosa con el AFN...\n");
        boolean expResult = true;
        boolean result = afnSim.validar();// || true; 
        
        assertEquals(expResult, result);
        
        System.out.println("# --> Validando cadena exitosa con el AFD...\n");
        result = afdSim.validar();
        System.out.println("# --> Path de simulación: "+afdSim.getSimulationPath());
        
        assertEquals(expResult, result);
        
        System.out.println("# --> Validando cadena exitosa con el AFD Mínimo...\n");
        result = afdMinSim.validar();
        System.out.println("# --> Path de simulación: "+afdSim.getSimulationPath());
        
        assertEquals(expResult, result);
        
        
        afnSim.setValidationString(testFracaso);
        afdSim.setValidationString(testFracaso);
        afdMinSim.setValidationString(testFracaso);
        
        System.out.println("# --> Validando cadena no exitosa con el AFN...\n");
        expResult = false;
        result = afnSim.validar();// && false;
        
        assertEquals(expResult, result);
        
        System.out.println("# --> Validando cadena no exitosa con el AFD...\n");
        result = afdSim.validar();
        
        System.out.println("# --> Path de simulación: "+afdSim.getSimulationPath());
        assertEquals(expResult, result);
        
        
        System.out.println("# --> Validando cadena no exitosa con el AFD Mínimo...\n");
        result = afdMinSim.validar();
        
        System.out.println("# --> Path de simulación: "+afdSim.getSimulationPath());
        assertEquals(expResult, result);
        
        
    }

    
}