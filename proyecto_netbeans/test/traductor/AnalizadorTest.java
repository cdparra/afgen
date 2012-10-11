/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package traductor;

import afgenjava.*;
import exceptions.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import graphviz.*;
import java.io.File;


/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 */
public class AnalizadorTest {
  
    public AnalizadorTest() {
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
     * Test of RE method, of class Analizador.
     */
    @Test
    public void testtraducir() {
        String regex = "a+(abc|b)*b?";
        String alpha = "abc";
        System.out.println("Testing: Analizador.java (testRE)");
        System.out.println("--> RE con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha);   
        String esperado = 
                "\nE.1\n"+"\t1 ---a---> 2\n"+
                "\nE.2\n"+"\t2 ---"+CONSTANS.getVacio()+"---> 3\n"+"\t2 ---"+CONSTANS.getVacio()+"---> 1\n"+
                "\nE.0(ini)\n"+"\t0 ---"+CONSTANS.getVacio()+"---> 1\n"+
                "\nE.3\n"+"\t3 ---"+CONSTANS.getVacio()+"---> 4\n"+"\t3 ---"+CONSTANS.getVacio()+"---> 12\n"+
                "\nE.5\n"+"\t5 ---a---> 6\n"+
                "\nE.6\n"+"\t6 ---b---> 7\n"+
                "\nE.7\n"+"\t7 ---c---> 8\n"+
                "\nE.8\n"+"\t8 ---"+CONSTANS.getVacio()+"---> 11\n"+
                "\nE.9\n"+"\t9 ---b---> 10\n"+
                "\nE.10\n"+"\t10 ---"+CONSTANS.getVacio()+"---> 11\n"+
                "\nE.4\n"+"\t4 ---"+CONSTANS.getVacio()+"---> 5\n"+"\t4 ---"+CONSTANS.getVacio()+"---> 9\n"+
                "\nE.11\n"+"\t11 ---"+CONSTANS.getVacio()+"---> 12\n"+"\t11 ---"+CONSTANS.getVacio()+"---> 4\n"+
                "\nE.12\n"+"\t12 ---"+CONSTANS.getVacio()+"---> 13\n"+"\t12 ---"+CONSTANS.getVacio()+"---> 15\n"+
                "\nE.13\n"+"\t13 ---b---> 14\n"+
                "\nE.14\n"+"\t14 ---"+CONSTANS.getVacio()+"---> 15\n"+
                "\nE.15(fin)\n";
        
        Analizador t = new Analizador(regex, alpha);
      
        Automata A = t.traducir();
        System.out.println("\n------------------------------------------------------------\n");
        System.out.println(A.imprimir());
        System.out.println("\n------------------------------------------------------------\n");
        
        
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_sinespacios = A.imprimir();
        
        assertEquals(salida_sinespacios, esperado);
          
        System.out.println("-->   TEST OK!");   
        
        regex = "a +(abc|b) *\tb\t?   ";
        alpha = "abc";
        System.out.println("--> RE con:\n-->   regex (con espacios y tab)="+regex+"\n-->   alfabeto="+alpha);   
        
        t.setRegex(regex);
        t.setPosicion(0);

        A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_conespacios = A.imprimir();
        
        System.out.print(salida_conespacios);
        assertEquals(salida_conespacios, salida_sinespacios);
          
        System.out.println("-->   TEST OK!");        
    }
    
    /**
     * Test #1 de la traducción de una expresión regular
     * 
     */
    @Test
    public void testAfGen() {
        String regex = "(a|b)?b+";
        String alpha = "ab";
        
        System.out.println("Testing: Analizador.java (testAfGen)");
        System.out.println("--> Generación de un AFN simple con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha); 
        
        Analizador t = new Analizador(regex, alpha);
                
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_simple = A.imprimir();
        
        System.out.print(salida_simple);
        
        String salida_ESPERADA = 
                "\nE.2\n"+"\t"+"2 ---a---> 3"+"\n"+
                "\nE.3\n"+"\t"+"3 ---"+CONSTANS.getVacio()+"---> 6"+"\n"+
                "\nE.4\n"+"\t"+"4 ---b---> 5"+"\n"+
                "\nE.5\n"+"\t"+"5 ---"+CONSTANS.getVacio()+"---> 6"+"\n"+
                "\nE.1\n"+"\t"+"1 ---"+CONSTANS.getVacio()+"---> 2"+"\n\t"+"1 ---"+CONSTANS.getVacio()+"---> 4"+"\n"+
                "\nE.6\n"+"\t"+"6 ---"+CONSTANS.getVacio()+"---> 7"+"\n"+
                "\nE.0(ini)\n"+"\t"+"0 ---"+CONSTANS.getVacio()+"---> 1"+"\n\t"+"0 ---"+CONSTANS.getVacio()+"---> 7"+"\n"+
                "\nE.7\n"+"\t"+"7 ---"+CONSTANS.getVacio()+"---> 8"+"\n"+
                "\nE.8\n"+"\t"+"8 ---b---> 9"+"\n"+
                "\nE.9\n"+"\t"+"9 ---"+CONSTANS.getVacio()+"---> 10"+"\n\t"+"9 ---"+CONSTANS.getVacio()+"---> 8"+"\n"+
                "\nE.10(fin)\n";
        assertEquals(salida_simple, salida_ESPERADA);
        
        System.out.println("-->   TEST OK!");     
    }
    
    /**
     * Caso de prueba que verifica que el analizador lance correctamente 
     * los errores
     */
    @Test
    public void testErrors() {
                  
        String regex = "aaaa";
        //String regex = "a|b!";
        String alpha = "ABCDEF";
        
        System.out.println("Testing: Analizador.java (testErrors)");
        System.out.println("--> Generación de un AFN con error léxico con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha); 
        Analizador t = new Analizador(regex, alpha);
                
        Automata A = t.traducir();  
        
        System.out.println(t.getErrMsg());
        assertTrue(t.isHayErrores());
        
        System.out.println("-->   TEST OK!");     
        
        System.out.println("--> Generación de un AFN con error sintáctico con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha); 
        
        regex = "(a|b)**=?+abb";
        alpha = "ab=";
        
        t = new Analizador(regex, alpha);
        
        A = t.traducir();
        
        System.out.println(t.getErrMsg());
        assertTrue(t.isHayErrores());
                
        System.out.println("-->   TEST OK!");             
    } 

    /**
     * Prueba de generación de imagen con Graphviz
     */
    @Test
    public void testImage() {
        
        String regex = "(a|b)?b+";
        String alpha = "ab";
        
        System.out.println("Testing: Analizador.java (testAfGen)");
        System.out.println("--> Generación de un AFN simple con:\n-->   regex (sin espacios)="+regex+"\n-->   alfabeto="+alpha); 
        
        Analizador t = new Analizador(regex, alpha);
                
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_simple = A.imprimirGraphViz();
        
        System.out.println(salida_simple);
        
        String result_header = "Digraph AFN {\n" +
                "\trankdir=LR;\n\toverlap=scale;\n";
        
        String salida_ESPERADA = "Digraph AFN {\n" +
                                "\trankdir=LR;\n\toverlap=scale;\n"+
                                 "2 [shape=circle];\n"+
                                 "3 [shape=circle];\n"+
                                 "4 [shape=circle];\n"+
                                 "5 [shape=circle];\n"+
                                 "1 [shape=circle];\n"+
                                 "6 [shape=circle];\n"+
                                 "0 [shape=circle];\n"+
                                 "7 [shape=circle];\n"+
                                 "8 [shape=circle];\n"+
                                 "9 [shape=circle];\n"+
                                 "10 [shape=doublecircle];\n"+                
                "2 -> 3 [label = \"a\" ];\n"+
                "3 -> 6 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "4 -> 5 [label = \"b\" ];\n"+
                "5 -> 6 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "1 -> 2 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "1 -> 4 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "6 -> 7 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "0 -> 1 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "0 -> 7 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "7 -> 8 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "8 -> 9 [label = \"b\" ];\n"+
                "9 -> 10 [label = \""+CONSTANS.getVacio()+"\" ];\n"+
                "9 -> 8 [label = \""+CONSTANS.getVacio()+"\" ];\n}";
                
        assertEquals(salida_simple, salida_ESPERADA);
        
        System.out.println("-->   TEST OK!");   
                
        GraphViz gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        File out = new File("out_afn.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
        
        assertTrue(true);
    }
    
    @Test
    public void testSubConjuntos() throws AutomataException {
        String regex = "a*b?(ab|ba)*b?a*";
        String alpha = "ab";
        
        System.out.println("Testing: Algoritmos de Subconjuntos");
        System.out.println("--> Generación de un AFN simple con:\n-->   regex="+regex+"\n-->   alfabeto="+alpha); 
        
        Analizador t = new Analizador(regex, alpha);
                
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        
        String salida_simple = A.imprimirGraphViz();        
                        
        GraphViz gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        File out = new File("out_afn_x.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
        
        
        
        System.out.println("--> Inicializando el Algoritmo de subconjuntos..."); 
        
        AlgSubconjuntos alg = new AlgSubconjuntos(A);
        
        Automata afd = alg.ejecutar().convertAutomata();
        
        salida_simple = afd.imprimirGraphViz();        
                        
        gv1 = new GraphViz();
        gv1.addln(salida_simple);
        
        System.out.println(gv1.getDotSource());
      
        out = new File("out_afd_x.gif");
        gv1.writeGraphToFile(gv1.getGraph(gv1.getDotSource()), out);
        
    }
}