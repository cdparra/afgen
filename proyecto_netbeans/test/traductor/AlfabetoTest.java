/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package traductor;

import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas generada automáticamente y modificada para probar casos 
 * especiales en el funcionamiento de las clases implementadas. 
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class AlfabetoTest {

    public AlfabetoTest() {
        
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
     * Test of getIterator method, of class Alfabeto.
     * 
     * Se prueba sencillamente que el iterador implementado permita iterar
     * efectivamente sobre todos los elementos. 
     */
    @Test
    public void testGetIterator() {
        System.out.println("# Alfabeto --> TEST --> getIterator");
        
        Alfabeto instance = new Alfabeto("abcde");
        
        String expResult = "";
        
        Iterator<String> result = instance.getIterator();
        
        System.out.println("# (1)--------> Assert Iterator not null para alfabeto: "+instance.imprimir());
        assertNotNull(result);        
        
        int cuenta = 0;         
        while (result.hasNext()) {
            expResult = result.next();
            cuenta++;      
            
            if (cuenta==1) {                
                System.out.println("# (2)--------> Assert Iterator firs element");
                assertEquals("a", expResult);
            }
        }
        
        System.out.println("# (3)--------> Assert cuenta de elementos");
        assertEquals(cuenta, 5);        
    }

    /**
     * Test of getTamanho method, of class Alfabeto.
     */
    @Test
    public void testGetTamanho() {
        
        Alfabeto instance = new Alfabeto("abcde");
        
        System.out.println("# Alfabeto --> TEST --> getTamanho");        
        System.out.println("# (1)--------> Assert tamaño de alfbeto: "+instance.imprimir());
        
        assertEquals(instance.getTamanho(), 5);        
    }

    /**
     * Test of contiene method, of class Alfabeto.
     */
    @Test
    public void testContiene() {
        
        Alfabeto instance = new Alfabeto("abcde");
        
        System.out.println("# Alfabeto --> TEST --> contiene");        
        
        String simboloSI = "d";
        String simboloNO = "z";
        String vacio     = "";
        
        
        System.out.println("# (1)--------> Assert contiene ("+simboloSI+") sobre alfabeto: "+instance.imprimir());
        boolean result = instance.contiene(simboloSI);
        assertTrue(result);
        
        System.out.println("# (2)--------> Assert no contiene ("+simboloNO+") sobre alfabeto: "+instance.imprimir());
        result = instance.contiene(simboloNO);
        assertFalse(result);
        
        System.out.println("# (3)--------> Assert no contiene (vacio) sobre alfabeto: "+instance.imprimir());
        result = instance.contiene(vacio);
        assertFalse(result);
    }

    /**
     * Test of imprimir method, of class Alfabeto.
     */
    @Test
    public void testImprimir() {
        
        Alfabeto instance = new Alfabeto("abcde");
        
        System.out.println("# Alfabeto --> TEST --> imprimir");                
        String expResult = "ALPHA = { a, b, c, d, e } ";
        
        System.out.println("# (1)--------> Assert imprimir: "+instance.imprimir());
        String result = instance.imprimir();
        assertEquals(expResult, result);
    }
}