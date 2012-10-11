package afgenjava;
import java.lang.StringBuffer;
import java.util.*;


/**
 *
 * @author Cristhian Parra ({@link cdparra@gmail.com})<br>
 * @author Fernando Mancia ({@link fernandomancia@gmail.com})
 */
public class Automata {
        
/*------------------------ ATRIBUTOS ------------------------*/
    
    /**
     * Lista de Estados que componen el automata
     */
    private ListaEstados estados;  
    
    /**
     * Apuntador al Estado inicial del mismo
     */
    private Estado inicial;         
    
    /**
     * Lista de apuntadores a estados finales 
     */
    private ListaEstados finales;   
    
    /**
     * Identificador del tipo de Automata representado. Puede ser: <br>
     * <ul>
     *  <li>AFN (Automata Finito No-Determinístico)        </li>
     *  <li>AFD (Automata Finito Determinístico)           </li>
     *  <li>AFDMin (Automata Finito Determinístico Mínimo) </li>
     * </ul>
     */
    private TipoAutomata tipo;
    
    // Los siguientes atributos podríán ser eliminados
    
    /**
     * Expresion regular representada
     */
    private String regex;           
    
    /**
     * Alfabeto sobre el cual se define la expresión regular
     */
    private ArrayList<String> alpha;    
    
    private String empty = CONSTANS.getVacio();
  
    
    // VARIABLES AUXILIARES
    
    private int level = 0;
    
/*------------------------------ CONSTRUCTORES ------------------------------*/
    
    /**
     * Constructor Vacío
     */
    public Automata() {
        this.estados = new ListaEstados();
        this.finales = new ListaEstados();
    }
    
    /**
     * Constructor de un automata simple. Compuesto por dos estados y un solo 
     * enlace a través del simbolo especificado. 
     * @param simbolo Expresion regular simple (de un solo caracter)
     */
    public Automata(String simbolo) {
        this.estados = new ListaEstados();

        Estado e1 = new Estado(0, true, false, false);
        Estado e2 = new Estado(1, false, true, false);        
        Enlace enlace = new Enlace(e1, e2, simbolo);
        e1.addEnlace(enlace);

        this.estados.insertar(e1);
        this.estados.insertar(e2);
        
        // Actualización de apuntadores auxiliares
        this.inicial = e1;
        this.finales = new ListaEstados();
        this.finales.add(e2);
    }

    /**
     * Constructor auxiliar para automatas simples con especificación del tipo
     * de automata a construir. 
     * @param simbolo Expresion regular simple (de un solo caracter)
     * @param tipo Especificación del tipo de automata en construcción
     */
    public Automata(String simbolo, TipoAutomata tipo) {
        this(simbolo);
        this.tipo = tipo;
    }
    
/*----------------------- OPERACIONES DE THOMPSON -----------------------*/    
    
    /**
     * Implementación de la generación de automatas según la definición de 
     * Thompson para la operación "|"
     * 
     * @param A2 Automata a seguir como camino alternativo al actual
     */
    public void thompson_or(Automata A2){
        
        Automata A1 = this;
        
        
        
        // Obtenemos las referencias a los finales e iniciales correspondientes
        Estado final_A1 = A1.getFinales().getEstado(0);
        Estado final_A2 = A2.getFinales().getEstado(0);
        Estado inicial_A1 = A1.getInicial();
        Estado inicial_A2 = A2.getInicial();
        
        final_A1.setEstadofinal(false);
        final_A2.setEstadofinal(false);
        
        // Se crean 2 nuevos estados
        Estado estado_inicial = new Estado(0, true, false, false);
        Estado estado_final = new Estado(A1.estados.size()+A2.estados.size()+1, false, true, false);

        
        // Actualizar estados iniciales de A1 y A2 
        A1.inicial.setEstadoinicial(false);
        A2.inicial.setEstadoinicial(false);
        
        // Se incrementan los numeros de ambos automatas
        A1.renumerar(1);
        A2.renumerar(A1.estados.size()+1);
                
        // Se crean los enlaces vacios desde el nuevo estado inicial
        
        // 1. Nuevo Inicio --> Inicio del Automata Actual
        estado_inicial.addEnlace(new Enlace(estado_inicial, 
                                            inicial_A1,
                                            this.empty));
        
        // 2. Nuevo Inicio --> Inicio del Automata Alternativo
        estado_inicial.addEnlace(new Enlace(estado_inicial, 
                                            inicial_A2,
                                            this.empty));
        
        // Se crean los enlaces desde los finales del Actual (A1) y el 
        // alternativo (A2) hacia el Nuevo Estado Final.
                        
        // 3. Fin del Actual (A1) --> Nuevo Estado Final
        final_A1.addEnlace( new Enlace( final_A1, estado_final, this.empty) );
        
        // 4. Fin del Alternativo (A2) --> Nuevo Estado Final        
        final_A2.addEnlace( new Enlace( final_A2, estado_final, this.empty) );
                
        // Agregamos a A1 todos los estados de A2
        Iterator it = A2.estados.getIterator();
        while(it.hasNext()){
            A1.estados.insertar((Estado)it.next());
        }
        
        // Agregamos a A1 los nuevos estados creados.
        A1.estados.insertar(estado_inicial);
        A1.estados.insertar(estado_final);
        
        // Actualizar referencias auxiliares al inicial y al final del actual
        A1.inicial=estado_inicial;
        A1.getFinales().set(0, estado_final);        
    }
    
