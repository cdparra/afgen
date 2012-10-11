package traductor;

/**
 * Clase que encapsula a cada componente enviado desde el analizador léxico 
 * al analizador sintáctico para su procesamiento. <br> <br>
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Token implements Comparable<Token> {
    
    
    private TipoToken tipo;
    
    
    private String valor;
    
    
    /**
     * Constructor principal del Token a partir del símbolo que se le pasa. Se
     * asume que el símbolo es válido ya que se deja la validcación al analizador
     * léxico. 
     * @param tipo Indica el tipo de token definidas por el enum TipoToken. 
     */
    public Token(String simbolo) {
        this.valor = simbolo;
        this.setTipo(simbolo);
    }

    /**
     * Función que retorna el tipo de token actual
     * @return Retorna el tipo de token
     */
    public TipoToken getTipo() {
        return tipo;
    }

    /**
     * Método que retorna el valor (char) del token actual. 
     * @return
     */
    public String getValor() {
        return valor;
    }

    /**
     * Establece el tipo de token
     * @param tipo Tipo del token actual
     */
    public void setTipo(TipoToken tipo) {
        this.tipo = tipo;
    }

    /**
     * Valor (en char) del tipo de token actual
     * @param valor Caracter que representa el tipo de token
     */
    public void setValor(String valor) {
        this.valor = valor;
        this.setTipo(valor);
    }
    
    /**
     * Método abstracto de la clase Comparable implementado por Token para poder
     * utilizar el operador == para las comparaciones <br><br>
     * 
     * @param t Token con el que se comparará el actual. 
     * @return <ul> <li><b>0 (Cero)</b> si son  iguales         </li>
     *              <li><b>-1 (Menos Uno)</b> si no son iguales </li>
     *         </ul>
     */
    public int compareTo(Token t) {
        if (this.getTipo() == t.getTipo() 
                && this.getValor().compareTo(t.getValor()) == 0 ) {
            return 0;
        } else {
            return -1;
        }
    }

    private void setTipo(String simbolo) {
        
        if (simbolo.isEmpty()) {
            this.tipo = TipoToken.FIN;
        } else {

            switch (simbolo.charAt(0)) {
                case '*':
                    this.tipo = TipoToken.KLEENE;
                    break;
                case '+':
                    this.tipo = TipoToken.PLUS;
                    break;
                case '?':
                    this.tipo = TipoToken.CEROUNO;
                    break;
                case '|':
                    this.tipo = TipoToken.OR;
                    break;
                case '(':
                    this.tipo = TipoToken.PARI;
                    break;
                case ')':
                    this.tipo = TipoToken.PARD;
                    break;
                default:
                    this.tipo = TipoToken.ALFA;
                    this.valor = simbolo;
                    break;
            }
        }
    }
}
