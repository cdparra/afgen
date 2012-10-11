/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import afgenjava.Automata;
import afgenjava.Enlace;
import afgenjava.Estado;

import afgenjava.ListaEnlaces;
import afgenjava.ListaEstados;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import exceptions.AutomataException;
import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import java.util.ArrayList;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

/**
 * Representacion del Grafo usando la definicion jgraph.
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class AutomataGraph extends JGraph{

    // Lista de cells que se agregan al jgraph
    private ArrayList<DefaultGraphCell> cells;
    
    int width = 40, height = 40;

    private Automata automata;

    private int indiceEstadoActual = 0;


    private ListaEstados estSimular;
    
    private Estado estadoActual;

    public AutomataGraph(Automata a, DefaultGraphModel model){
        super(model);
        this.automata = a;
        
        //Imprimimos en consola para verificar.
        System.out.println("El automata que se dibujara es\n_____________________\n");
        System.out.println(a.imprimir());
        
        
        //Convertimos y dibujamos con jgraph.
        convertirJgraph();
        
        aplicar_layout_organico();
    }
    
    
    
/**
 * METODO PRINCIPAL PARA CONVERTIR EL AUTOMATA(nodos, arcos) A LOS 
 * ELEMENTOS DE UN JGRAPH
 */
    private void convertirJgraph(){

        //Borramos todo lo que habia en cells y creamos uno nuevo 
        cells = new ArrayList<DefaultGraphCell>();
        
        for(Estado elEstado : automata.getEstados()){
            incluirEnlacesEstado(elEstado);
        }
        
        Object[] elementosObj = cells.toArray();
        //DefaultGraphCell[] elementos = (DefaultGraphCell[]) cells.toArray();
        
        // Control-drag should clone selection
        setCloneable(true);

        // Enable edit without final RETURN keystroke
        setInvokesStopCellEditing(true);

        // When over a cell, jump to its default port (we only have one, anyway)
        setJumpToDefaultPort(true);


        // Insert the cells via the cache, so they get selected
        getGraphLayoutCache().insert(elementosObj);
        
    }
    
    

    
/**
 * FUNCIONES PARA APLICAR UN LAYOUT EN PARTICULAR
 */
    private void aplicar_layout_circular(){
        // Pass the facade the JGraph instance
        JGraphFacade facade = new JGraphFacade(this); 
         
        // Create aninstance of the circle layout
        JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE); 
         
        layout.run(facade); // Run the layout on the facade.
        Map nested = facade.createNestedMap(true, true); // Obtain a map of the resulting attribute changes from the facade
        getGraphLayoutCache().edit(nested); // Apply the results to the actual graph
    }
            
    private void aplicar_layout_jerarquico(){
        JGraphFacade facade = new JGraphFacade(this); 
        // Pass the facade the JGraph instance
        
        JGraphLayout layout = new JGraphHierarchicalLayout(true);//phOrganicLayout();
        // Create an instance of the appropriate layout
        
        layout.run(facade); 
        // Run the layout on the facade. Note that layouts do not implement the Runnable interface, to avoid confusion
        
        Map nested = facade.createNestedMap(true, true); 
        // Obtain a map of the resulting
        this.getGraphLayoutCache().edit(nested);
    }
    
    
    private void aplicar_layout_organico(){
        JGraphFacade facade = new JGraphFacade(this);
        facade.setDirected(true);
        
        JGraphOrganicLayout layout = new JGraphOrganicLayout();
        layout.setOptimizeEdgeDistance(true);
        layout.setEdgeCrossingCostFactor(500000);
        layout.setOptimizeEdgeDistance(true);
        layout.setEdgeDistanceCostFactor(5000);
       
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        getGraphLayoutCache().edit(nested); 
        
    }   
    
    