    /**
     * Implementación de la generación de automatas según la definición de 
     * Thompson para la operación de concatenación
     * 
     * @param A2 Automata siguiente al actual
     */
    public void thompson_concat(Automata A2){
        Automata A1 = this; //se agrega a este automata quedando A1 A2 osea this A2.

        // Obtener referencias al final de A1 y al inicial de de A2
        Estado final_A1   = A1.getFinales().getEstado(0);
        Estado inicial_A2 = A2.getInicial();
        
        // Se actualiza al estado inicial del Automata Siguiente (A2) para 
        // que deje de ser inicial
        inicial_A2.setEstadoinicial(false);
        final_A1.setEstadofinal(false);
        
        // Renumeramos los estados del Automata siguiente
        int a1_estado_final = A1.estados.size() - 1;
        A2.renumerar(a1_estado_final);
                        
        // Se fusiona el enlace inicial de A2 con el final de A1        
        // 1. Primero agregamos los enlaces del inicio de A2, al final de A1
        Iterator <Enlace> enlaces_a2_inicio = inicial_A2.getEnlaces().getIterator();        
        
        while(enlaces_a2_inicio.hasNext()){            
            Enlace current = enlaces_a2_inicio.next();            
            current.setOrigen(final_A1);
            final_A1.addEnlace(current);
        }
        
        // 2. Agregar los demás estados de A2, excepto su inicial, al automata A1
        Iterator <Estado> estados_a2 = A2.estados.getIterator();
        
        while(estados_a2.hasNext()){
            Estado est_a2 = estados_a2.next();
            
            // 2.1 Actualizar en el estado, todos los enlaces que apuntaban al 
            //     inicio de A2 para que apunten al nuevo inicio, que es el final 
            //     de A1 y a            
            Iterator <Enlace> enlaces = est_a2.getEnlaces().getIterator();        
            
            while(enlaces.hasNext()){
                Enlace current = enlaces.next();
                Estado current_destino = current.getDestino();
                
                // Si el destino de este enlace 
                if (current_destino.getId() == inicial_A2.getId()) {
                    current.setDestino(final_A1);                    
                }
            }
        
            // Agregar el estado al automata actual
            if(est_a2.getId() != inicial_A2.getId()){
                A1.estados.insertar(est_a2);
            }
        }
         
        A1.getFinales().set(0, A2.getFinales().getEstado(0));
    }
    
    
    /**
     * Parte de las operaciones de implementación de kleene (*), plus (+) y 
     * cerouno (?) que es común entre las tres. <br>
     * 
     * Modifica el automata actual de la siguiente manera: <br>
     * <ul>
     *   <li>Agrega dos nuevos estados (uno al inicio y otro al final) </li>
     *   <li>Agrega dos nuevos enlaces vacíos 
     *       <ul>
     *          <li>Uno para unir el nuevo inicio con el viejo</li>
     *          <li>Uno para unir el viejo fin con el nuevo</li>
     *       </ul>
     *   </li>     *          
     * </ul>     * 
     */
    public void thompson_common() {
      
        // Se realiza la operacion sobre el mismo objeto.
        Automata A1 = this;

        
        // Se incrementan en 1 los estados
        A1.renumerar(1);
        
        // Se agregan 2 Estados nuevos (Un inicial y uno al final)
        Estado estado_inicial = new Estado(0, true, false, false);
        Estado estado_final   = new Estado(A1.estados.size()+1, false, true, false);

        Estado ex_estado_inicial = A1.getInicial();
        Estado ex_estado_final   = A1.getFinales().getEstado(0);
        
        ex_estado_inicial.setEstadoinicial(false);
        ex_estado_final.setEstadofinal(false);
                     
        // Agregar vacíos al comienzo y al final
        estado_inicial.addEnlace(new Enlace(estado_inicial, 
                                            ex_estado_inicial, 
                                            this.empty));   
        
        ex_estado_final.addEnlace(new Enlace(ex_estado_final,
                                             estado_final,
                                            this.empty));
        
        
        // Actualizar referencias auxiliares
        this.inicial = estado_inicial;
        this.finales.set(0, estado_final);
        
        A1.estados.insertar(estado_inicial);
        A1.estados.insertar(estado_final);   
        
        
    }            
    
