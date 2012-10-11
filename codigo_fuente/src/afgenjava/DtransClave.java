/*
 * DtransClave.java
 *
 * Created on 11 de noviembre de 2008, 12:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package afgenjava;

import traductor.Token;

/**
 * Clase DtransClave, 
 *      Se utiliza como clave de el Dtrans, que es un hashtable.
 *      Consta de:
 *              1 (una) lista de estados
 *              1 (un) token
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class DtransClave {
    
    /**
     * Las filas, indicadas por una lista de estados
     */
    private ListaEstados indiceEstados;

    /**
     * La columna, indicada por un token del lenguaje
     */
    private Token indiceToken;
    
    

    
    
    /** Creates a new instance of DtransClave */
    public DtransClave(ListaEstados list, Token tok) {
        this.indiceEstados = list;
        this.indiceToken = tok;
    }
    
    
    /**
     * Getter for property indiceEstados.
     * @return Value of property indiceEstados.
     */
    public ListaEstados getIndiceEstados() {
        return this.indiceEstados;
    }

    /**
     * Setter for property indiceEstados.
     * @param indiceEstados New value of property indiceEstados.
     */
    public void setIndiceEstados(ListaEstados indiceEstados) {
        this.indiceEstados = indiceEstados;
    }


    /**
     * Getter for property indiceToken.
     * @return Value of property indiceToken.
     */
    public Token getIndiceToken() {
        return this.indiceToken;
    }

    /**
     * Setter for property indiceToken.
     * @param indiceToken New value of property indiceToken.
     */
    public void setIndiceToken(Token indiceToken) {
        this.indiceToken = indiceToken;
    }
    
    
    
    /**
     * Compara 2 claves del Dtrans.
     **/
    public int compareTo(Object otro){
        DtransClave o = (DtransClave) otro;
        if(indiceToken.getValor().compareTo(o.getIndiceToken().getValor()) == 0) {
            if(indiceEstados.compareTo(o.getIndiceEstados()) == 0){
                return 0;
            }else{
                return -1;    
            }
        }else{
            return -1;
        }
    }
    
}
