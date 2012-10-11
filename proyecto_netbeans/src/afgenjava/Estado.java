package afgenjava;

import java.util.ArrayList;
import java.util.HashMap;
import traductor.Token;

/**
 * La clase <b> Estado </b> representa a los nodos dentro de un Autómata 
 * finito. <br><br>
 * 
 * Un estado está definido por su nombre (identificador) y puede estar conectado 
 * a otros estados por medio de símbolos en el alfabeto. Esta clase contiene 
 * los dos componentes:<br><br>
 * <ul>
 *   <li>Identificador del Estado</li>
 *   <li>Su conjunto de enlaces asociados</li>
 * </ul>
 * 
 * <br> 
 * Además, se definine propiedades auxiliares que caracterizan al estado
 * en el automata correspondiente. 
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Estado implements Comparable<Estado> {
    private int id; 
    private ListaEnlaces enlaces;
       
    /*
     * Otras propiedades del Estado que lo definen en el contexto de un 
     * autómata
     */
    private boolean estadoinicial;  // establece si el Estado es un estado Inicial
    private boolean estadofinal;    // establece si el Estado es un estado Final
    private boolean visitado;       // establece si el Estado ya fue visitado en el 
                                    // contexto de un recorrido por el autómata 
    
    /**
     * Constructor del Estado. Inicializa todas sus características. 
     * 
     * @param id Identificador del Estado
     * @param esInicial Define si es un estado inicial
     * @param esFinal Define si es un estado final
     * @param visitado Define si ya fue visitado
     */
    public Estado(int id, boolean esInicial, boolean esFinal, boolean visitado) {
        this.id         = id;
        this.estadoinicial  = esInicial;
        this.estadofinal    = esFinal;
        this.visitado   = visitado;
        this.enlaces = new ListaEnlaces();
    }

    
    // ------------------------------ GETTERS ------------------------------ //
    
    /**
     * Obtener Id del Estado
     * @return Id del estado
     */
    public int getId() {
        return id;
    }

    /**
     * Obtener lista de enlaces del estado
     * @return ArrayList con los enlaces. 
     */
    public ListaEnlaces getEnlaces() {
        return enlaces;
    }

    /**
     * Verifica si el estado es un estado final
     * @return Boolean que define si el estado es un estado final
     */
    public boolean isEstadofinal() {
        return estadofinal;
    }

    /**
     * Verifica si el estado es un estado inicial
     * @return Boolean que define si el estado es un estado inicial
     */
    public boolean isEstadoinicial() {
        return estadoinicial;
    }

    /**
     * Verifica si el estado ya fue visitado en un recorrido
     * @return Boolean que define si el estado ya fue visitado en un recorrido
     */    
    public boolean isVisitado() {
        return visitado;
    }

    
    // ------------------------------ SETTERS ------------------------------ // 
    
    /**
     * Establece un valor para el identificador del estado
     * @param id Identificador del Estado
     */
    public void setId(int id) {
        this.id = id;
    }
        
    /**
     * Establece si el estado es Final
     * @param estadofinal Boolean que establece si el estado es o no Final
     */
    public void setEstadofinal(boolean estadofinal) {
        this.estadofinal = estadofinal;
    }

    /**
     * Establece si el estado es inicial
     * @param estadoinicial Boolean que establece si el estado es o no Inicial
     */
    public void setEstadoinicial(boolean estadoinicial) {
        this.estadoinicial = estadoinicial;
    }

    /**
     * Establece si el estado fue o no visitado en un recorrido
     * @param visitado Boolean que establece si el estado fue o no visitado en un recorrido
     */
    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
    
    // --------------------------- OTROS MÉTODOS --------------------------- //

    /**
     * Agrega un nuevo enlace que sale de este estado
     * @param e Enlace a agregar
     */
    public void addEnlace(Enlace e) {        
        // Insertar en la lista de enlaces para tener un método eficiente de 
        // recorrido en el futuro
        enlaces.insertar(e);        
    }

    /**
     * Retorna el estado destino buscando entre todos los enlaces de este estado.
     * @param a Token de la transicion.
     * @return El estado destino al que va desde este estado por el token a
     */
    public Estado estadoDestino(Token a){
        return estadoDestinoString(a.getValor());
    }

    /**
     * Retorna el estado destino buscando entre todos los enlaces de este estado.
     * @param a String que es la etiqueta de la transicion.
     * @return El estado destino al que va desde este estado por el token a
     */
    public Estado estadoDestinoString(String a){
        for(Enlace x: enlaces){
            if(x.getEtiqueta().compareTo(a)== 0){
                return x.getDestino();
            }
        }
        return null;   
    }
    
    /**
     * Obtiene el primer enlace asociado al simbolo especificado que está 
     * cargado en el Hash de enlaces
     * @param simbolo
     * @return
     */
    public Estado getDestinoFromHash(String simbolo) {
        Enlace link = this.getEnlaceSimboloFromHash(simbolo); 
        Estado result = null;
        
        if (link != null) {
            result =link.getDestino();
        }
        return result;
    }
    
    /**
     * Devuelve el enlace relacionado con el símbolo 
     * @param simbolo
     * @return
     */
    public Enlace getEnlaceSimboloFromHash(String simbolo) {
        return this.enlaces.getEnlaceSimbolo(simbolo);
    }
    
    /**
     * Si el automata es un AFN, devuelve los enlaces vacios asociado a este
     * estado.
     * @return
     */
    public ArrayList<Enlace> getEnlacesVacios() {
        return this.enlaces.getVacios();
    }
    
    
    public void eliminarEnlace(Enlace e){
        this.enlaces.borrar(e);
    }
    
    public boolean esEstadoMuerto(){
        if(isEstadofinal()){
            return false;
        }
        
        boolean esMuerto = true;
        for(Enlace e: this.enlaces){
            if(e.getDestino().getId() != this.getId()){
                esMuerto = false;
            }
        }
        return esMuerto;
    }
    
    
    
    /**
     * Implementación del método para comparar estados
     * 
     * @param e Estado al cual queremos comparar el actual
     * @return <ul> <li><b>0 (Cero)</b> si son  iguales                       </li>
     *              <li><b>1 (Uno)</b> si Estado es mayor que <b>e</b>        </li>
     *              <li><b>-1 (Menos Uno)</b> si Estado es menor que <b>e</b> </li>
     *         </ul>
     */
    public int compareTo(Estado e) {
        if (this.getId() == e.getId()) {
            return 0;
        } else if (this.getId() > e.getId()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        String result = ""+id;
        if (this.isEstadofinal()) {
            result = result + "(fin)";
        }
        
        if (this.isEstadoinicial()){
            result = result + "(ini)";
        }
        return result; 
    }
}