    /**
     * Implementación de la operación '?' sobre el automata actual. <br> 
     * 
     * Consiste en Agregar al automata actual enlaces vacios al comienzo y al 
     * final ademas de un enlace vacio entre el inicio y el final para permitir
     * que se pueda recorrer o no el Automata actual, tal como lo especifica la 
     * operación ? <br>
     * 
     * Observación: La operación '?' no está prevista entre las operaciones 
     * originales de Thompson por lo que implementamos nuestra propia versión
     * 
     */
    public void thompson_cerouno() {
        
        // Agrega dos nuevos estados al inicio y al final y los enlaza al 
        // inicio y al final del automata original respectivamente, 
        // por medio del símbolo vacío
        this.thompson_common();
        
        // Se agregan un enlace vacío entre el nuevo inicio y el nuevo fin
        this.inicial.addEnlace(new Enlace(this.inicial,
                                          this.finales.getEstado(0), 
                                          this.empty));
    }
    
    /**
     * 
     */
    public void thompson_plus() {

        Estado inicio_original = this.inicial;
        Estado fin_original    = this.getFinales().getEstado(0);
        
        // Agrega dos nuevos estados al inicio y al final y los enlaza al 
        // inicio y al final del automata original respectivamente, 
        // por medio del símbolo vacío
        this.thompson_common();
        
        // Se agregan un enlace vacío entre el fin original y inicio original
        // para que se recorra el actual por lo menos una vez y pueda ser 
        // recorrido más veces como lo especifica la operación '+'
        fin_original.addEnlace(new Enlace(fin_original,
                                          inicio_original, 
                                          this.empty));
    }
    
    public void thompson_kleene(){
        Estado inicio_original = this.inicial;
        Estado fin_original    = this.finales.get(0);
        
        // Agrega dos nuevos estados al inicio y al final y los enlaza al 
        // inicio y al final del automata original respectivamente, 
        // por medio del símbolo vacío
        this.thompson_common();
        
        // Se agrega un enlace vacío entre el fin original y inicio original
        // para que se recorra el actual más veces como lo especifica 
        // la operación *
        fin_original.addEnlace(new Enlace(fin_original,
                                          inicio_original, 
                                          this.empty));    
        
        // Se agregan un enlace vacío entre el nuevo inicio y el nuevo fin
        this.inicial.addEnlace(new Enlace(this.inicial,
                                          this.finales.getEstado(0), 
                                          this.empty));
    }
    
    
    /* --------------- GETTERS Y SETTERS -------------------- */
    
    

    /**
     * Obtener el estado referenciado por el índice correspondiente
     * 
     * @param index indice en el listado donde se encuentra el estado. 
     * @return Estado guardado en index
     */
    public Estado getEstado(int index){
        return this.estados.getEstado(index);
    }
    
    public ListaEstados getEstados() {
        return this.estados;
    }
    
    public Estado getEstadoById(int id) {
        return this.estados.getEstadoById(id);
    }

    /**
     * Obtener la lista de estados finales. 
     * 
     * En el AFN, siempre hay un solo estado final, cuya referencia se guarda en 
     * la primera posición de este listado. 
     * @return ListaEstados Lista de Estados finales del Automata
     */
    public ListaEstados getFinales() {
        return finales;
    }
    
    /**
     * Obtiene la lista de estados no finales.
     **/
    public ListaEstados getNoFinales(){
        ListaEstados lista = new ListaEstados();
        for(Estado x : estados){
            if(!x.isEstadofinal()){
                lista.insertar(x);
            }
        }
        return lista;
    }

    
    /** 
     * Obtener el estado inicial del automata. 
     * 
     * @return Estado inicial del automata
     */
    public Estado getInicial() {
        return inicial;
    }

    public void setInicial(Estado ini) {
        this.inicial = ini;
    }
    
    public ArrayList<String> getAlpha() {
        return this.alpha;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    
    public void setAlpha(ArrayList<String> alpha) {
        this.alpha = alpha;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    
    /**
     * Renumera los identificadores del Automata incrementando su valor según un
     * incremento dado. 
     * 
     * @param incremento para renumerar los estados del automata. 
     */
    public void renumerar(int incremento){
        
        //Renumerar Estados
        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            e.setId(e.getId()+incremento);
        }

    }
    
/* TEST */
    
    public String imprimir(){

        String result =""; 
        
        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            result += "\nE."+ e.getId();
            
            if (e.isEstadoinicial()) {
                result += "(ini)";
            }
            
            if (e.isEstadofinal()) {
                result += "(fin)";
            }
            
            
            result+="\n";
            
            Iterator itenlaces = e.getEnlaces().getIterator();
            while(itenlaces.hasNext()){
                Enlace enlace = (Enlace) itenlaces.next();
                result +="\t"+
                        enlace.getOrigen().getId() + " ---"+enlace.getEtiqueta()+"---> " + enlace.getDestino().getId()+"\n";
            }
        }
        return result;
    }
    
    
    private void eliminarEstado(Estado e){
        
        for(Estado est: this.estados){
            for(Enlace enlace: est.getEnlaces()){
                if( e.getId() != est.getId() && enlace.getDestino().getId() == e.getId()){
                        est.eliminarEnlace(enlace);
                }
            }
        }
    }


