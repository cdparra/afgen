package traductor;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Clase que implementa el contenedor del alfabeto sobre el cual se define la 
 * expresión regular a traducir<br><br>
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Alfabeto extends ArrayList<String> {

    public Alfabeto(String simbolos) {
         
        for (int i = 0; i < simbolos.length(); i++) {
            String tmp = "" + simbolos.charAt(i);
            
            /**
             * @TODO
             * 1. No incluir espacio en blanco en el alfabeto
             */
            if (! this.contains(tmp)) {
                this.add(tmp);
            }
        }
        
        this.ordenar();        
    }
    
    /**
     * Método para obtener un iterador sobre el alfabeto.
     * @return Iterador sobre el alfabeto.
     */
    public Iterator getIterator() {
        return this.iterator();
    }
    
    /**
     * Método que permite obtener el tamaño del alfabeto .
     * @return Cantidad de símbolos del alfabeto.
     */
    public int getTamanho() {
        return this.size();
    }
        
    /**
     * Método para verificar la pertenencia de un símbolo al alfabeto.
     * @param simbolo Símbolo cuya pertenencia queremos verificar
     * @return <ul>
     *            <li><b>True</b> si el simbolo pertenece al alfabeto</li>
     *            <li><b>False</b> si el simbolo no pertenece al alfabeto</li>
     *         </ul>
     */
    public boolean contiene(String simbolo) {
        if ( this.contains(simbolo) ) return true;
        return false;
    }
    
    /**
     * Método que imprime el alfabeto.
     * @return Un String que contiene la representación en texto del alfabeto.
     */
    public String imprimir() {
        
        String result = "ALPHA = { ";
        for (int i = 0; i < this.size(); i++) {
            
            result += this.get(i);
            
            if (!(i == (this.size()-1))) {
                result += ", ";
            }
        }

        return result + " } ";

    }
    
    /**
     * Método privado que ordena las letras del alfabeto en orden ascendente.
     */
    private void ordenar() {       
        String a[] = new String[1]; 
        a = this.toArray(a);
        java.util.Arrays.sort(a);
        
        this.removeAll(this);
        for(int i = 0; i < a.length; i++) {
            this.add(a[i]); 
        }
    }
}
