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
public class TipoTokenTest {

    public TipoTokenTest() {
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
     * Test of values method, of class TipoToken.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        TipoToken[] expResult = { TipoToken.NONE,TipoToken.KLEENE,TipoToken.PLUS,
                                  TipoToken.CEROUNO,TipoToken.OR,TipoToken.PARI,
                                  TipoToken.PARD,TipoToken.ALFA,TipoToken.FIN};
        
        TipoToken[] result = TipoToken.values();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of valueOf method, of class TipoToken.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String name = "";
        TipoToken expResult = TipoToken.KLEENE;
        TipoToken result = TipoToken.valueOf("KLEENE");
        assertEquals(expResult, result);
    }

}