package afgenjava;

import exceptions.AutomataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class ListaEstados extends ArrayList<Estado>{

    /**
     * Identificador de la Lista de estados
     */
    private int id; 
    
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
     * Insertar un nuevo estado a la lista
     * @param e Estado a insertar.
     */
    public void insertar(Estado e) {
        this.add(e);
    }
    
    /**
     * Eliminar un estado del conjunto.
     * @param e Estado a eliminar
     */
    public void borrar(Estado e) {
        this.remove(this.getEstadoById(e.getId()));
    }
    
    /**
     * Obtener un estado de la lista. Por convención, el index de cada estado
     * será igual a su Id. 
     * @param index Indice del arraylist donde está almacenado el estado a obtener.
     * @return El estado almacenado en la posición index.
     */
    public Estado getEstado(int index){
        return this.get(index);
    }
    
    public Estado getEstadoById(int index) {
        Iterator it = this.getIterator();
        while(it.hasNext()){
            Estado e = (Estado) it.next();
            if(e.getId() == index){
                return e;
            }
        }
        throw new IndexOutOfBoundsException(" No existe en esta lista un Estado con id = " + index);        
    }
    
    /**
     * Obtener la cantidad de estados de la lista
     * @return Número de estados de la lista
     */
    public int cantidad() {
        return this.size();
    }
    
    /**
     * Devuelve un iterador para recorrer el listado de estados.
     * @return Iterador sobre el conjunto de estados.
     */
    public Iterator <Estado> getIterator() {
        return this.iterator();
    }
    
    /**
     * Con este método, se vuelven a marcar todos los estados de la lista
     * como no visitados. 
     */
    public void resetVisitas() {
        for (int i = 0; i < cantidad(); i++) {
            getEstado(i).setVisitado(false);
        }
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
    
    public Estado getEstadoInicial() throws AutomataException{
        int indice_ini = 0;
        int cant_iniciales = 0;
        for (int i = 0; i < cantidad(); i++) {
            if(getEstado(i).isEstadoinicial()){
                indice_ini = i;
                cant_iniciales++;
            }
        }
        if(cant_iniciales == 1){
            return getEstado(indice_ini);
        }else{
            throw new AutomataException("Solo debe haber un estado incial, y en esta lista existen "+ cant_iniciales);
        }
    }
    
    public Estado getEstadoFinal() throws AutomataException{
        int indice_fin = 0;
        int cant_finales = 0;
        for (int i = 0; i < cantidad(); i++) {
            if(getEstado(i).isEstadofinal()){
                indice_fin = i;
                cant_finales++;
            }
        }
        if(cant_finales == 1){
            return getEstado(indice_fin);
        }else{
            throw new AutomataException("Este metodo se usa cuando existe un solo " +
                    "estado final y en esta lista existen " + cant_finales + 
                    ". Utilize el metodo getEstadosFinales");
        }    
    }

    
    public ListaEstados getEstadosFinales() throws AutomataException{
        ListaEstados nuevaLista = new ListaEstados();
        for (int i = 0; i < cantidad(); i++) {
            if(getEstado(i).isEstadofinal()){
                nuevaLista.insertar(getEstado(i));
            }
        }
        return nuevaLista;
    }
    
    public boolean contieneInicial(){
        //verificar q contenga un estado inicial
        Estado ini = null;
        try{
            ini = getEstadoInicial();
            return true;
        }catch (Exception ex){
            return false;
        }
    }
    
    public boolean contieneFinal() {
        ListaEstados fin;
        try {
            fin = getEstadosFinales();
        } catch (AutomataException ex) {
            return false;
        }
        
        if(fin.cantidad() > 0){
            return true;
        }else{
            return false;
        }
    }
    
    
    /**
     * Método para ordenar los estados de la lista
     */
    public void ordenar() {       
        
        Estado a[] = new Estado[1]; 
        
        a = this.toArray(a);
        Comparator<Estado> c = null;
                
        Arrays.sort(a, c);
        
        this.removeAll(this);
        
        for(int i = 0; i < a.length; i++) {
            this.add(a[i]); 
        }
    }
    
    /**
     * Método heredado reescrito para comparar dos listas de estados. 
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
        
        ListaEstados otro = (ListaEstados) o;
        
        //Se ordenan ambas Listas
        otro.ordenar();
        this.ordenar();
        
        // comparación de cantidad de estados
        if (this.cantidad() == otro.cantidad()) {
            
            // comparación uno a uno
            for (int i = 0; i < this.cantidad(); i++) {
                
                Estado a = this.getEstado(i);
                try{
                    otro.getEstadoById(a.getId());    
                }catch(Exception ex){
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
        
        result = result + this.getId() + " = { ";
        
        for (int i = 0; i < this.cantidad(); i++) {
            
            result = result + ( this.get(i) ).getId();
            
            if (!(i == (this.cantidad()-1))) {
                result = result + ", ";
            }
        }
        
        result = result + " } ";
        
        return result;
    }

    /**
     * Holds value of property marcado.
     */
    private boolean marcado;

    /**
     * Getter for property marcado.
     * @return Value of property marcado.
     */
    public boolean isMarcado() {
        return this.marcado;
    }

    /**
     * Setter for property marcado.
     * @param marcado New value of property marcado.
     */
    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }


}