    /***
     * Método que elimina de este Automata los estados muertos, es decir, los 
     * estados en el que todos sus enlaces van a si mismo y no es estado final.
     */
    public void eliminar_estados_muertos(){
       
       for(Estado e : this.getEstados()){
           if(e.esEstadoMuerto()){
               eliminarEstado(e);
           }
       }
   }
    
    
    public ListaEnlaces getEnlaces(){
        ListaEnlaces ret = new ListaEnlaces();
        
        for(Estado est: getEstados()){
            for(Enlace enlace: est.getEnlaces()){    
                ret.add(enlace);
            }
        }
        
        return ret;
    }
    
    /**
     * Genera un String que puede ser utilizado para graficar con el GraphViz<br><br>
     * 
     * Ejemplo: <br><br>
     * <code>
     * digraph test123 {
     *         a -> b -> c;
     *         a -> {x y};
     *         b [shape=box];
     *         c [label="hello\nworld",color=blue,fontsize=24,
     *              fontname="Palatino-Italic",fontcolor=red,style=filled];
     *         a -> z [label="hi", weight=100];
     *         x -> z [label="multi-line\nlabel"];
     *         edge [style=dashed,color=red];
     *         b -> x;
     *         {rank=same; b x}
     * }
     * </code>
     * 
     * @return String del grafo formateado para dot (GraphViz) 
     */
    public String imprimirGraphViz(){

        String result_header = "Digraph AFN {\n" +
                "\trankdir=LR;\n\toverlap=scale;\n";

        String result_nodes = "";
        String result_edges = "";
        
        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            String shape = "circle";
            
            if (e.isEstadofinal()) {
                shape = "doublecircle";
            }
                
            result_nodes+=e.getId() + " [shape="+shape+"];\n";
            
            shape="circle";
            
            Iterator itenlaces = e.getEnlaces().getIterator();
            while(itenlaces.hasNext()){           
                
                Enlace enlace = (Enlace) itenlaces.next();
                
                Estado orig = enlace.getOrigen();
                Estado dest = enlace.getDestino();
                String label = enlace.getEtiqueta();                
                
                result_edges += orig.getId() + " -> " + dest.getId() + 
                                " [label = \""+label+"\" ];\n";
                
            }            
        }
        String result = result_header + result_nodes + result_edges + "}";
        return result;
    }
    
    
    /**
     * Genera un automata sencillo de prueba. 
     * @return
     */
    public static Automata dameAutomata(){
        Automata A1 = new Automata();
        A1.estados.insertar(new Estado(0,true,false, false));
        A1.estados.insertar(new Estado(1,true,false, false));
        A1.estados.insertar(new Estado(2,true,false, false));
        A1.estados.insertar(new Estado(3,true,false, false));
        A1.estados.insertar(new Estado(4,true,false, false));
        A1.estados.insertar(new Estado(5,true,false, false));
        
        //Estado 0
        A1.estados.getEstadoById(0).addEnlace( new Enlace(A1.estados.getEstadoById(0), 
                                           A1.estados.getEstadoById(1), "a"));
        
        A1.estados.getEstadoById(0).addEnlace( new Enlace(A1.estados.getEstadoById(0), 
                                           A1.estados.getEstadoById(2), "b"));
        
        
        //Estado 1 y 2
        A1.estados.getEstadoById(1).addEnlace( new Enlace(A1.estados.getEstadoById(1), 
                                           A1.estados.getEstadoById(3), "a"));
        
        A1.estados.getEstadoById(2).addEnlace( new Enlace(A1.estados.getEstadoById(2), 
                                           A1.estados.getEstadoById(4), "a"));
        
        
        //Estado 3 y 4
        A1.estados.getEstadoById(3).addEnlace( new Enlace(A1.estados.getEstadoById(3), 
                                           A1.estados.getEstadoById(5), "b"));
        
        A1.estados.getEstadoById(4).addEnlace( new Enlace(A1.estados.getEstadoById(4), 
                                           A1.estados.getEstadoById(5), "a"));
        return A1;
    }

    public TipoAutomata getTipo() {
        return tipo;
    }

    public void setTipo(TipoAutomata tipo) {
        this.tipo = tipo;
    }

    public void addEstado(Estado e){
        this.estados.insertar(e);
    }
  
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
