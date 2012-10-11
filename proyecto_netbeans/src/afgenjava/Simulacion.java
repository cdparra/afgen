/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package afgenjava;

import java.util.ArrayList;
import java.util.Stack;
import traductor.*;

/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class Simulacion {

     // variables utilizadas en la simulación y validación de cadenas de entrada
    private String validationString;
    
    private ArrayList<ListaEstados> estadosPathAFN;
    
    private ListaEstados estadosPath;     
    
    private int currentIndex = -1;

    private Automata automata;
    
    
    // variables auxiliares
    
    Stack<Estado> estadosAnt;           // conjunto actual de estados

    Stack<Estado> estadosNew;           // conjunto siguiente de estados

    boolean[] yaEstaEn;                 // cuales estados ya están en EstadosNew

    ArrayList<Enlace> enlacesVacios;         // lista utilizada para almacenar mover[s,vacio]
    
    String currentCar; 
    

    public Simulacion() {
    }

    public Simulacion(String validationString, Automata automata) {
        this.validationString = validationString;
        this.automata = automata;
        
        this.estadosPathAFN = new ArrayList<ListaEstados>();
        this.estadosPath = new ListaEstados();
        this.yaEstaEn    = new boolean[automata.getEstados().size()];
        this.estadosAnt  = new Stack<Estado>();
        this.estadosNew  = new Stack<Estado>();

        /* Deprecated
        for (boolean b : this.yaEstaEn) {
            b = false; 
        }*/
    }

    public Estado getEstadoFinal() {
        Estado result = null; 
        
        if (this.automata.getTipo() == TipoAutomata.AFN) {
            // @TODO
        } else {
            if (estadosPath != null) {
                int cantidad = estadosPath.size();       
                if (cantidad > 0) {
                    result = estadosPath.get(cantidad-1);                        
                }
            }
        }
        
        return result;
    }

    public Estado getEstadoPreFinal() {
        Estado result = null; 
        
        if (this.automata.getTipo() == TipoAutomata.AFN) {
            // @TODO
        } else {
            if (estadosPath != null) {
                int cantidad = estadosPath.size();                
                if (cantidad > 1) {
                    result = estadosPath.get(cantidad-2);                        
                } 
            }
        }
        
        return result;
    }
    
    
    /**
     * Proceso que recorre el automata para verificar si la cadena de prueba 
     * perteneces al lenguaje descrito por la expresión regular. 
     * @param test cadena de prueba cuya pertenencia queremos verificar
     * @return boolean True si la cadena pertenece, false en caso contrario. 
     */
    public boolean validar() {
        boolean exito = true;
        
        if (this.automata.getTipo() == TipoAutomata.AFN) {
            exito = this.validar_AFN();
        } else {
            exito = this.validar_AFD();
        }
            
        return exito; 
    }

    private void agregarEstado(Estado s) {
        this.estadosNew.push(s);
        this.yaEstaEn[s.getId()] = true;
        
        this.enlacesVacios = s.getEnlacesVacios(); // equivale a mover[s,(vacio)]
        
        for (Enlace e : this.enlacesVacios) {
            Estado t = e.getDestino();
            if (!this.yaEstaEn(t)) {
                this.agregarEstado(t);
            }
        }
    }

    private boolean contieneFinal(ListaEstados S) {
        boolean exito = false; 
        for (Estado e : S ) {
            exito = e.isEstadofinal();
            if (exito) {
                break;
            }
        }        
        return exito;
    }
    
    private Estado validar_AFN_Backtracking(Estado current_state) {
        
        String current = this.currentCar();
        Estado result = current_state; 
        
        Enlace path = current_state.getEnlaceSimboloFromHash(current);
        
        // Si no hay ningún enlace al símbolo, buscamos algún vacío. 
        // Solo se aplica a los AFNs
        if (path == null && this.automata.getTipo() == TipoAutomata.AFN) {
            ArrayList<Enlace> emptys = current_state.getEnlacesVacios();
            
            for (Enlace enlace : emptys) {                
                Estado siguiente = enlace.getDestino();       
                
                // se inserta el estado a seguir en el camino de validacion
                int indexEstado = this.estadosPath.cantidad();
                this.estadosPath.add(siguiente);
                result = this.validar_AFN_Backtracking(siguiente);
                
                if (result != null) {
                    break;
                }
                this.estadosPath.remove(indexEstado);
            }
        } else {  // se encontró un enlace seguir por el símbolo y avanzamos
            
            Estado siguiente = path.getDestino();        
            
            this.estadosPath.add(siguiente);            
            this.sigCar();
            
            result = this.validar_AFN_Backtracking(siguiente);
        }
        
        return result;         
    }

    
    private boolean validar_AFN() {
        boolean exito = true; 
        
        AlgSubconjuntos subc = new AlgSubconjuntos(this.automata);
        ListaEstados S = new ListaEstados();
        S = subc.e_cerradura(this.automata.getInicial(), S);
        String c = this.sigCar();
        
        this.estadosPathAFN.add(S);
        
        while (c.compareToIgnoreCase("")!=0) {
            S = subc.mover(S, new Token(c));
            S = subc.e_cerradura(S);   
            
            if (S == null || S.size() == 0) {
                exito = false;
                break;
            }
                    
            this.estadosPathAFN.add(S);
            
            c = this.sigCar(); 
        }
        
        if (exito) {
            exito = this.contieneFinal(S);
        }
        
        return exito; 
    }
    
    private boolean validar_AFD() {        
        Estado s = this.automata.getInicial();
        String c = this.sigCar();  
        boolean exito = true; 
        
        // empezamos a cargar el camino de la simulación
        this.estadosPath.insertar(s);
        
        while (c.compareToIgnoreCase("")!=0) {
            s = this.mover(s, c);
            if (s == null) {
                exito = false; 
                break;
            }
            
            this.estadosPath.insertar(s);
            c = this.sigCar(); 
        }
        
        if (s != null && !s.isEstadofinal()) {
           exito = false;
        }                
        
        return exito;
    }
    
    private Estado mover(Estado s, String c) {
        Estado next = s.getDestinoFromHash(c);
        return next;
    }
    
    private String sigCar() {
        String siguiente = "";
        this.currentIndex++; 
        if (this.currentIndex < this.validationString.length()) {
            siguiente = this.validationString.charAt(this.currentIndex)+"";            
        } else {
            this.currentIndex = 0; 
        }
        return siguiente; 
    }
    
    private String currentCar() {
        String siguiente = this.validationString.charAt(this.currentIndex)+"";
        return siguiente; 
    }
    
    
    public String getValidationString() {
        return validationString;
    }

    public void setValidationString(String validationString) {
        this.validationString = validationString;
        this.currentIndex = 0; 
        this.estadosPath = new ListaEstados();
    }

    public ArrayList<ListaEstados> getEstadosPathAFN() {
        return estadosPathAFN;
    }

    public ListaEstados getEstadosPath() {
        return estadosPath;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public

    Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
    }

    private boolean yaEstaEn(Estado t) {
        return this.yaEstaEn[t.getId()];
    }
    
    public String getSimulationPath() {
        return this.estadosPath.toString();
    }
    
}
