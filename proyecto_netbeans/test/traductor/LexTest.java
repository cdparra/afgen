/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package traductor;

import exceptions.LexicalError;
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
public class LexTest {

    public LexTest() {
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
     * Test of next method, of class Lex.
     */
    @Test
    public void testNext() throws Exception {
        
        String regex = "a+(a*b?)?| abc";
        
        Lex instance = new Lex(regex,"abcde");
        
        System.out.println("# Lex -------> TEST --> next()");        
        System.out.println("# (1)--------> Assert cada siguiente elemento hasta terminar (sin error de alfabeto)");
        
        int count = 0;

        Token result = instance.next();
        Token expResult = new Token(regex.charAt(count)+"");
        
        while (result.getTipo() != TipoToken.FIN) {
            try {                
                
                // Vemos si el caracter actual esperado es espacio o tabulador
                // y si es así nos vemos un lugar en el regex ya que el Lex 
                // debería haber salteado estos caracteres. 
                if (expResult.getValor().compareTo(" ") == 0 
                        || expResult.getValor().compareTo("\t") == 0 ) {
                    count++;
                    
                    expResult.setValor(regex.charAt(count)+"");
                    
                    System.out.println("# (1." + count + ")------> Encontramos espacio o tab, luego avanzar a '"+expResult.getValor()+"'");
                }           
                
                
                System.out.println("# (1." + count + ")------> Assert Token " + expResult.getValor() );
                assertEquals(expResult.getValor(), result.getValor());
                count++;
                
                result = instance.next();
                
                if (result.getTipo() != TipoToken.FIN) {
                    expResult.setValor(regex.charAt(count)+"");
                }               
                
            } catch (LexicalError e) {
                System.out.println("# (1." + count + ")------> Token no existe en alfabeto");
                break;
            }
        }
        
        ///---

        System.out.println("# (2)--------> Assert cada siguiente elemento hasta terminar (con error de alfabeto)");
        
        count = 0;
        
        regex = "a+(a*z?)?| abc";
        
        instance = new Lex(regex, "abcde");
               
        while (true) {
            try {                
                result = instance.next();
                expResult.setValor(regex.charAt(count)+"");
                
                System.out.println("# (2." + count + ")------> Assert Token " + result.getValor() );
                // Vemos si el caracter actual esperado es espacio o tabulador
                // y si es así nos vemos un lugar en el regex ya que el Lex 
                // debería haber salteado estos caracteres. 
                if (expResult.getValor().compareTo(" ") == 0 
                        || expResult.getValor().compareTo("\t") == 0 ) {
                    count++;
                    expResult.setValor(regex.charAt(count)+"");
                }           
                
                assertEquals(expResult.getValor(), result.getValor());
                count++;
            } catch (LexicalError e) {
                System.out.println("# (2." + count + ")------> Token no existe en alfabeto");
                break;
            }
        }
    }
}