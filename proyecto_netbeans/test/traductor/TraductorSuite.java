package traductor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
@RunWith(Suite.class)
@Suite.SuiteClasses ( { 
    traductor.LexTest.class,traductor.TokenTest.class,
    traductor.TipoTokenTest.class,traductor.AlfabetoTest.class,
    traductor.AnalizadorTest.class
})
public class TraductorSuite {

}