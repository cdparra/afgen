/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package traductor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
public class TokenTest {

    public TokenTest() {
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
     * Test of getTipo method, of class Token.
     */
    @Test
    public void testGetTipo() {
        System.out.println("getTipo");
        Token instance = new Token(")");
        TipoToken expResult = TipoToken.PARD;
        TipoToken result = instance.getTipo();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValor method, of class Token.
     */
    @Test
    public void testGetValor() {
        System.out.println("getValor");
        Token instance = new Token("*");
        String expResult = "*";
        String result = instance.getValor();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTipo method, of class Token.
     */
    @Test
    public void testSetTipo() {
        System.out.println("setTipo");
        TipoToken tipo = TipoToken.ALFA;
        Token instance = new Token("a");
        instance.setTipo(tipo);
        
        assertEquals(instance.getTipo(), tipo);
    }

    /**
     * Test of setValor method, of class Token.
     */
    @Test
    public void testSetValor() {
        System.out.println("setValor");
        String valor = "+";
        Token instance = new Token("*");
        
        String valorIni = instance.getValor();
        
        instance.setValor(valor);
        
        String valorFin = instance.getValor();
        
        assertFalse(valorIni.compareTo(valorFin)==0);
    }

    /**
     * Test of compareTo method, of class Token.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Token t = new Token("*");
        Token instance =  new Token("*");
        int expResult = 0;
        int result = instance.compareTo(t);
        assertEquals(expResult, result);        
    }
}