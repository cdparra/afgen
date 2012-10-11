/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package afgenjava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Wrapper de un ArrayList en el que se almacenarán los enlaces que salen de un 
 * Estado. 
 * 
 * Observación: 
 * - Evaluar si no sería mejor un HashMap con clave, en el que podríamos utilizar
 *   el símbolo del alfabeto como clave de cada enlace. 
 * 
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class ListaEnlaces extends ArrayList<Enlace> {
 
    /* --------------------- PROPIEDADES DE LA LISTA --------------------- */
    
    /**
     * Identificador de la Lista de enlaces
     */
    private int id; 
    
    /**
     * Se implementa una tabla Hash interna para la lista de enlaces que 
     * permita indexar para cada símbolo del alfabeto, el índice del array list 
     * con el enlace asociado. <br>
     * 
     * Por cada nuevo enlace, se tendrá que agregar la Hash la entrada 
     * correspondiente. Esta tabla será útil para buscar los enlaces asociados 
     * a un símbolo cuando se requiera recorrer el Automata. 
     * 
     * En esta tabla solo se guardarán los índices de enlaces asociados a 
     * símbolso no vacíos. 
     * 
     */
    private HashMap<String, Integer> TablaEnlaces;
    
    /**
     * Listado de enlaces cuya etiqueta es el simbolo vacio
     */
    private ArrayList<Enlace> vacios;
    
    
    
    public ListaEnlaces(){
        this.TablaEnlaces = new HashMap<String, Integer>();
        this.vacios       = new ArrayList<Enlace>();
    }
    
    
    /* ------------------- GETTERS Y SETTERS DE LA LISTA ------------------- */
    
    /**
     * Establecer el identificador de listado
     * @param id Identificador del conjunto de estados.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    
    /**
     * Obtener el id del conjunto de estados.
     * @return Identificador del conjunto de estados.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Obtener la lista de enlaces asociados al símbolo vacíó
     * @return Lista de enlaces del simbolo vacío
     */
    public ArrayList<Enlace> getVacios() {
        return vacios;
    }
    
    
    /**
     * Obtener un estado de la lista. Por convención, el index de cada estado
     * será igual a su Id. 
     * @param index Indice del arraylist donde está almacenado el estado a obtener.
     * @return El estado almacenado en la posición index.
     */
    public Enlace getEnlace(int index) {
        return this.get(index);
    }
    
    /**
     * Devuelve un iterador para recorrer el listado de estados.
     * @return Iterador sobre el conjunto de estados.
     */
    public Iterator<Enlace> getIterator() {
        return this.iterator();
    }
        
    
    /* --------------------- OTROS MÉTODOS --------------------- */
    
    /**
     * Insertar un nuevo estado a la lista
     * @param e Estado a insertar.
     */
    public void insertar(Enlace e) {
    
        int     indexToInsert   = this.cantidad();        
        String  simbolo         = e.getEtiqueta();
        
        this.add(e);
        
        if (e.isVacio()) {
            this.agregarEnlaceVacio(e);
        } else {
            this.TablaEnlaces.put(simbolo, indexToInsert);
        }
    }
    
    /**
     * Insertar un nuevo estado a la lista, en la posición indicada
     * @param e Estado a insertar.
     * @param index posicion donde se insertara el elemento
     */
    public void insertarAt(Enlace e, int index) {
    
        int     indexToInsert   = index;        
        String  simbolo         = e.getEtiqueta();
        
        this.add(index,e);
        
        if (e.isVacio()) {
            this.agregarEnlaceVacio(e);
        } else {
            this.TablaEnlaces.put(simbolo, indexToInsert);
        }
    }
    
    public Enlace getEnlaceSimbolo(String symbol) {
        Integer index = this.TablaEnlaces.get(symbol);        
        Enlace result = null; 
        
        if (index != null) {
            result = this.get(index);
        }
        return result; 
    }
    
    /**
     * Método que permite añadir al final de la lista de enlaces, otra lista de 
     * enlaces. Será útil para la implementación de los algoritmos de thompson. 
     * 
     * @param l
     */
    public void insertarListaEnlaces(ListaEnlaces l) {
        Iterator <Enlace> i = l.getIterator();
        Enlace current; 
        
        while(i.hasNext()) {
            current = i.next();            
            this.insertar(current);
        }
    }
    
    /**
     * Permite insertar un nuevo enlace cuya etiqueta es VACIO en la lista de 
     * vacios
     * @param e
     */
    private void agregarEnlaceVacio(Enlace e) {
        this.getVacios().add(e);
    }
    
    /**
     * Eliminar un estado del conjunto.
     * @param e Estado a eliminar
     */
    public void borrar(Enlace e) {
        
        String simbolo = e.getEtiqueta();
        
        this.remove(e);
        
        if (e.isVacio()) {
            this.getVacios().remove(e);
        } else {
            TablaEnlaces.remove(simbolo);                    
        }        
    }
    
    /**
     * Obtener la cantidad de estados de la lista
     * @return Número de estados de la lista
     */
    public int cantidad() {
        return this.size();
    }
    
       
    /**
     * Método que permite verificar si el estado e pertenece o no 
     * a la lista de estados.
     * @param e Estado para el cual queremos verificar la condición de pertenencia
     * @return True o False dependiendo de si el estado pertenece o no
     */
    public boolean contiene(Estado e) {        
        if (this.contains(e)) {
                return true;
        }        
        return false;
    }
    
    
    /**
     * Método heredado reescrito para comparar dos listas de enlaces. 
     * 
     * Dos listas de estados son iguales si tienen la misma cantidad de elementos 
     * y si los mismos son iguales en ambas listas. 
     * 
     * @param o ListaEstados con el que se comparará la lista actual.
     * @return <ul> <li><b>0 (Cero)</b> si son  iguales                       </li>
     *              <li><b>1 (Uno)</b> si Estado es mayor que <b>e</b>        </li>
     *              <li><b>-1 (Menos Uno)</b> si Estado es menor que <b>e</b> </li>
     *         </ul>.
     */
    public int compareTo(Object o) {
        
        int result = -1; 
        
        ListaEnlaces otro = (ListaEnlaces) o;
        
        // comparación de cantidad de estados
        if (this.cantidad() == otro.cantidad()) {
            
            // comparación uno a uno
            for (int i = 0; i < this.cantidad(); i++) {
                
                Enlace a = this.getEnlace(i);
                Enlace b = otro.getEnlace(i);
                
                if (a.compareTo(b) != 0) {
                    return -1;
                }
            }
            
            result = 0; //Si llego hasta aqui es xq los elementos son iguales
        }
        
        return result;
    }
    
    /**
     * Imprime en una larga cadena toda la lista de estados. 
     * @return Un String que contiene la representación en String de
     *         la lista de estados. 
     */
    public String imprimir() {
        
        String result = " ";
        
        Estado origi;
        Estado desti;
        String simbi;
        Enlace current;
        
        
        result = result + this.getId() + " = { ";
        
        for (int i = 0; i < this.cantidad(); i++) {
            
            current = this.getEnlace(i);
            
            origi = current.getOrigen();
            desti = current.getDestino();
            simbi = current.getEtiqueta();
            
            if (current.isVacio()) {
                simbi = "EMPTY";
            }
            
            result = result + "("+ origi + "--|"+simbi+"|-->"+desti+ ")";
            
            if (!(i == (this.cantidad()-1))) {
                result = result + ", ";
            }
        }
        
        result = result + " } ";
        
        return result;
    }
}   
