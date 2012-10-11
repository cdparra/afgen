/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import afgenjava.Automata;
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
public class AutomataTableTest {

    public AutomataTableTest() {
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

    @Test
    public void testLoadTable() {
        String regex = "(ab|ba)*";
        String alpha = "ab";
        
        System.out.println("Testing: Carga de Tabla visual");
        System.out.println("--> Generación de un AFN simple con:\n-->   regex="+regex+"\n-->   alfabeto="+alpha); 
        
        Analizador t = new Analizador(regex, alpha);
                
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        System.out.println("--> Carga de la Tabla:\n-->   regex="+regex+"\n-->   alfabeto="+alpha); 
        AutomataTable tabla = new AutomataTable(A);
        
        assertEquals(A.getEstados().size(), tabla.getRowCount());
        assertEquals(A.getAlpha().size()+1, tabla.getColumnCount());        
        
        String tablaString = tabla.toString();
        System.out.println("\n"+tablaString);
        
    }

}