/**
 * FUNCIONES AUXILIARES PARA CONVERTIR DE UN AUTOMATA A UN JGRAPH
 */
    private void incluirEnlacesEstado(Estado estado){
        
        // Crear un "cell" para el Estado
        DefaultGraphCell origen = createCell(estado, width * automata.getEstados().cantidad()/2, 250);
        double x = 0;
        double y;
        for (Enlace link : estado.getEnlaces()) {
                    if (estado.getEnlaces().indexOf(link)  % 2 == 0) {
                        y = 50;
                    } else {
                        y = 450;
                    }
                    
                    DefaultGraphCell destino = createCell( link.getDestino(), x, y);
                    DefaultGraphCell currentLink = createEdge(link, origen, destino);
                    x = x + width;
                }
    }

    private DefaultGraphCell createCell(Estado estado, double x, double y) {
        DefaultGraphCell cell  = obtenerEstado(estado);
        if(cell == null){
            cell = new DefaultGraphCell(estado);
            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, width, height));
            GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createRaisedBevelBorder());
            GraphConstants.setOpaque(cell.getAttributes(), true);
            GraphConstants.setGradientColor(cell.getAttributes(), Color.LIGHT_GRAY);
            cell.addPort(new Point2D.Double(0, 0));
            
            //Agregamos al la lista
            cells.add(cell);
        }
        return cell;
    }

    private DefaultGraphCell createEdge(Enlace enlace, DefaultGraphCell source, DefaultGraphCell target) {
        DefaultEdge edge = new DefaultEdge(enlace);
        source.addPort();
        edge.setSource(source.getChildAt(source.getChildCount() -1));
        target.addPort();
        edge.setTarget(target.getChildAt(target.getChildCount() -1));
        GraphConstants.setLabelAlongEdge(edge.getAttributes(), true);
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        cells.add(edge);
        return edge;
    }


    
    
/**
 * FUNCIONES PARA ACCEDER A LOS ELEMENTOS
 */ 

    private DefaultGraphCell obtenerEstado(Estado estado){
        
        for(DefaultGraphCell oneCell: cells){
              if(oneCell.getUserObject() instanceof Estado && oneCell !=null){
                    if(((Estado)oneCell.getUserObject()).getId() == estado.getId()){
                        return oneCell;
                    }
              }
        }
        return null;
    }
                
    
    
/**
 * FUNCIONES PARA MANIPULAR LOS ELEMENTOS
 */     
    
    public void empezarSimulacion(ListaEstados estSimular){
        this.estSimular = estSimular;
        this.indiceEstadoActual = 0;
        this.estadoActual = null;
    }
    
    public void nextSimulacion(){
        if(indiceEstadoActual >= 0 && indiceEstadoActual < estSimular.cantidad()){
            estadoActual  = estSimular.get(indiceEstadoActual);
            pintarEstado(estadoActual);
            indiceEstadoActual++;            
        }else{
            estadoActual = null;
        }
    }
    
    public void previewSimulacion(){
        indiceEstadoActual--;
        if(indiceEstadoActual >= 0 && indiceEstadoActual < estSimular.cantidad()){
            estadoActual = estSimular.get(indiceEstadoActual);
            pintarEstado(estadoActual);
        }else{
            estadoActual = null;
        }
    }
    
    public void pintarEstado(Estado e){

        clearSelection();
        final DefaultGraphCell nodo = obtenerEstado(e);
        GraphConstants.setGradientColor(nodo.getAttributes(), Color.BLACK);
        GraphConstants.setBorder(nodo.getAttributes(), BorderFactory.createLineBorder(Color.BLACK));
        GraphConstants.setBackground(nodo.getAttributes(), Color.BLACK);
        nodo.setUserObject(e);
        
        System.out.println("Se trato de pintar "+e.toString());
    }
   
    
    

/**
 * GETTER's y SETTER's
 * 
 */
    public ListaEstados getEstSimular() {
        return estSimular;
    }

    public void setEstSimular(ListaEstados estSimular) {
        this.estSimular = estSimular;
    }

    public int getIndiceEstadoActual() {
        return indiceEstadoActual;
    }

    public void setIndiceEstadoActual(int indiceEstadoActual) {
        this.indiceEstadoActual = indiceEstadoActual;
    }
    

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
    }
    
    
    public static void main(String args[]) {
        try {
            Automata a = afgenjava.Main.unAutomata();
            DefaultGraphModel model = new DefaultGraphModel();
            AutomataGraph ag = new AutomataGraph(a, model);

            JFrame f = new JFrame();
            f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
            f.getContentPane().add(ag);
            f.pack();
            f.setVisible(true);
        } catch (AutomataException ex) {
            Logger.getLogger(AutomataGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
 
}
