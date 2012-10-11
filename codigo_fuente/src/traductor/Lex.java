package traductor;

import exceptions.LexicalError;

/**
 *
 * Analizador Léxico del traductor dirigido por sintaxis de expresiones regulares
 * a AFNs
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Lex {
    
    /**
     * Buffer de String que contiene la expresión regular a analizar
     */
    private StringBuffer regex;
    
    /**
     * Lista de caracteres que conforman el alfabeto de la expresión regular<br><br>
     * En conjunto con la propiedad "specials" forman la "Tabla de Símbolos" 
     * del traductor
     */
    private Alfabeto Alpha;    
    
    /**
     * Símbolos especiales del lenguaje
     */
    private String specials;     
    
    /**
     * Constructor de la clase del analizador léxico
     * @param regex Expresión regular que se quiere analizar
     * @param alfabeto Cadena de símbolos que constituyen el alfabeto
     */
    public Lex(String regex, String alfabeto) {
        this.regex = new StringBuffer(regex);
        this.Alpha = new Alfabeto(alfabeto);
        this.specials = "*+?|()";
    }
    
    
    /**
     * Constructor de la clase del analizador léxico, con Alfabet ya creado 
     * en una ámbito superior
     * @param regex Expresión regular que se quiere analizar
     * @param alfabeto Objeto Alfabeto que contiene la lista completa de símbolos del mismo
     */
    public Lex(String regex, Alfabeto alfabeto) {
        this.regex = new StringBuffer(regex);
        this.Alpha = alfabeto;
        this.specials = "*+?|()";
    }
    
    /**
     * Consume la entrada y devuelve el siguiente a procesar. Si no se trata de
     * un token que pertenezca al alfabeto, entonces se lanza una Excepción. 
     * <br><br>
     * 
     * @return El siguiente caracter de la expresión regular
     * @throws java.lang.Exception Se lanza una excepción si el siguiente símbolo
     *                             no pertenece al alfabeto o a alguno de los 
     *                             símbolos conocidos
     */
    public Token next() throws LexicalError {
        
        String s = consume();
        Token siguiente;
        
        if (s.equalsIgnoreCase(" ") || s.equalsIgnoreCase("\t")) {
            siguiente = next();         // Los espacios y tabuladores se ignoran            

        } else if (this.specials.indexOf(s) >= 0 || this.Alpha.contiene(s) || s.length() == 0) {
            siguiente = new Token(s);   // se procesan los simbolos del alfabeto o especiales

        } else {
            String except = "El símbolo "+s+" no es válido";
            throw new LexicalError(except);
        }

        return siguiente;
    }
    
    
    /**
     * Método que consume un carácter de la expresión regular. Si retorna la 
     * cadena vacía es porque ya no hay nada que consume. <br> <br>
     * 
     * Consume consiste en extraer la primera letra de la expresión regular
     * y devolverla como un String.
     * 
     * @return El siguiente caracter en la expresión regular
     */
    private String consume() {
        
        String consumido = "";
              
        if (this.regex.length() > 0) {
            consumido = Character.toString( this.regex.charAt(0) );
            this.regex.deleteCharAt(0);
        }
        
        return consumido;
    }

    
    /**
     * Obtener el Alfabeto utilizado
     * @return Alpha El Alfabeto completo utilizado
     */
    public Alfabeto getAlpha() {
        return Alpha;
    }

    /**
     * Obtener la expresión regular
     * @return regex Expresión regular
     */
    public StringBuffer getRegex() {
        return regex;
    }

    
    /**
     * Obtener la expresión regular (en String)
     * @return regex Expresión regular, como un String
     */
    public String getRegexString() {
        return regex.toString();
    }
    
    /**
     * Obtener caracteres especiales
     * @return specials Los operadores y simbolos especiales del lenguaje
     */
    public String getSpecials() {
        return specials;
    }
}
