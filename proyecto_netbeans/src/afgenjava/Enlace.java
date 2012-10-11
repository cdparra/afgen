package afgenjava;

/**
 * La clase <b> Enlace </b> representa a los arcos que conectan los estados 
 * en una Automata Finito. <br><br>
 * 
 * Un enlace está definido por los siguientes componentes:<br><br>
 * <ul>
 *   <li>Estado Origen</li>
 *   <li>Estado Destino</li>
 *   <li>Etiqueta (símbolo del alfabeto)</li>
 * </ul>
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Enlace implements Comparable<Enlace> {
    
    /**
     * Apuntador al estado de origen del Enlace
     * Obs.: No es necesario, pero se deja porque podría favorecer a la 
     * reutilización de la clase.
     */
    private Estado origen;
    
    /**
     * Apuntador al estado de destino del Enlace
     */
    private Estado destino;
    
    /**
     * Simbolo de transición. Su valor no importa si vacio esta como true;
     */
    private String etiqueta;
    
    /**
     * Propiedad que indica si el enlace representa al símbolo vacío. 
     */
    private boolean vacio;
          
     /**
      * Constructor de la Clase Enlace. Crea un nuevo enlace entre "origen" y 
      * "destino" con la etiqueta "label"
      * 
      * @param origen  Estado de origen del enlace.
      * @param destino Estado de destino del enlace.
      * @param label   Etiqueta del Enlace
      */
    public Enlace(Estado origen, Estado destino, String label) {
        this.origen = origen;
        this.destino = destino;
        this.etiqueta = label;
        
        if (label.compareTo(CONSTANS.getVacio())==0) {
            this.vacio = true;
        } else {
            this.vacio = false;
        }
    }

    // ------------------- GETTERS Y SETTERS DE LA CLASE -------------------//
    
    /**
     * Método para obtener el estado origen del enlace
     * @return El origen del enlace
     */    
    public Estado getOrigen() {
        return origen;
    }
    
    /**
     * Método para establecer el estado origen del enlace
     * @param origen Origen a establecer
     */
    public void setOrigen(Estado origen) {
        this.origen = origen;
    }

    /**
     * Método para obtener el estado destino del enlace
     * @return El destino del enlace
     */
    public Estado getDestino() {
        return destino;
    }

    /**
     * Método para establecer el estado destino del enlace
     * @param destino Destino a establecer
     */
    public void setDestino(Estado destino) {
        this.destino = destino;
    }

    /**
     * Obtener la etiqueta del enlace 
     * @return La etiqueta del enlace.
     */
    public String getEtiqueta() {
        return this.etiqueta;
    }
    
    /**
     * Establecer la etiqueta del enlace  
     * @param label Etiqueta para el enlace
     */    
    public void setEtiqueta(String label) {
        this.etiqueta = label;
    }

    public void setVacio(boolean vacio) {
        this.vacio = vacio;
    }

    public boolean isVacio() {
        return vacio;
    }

    /**
     * Implementación del método para comparar enlaces
     * 
     * @param e Estado al cual queremos comparar el actual
     * @return <ul> <li><b>0 (Cero)</b> si son  iguales                       </li>
     *              <li><b>-1 (Menos Uno)</b> si son <b>distintos</b> </li>
     *         </ul>
     */
    public int compareTo(Enlace e) {
        
        Estado origi;
        Estado desti;
        String simbi;
        
        
        origi = e.getOrigen();
        desti = e.getDestino();
        simbi = e.getEtiqueta();

        
        if (origi == this.getOrigen()
                && desti == this.getDestino()
                && simbi.equals(this.getEtiqueta())
                ) {
            return 0;
        } else {
            return -1;
        }
    }
    
    public String toString(){
        return getEtiqueta();
    }
}
