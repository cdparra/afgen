package traductor;

/**
 * Tipo "enum" que enumera los diferentes tipos de de token que se pueden 
 * manipular. <br><br>
 * 
 * <ul>
 *  <li> <b>'*': </b>&nbsp&nbsp cerradura de kleene </li>
 *  <li> <b>'+': </b>&nbsp&nbsp cerradura positiva de kleene </li>
 *  <li> <b>'?': </b>&nbsp&nbsp cero o una instancia </li>
 *  <li> <b>'|': </b>&nbsp&nbsp disyunción </li>
 *  <li> <b>'(': </b>&nbsp&nbsp paréntesis izquierdo </li>
 *  <li> <b>')': </b>&nbsp&nbsp paréntesis derecho </li>
 *  <li> <b>'ALFA': </b>&nbsp&nbsp cualquier letra del alfabeto </li>
 * </ul>
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public enum TipoToken {    
    NONE,       // token erróneo
    KLEENE,     // '*' --> cerradura de kleene
    PLUS,       // '+' --> cerradura positiva de kleene
    CEROUNO,    // '?' --> Cero o una instancia
    OR,         // '|' --> Disyunción
    PARI,       // '(' --> Paréntesis izquierdo
    PARD,       // ')' --> Paréntesis derecho
    ALFA,       // Cualquier letra del alfabeto
    FIN         // Fin de la expresión regular
}
