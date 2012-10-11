/*
 * AutomataGrafico.java
 *
 * Created on 15 de noviembre de 2008, 02:47 PM
 */

package app;

import afgenjava.*;
import graphviz.GraphViz;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

    
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;


/**
 *
 * @author  Cristhian Parra ({@link cdparra@gmail.com})
 */
public class AutomataGrafico extends javax.swing.JFrame {

    
    private Automata automata;
    private AutomataGraph jgraph;
    DefaultGraphCell[] cells;
    private String library = "graphviz";
    private JLabel imageLabel;
    private String imgDir = "/tmp";
    private Simulacion simulacion;
    private boolean simulacionResult;
    private String imgUrl;
    private String graphvizbin="/usr/bin/dot";
    private boolean enSimulacion = false;
    private ListaEstados camino;
    private Estado EstadoActual;
    private int IndexActual;
    private boolean simulacionTerminada = false;
    private String simulationMessage = "";
    private String validationString="";
    private String CaracterActual="";
    private Estado EstadoSiguiente;
    private ArrayList<File> fileList;
    private File imagenOriginal;
    private boolean CargarOriginal=true;
    private boolean HayArchivos = false;
    private boolean primeraVez = true;
    
    /** Creates new form AutomataGrafico */
    public AutomataGrafico(String library, Automata a, Configuracion conf) {        
        /*
         * Definir si usamos mxGraph o JGraph
         */
        this.automata = a;
        this.imageLabel = new javax.swing.JLabel();
        this.imageLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        this.imageLabel.setForeground(new java.awt.Color(100, 100, 255));
        this.imageLabel.setText("<Aquí va la Imagen>");
        this.library = library;
        
        this.imgDir = conf.getImgdir();
        this.graphvizbin = conf.getDotPath()+File.separator + "dot";
        // Construct Model and Graph
        DefaultGraphModel model = new DefaultGraphModel();
        this.jgraph = new AutomataGraph(a,model);
        
        initComponents();
        
        this.verificarSimulacion();
        this.cargarAutomata();
    }

    void setSimulacionResult(boolean simResult) {
        this.simulacionResult = simResult;
    }
    
    /**
     * Borra los archivos que están en la cache, incluyendo el original si así lo 
     * indica el parámetro
     * 
     * @param incluirOri
     */
    private void borrarArchivos(boolean incluirOri) {
        if (this.fileList != null) {
            for (File f : this.fileList) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
        
        this.fileList = null;
        
        if(incluirOri) {
            if (this.imagenOriginal != null && this.imagenOriginal.exists()) {
                this.imagenOriginal.delete();
            }
        }
        
        this.HayArchivos = false;
    }
    
    private void cargarAutomata() {
        if (this.library.compareTo("jgraph") == 0) {
            // Do nothing
        } else {
            this.cargarAutomataGraphViz();
        }
    }
          
    private void cargarAutomataGraphViz() {        

        this.imgUrl = this.generateImageUrl();
        GraphViz gv = new GraphViz();

        if (gv.testGraphViz()) {
            if (this.imgUrl.compareTo("<NO>") == 0) {
                this.imageLabel.setText("<El Path al Directorio de imagen es incorrecto>");
                this.imageLabel.setFont(new Font("Verdana", Font.BOLD, 14));
                this.imageLabel.setForeground(Color.orange);
            } else {
                
                boolean dibujar = this.primeraVez;     // variable que indica que debemos generar el dibujo
                
                ImageIcon i = null;
                
                if (dibujar) { // Usar las imágenes guardadas
                    i = this.dibujarNuevo();
                    this.imagenOriginal = new File (this.imgUrl);
                    this.primeraVez = false;
                } else {
                    if (this.fileList != null && ((this.fileList.size() - 1) < this.IndexActual)) {
                        dibujar = true;
                        i = this.dibujarNuevo();
                        File f = new File(this.imgUrl);
                        this.fileList.add(f);
                        this.HayArchivos = true;
                    } else if (this.fileList != null) {
                        dibujar = false; // no generar, utilizar uno ya creado.                        
                        File f = this.fileList.get(this.IndexActual);
                        i = new ImageIcon(f.getAbsolutePath());
                    }
                }
                
                if (i != null) {
                    i.setDescription("Automata Generado");
                    this.imageLabel.setIcon(i);
                }
            }
        } else {
            this.imageLabel.setText("<GraphViz no está instalado>");
            this.imageLabel.setFont(new Font("Verdana", Font.BOLD, 14));
            this.imageLabel.setForeground(Color.red);
        }
        this.imageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.imageLabel.setVisible(true);
    }
            
    private Component ScrollPaneConstructor() {
        if (this.getLibrary().compareTo("jgraph") == 0) {
            return this.jgraph;
        } else if (this.getLibrary().compareTo("graphviz") == 0){
            return this.imageLabel;
        }
        
        return this.imageLabel;
    }

    private void cargarMensajeSimulacion(String msg, Color c) {
        if (this.simulacionTerminada) {
            this.simulationMessage = "La Cadena ";
            this.simulationMessage += this.simulacionResult ? "SI " : "NO ";
            this.simulationMessage += "pertenece al Lenguaje.##" + msg;
        } else {
            this.simulationMessage = msg;
        }
        
        Color finalColor = c;
        if(this.simulacionTerminada) {
            finalColor = this.simulacionResult?Color.blue:Color.red;
        }
        Font font = new Font("VERDANA", Font.BOLD, 12);
        
        this.jTextFieldOutput.setText(this.simulationMessage);
        this.jTextFieldOutput.setForeground(finalColor);
        this.jTextFieldOutput.setFont(font);
    }

    private ImageIcon dibujarNuevo() {
        GraphViz gv = new GraphViz();
        gv.dibujar(this.getDotSyntax(), this.imgUrl);
        this.imageLabel.setText("");
        return (new ImageIcon(this.imgUrl));
    }

    /**
     * Genera un URL para la imagen a utilizar en los gráficos. 
     * Se utiliza un URL aleatorio debido a que existe un problema con la 
     * cache de los ImageIcon que hace que no se actualize la imagen si el 
     * nombre no cambio. 
     * @return Strin nombre de la imagen a cargar en el panel de dibujo
     */
    private String generateImageUrl() {
        Random r = new Random();
        r.nextInt(100000);
        String rand = ""+r.nextInt(100000);
        
        String dir = this.getImgDir();
        
        File fileDir = new File(dir);
        String dibujo = "<NO>";
        
        System.out.println("ImageDir: "+fileDir);
        if (fileDir.isDirectory()) {            
            // Crear la Imagen
            dibujo = this.getImgDir()+File.separator+"automata_"+rand+".gif";
        }        
        
        return dibujo;
    }

    /**
     * Construye la cadena de atributos visuales para un estado en particular 
     * @param e El estado que vamos a dibujar
     * @param marcado Si es true, significa que está marcado y tiene un color especial
     * @return
     */
    private String getColorEstado(Estado e, boolean marcado) {
        
        String style = "[";
        // Características gráficas de cada estado
        String shape = e.isEstadofinal()?"shape=doublecircle":"shape=circle";
        
        style += shape;
        
        /**
         * Estilos Especiales. 
         * 
         * Definen los estilos para estados finales e iniciales. Si el nodo está 
         * marcado, define los atributos de un nodo marcado. 
         */
        String coloresp     = marcado?"color=green4":"color=blue4";
        String fillcolor    = marcado?"style=filled,fillcolor=green":"style=filled,fillcolor=blue";
        String fontcolor    = marcado?"fontcolor=white":"fontcolor=white";
        String label        = e.isEstadoinicial()?",label=inicio":""; 
        
        if(e.isEstadofinal() || e.isEstadoinicial() || marcado) {
            style += ","+fillcolor+","+coloresp+","+fontcolor+label;
        }
        
        return style+"];";
    }
    
    /**
     * Construye la cadena de atributos visuales para un enlace en particular
     * @param enalce El enlace que vamos a dibujar
     * @param lbl Label del enlace
     * @param marcado Si es true, significa que está marcado y tiene un color especial
     * @return
     */
    private String getEnlaceStyle(Enlace enlace, String lbl, boolean marcado) {
        
        String style = "[";
        // Características gráficas de cada estado
        String label = "label=\""+lbl+"\"";
        
        style += label;
        
        /**
         * Estilos Especiales. 
         * 
         * Definen los estilos para enlaces marcados en una simulacion
         */
        String coloresp     = marcado?",color=green4":"";
        
        style += coloresp+"];";
        
        return style;
    }

    private void habilitarSimulacion() {
        jButtonNext.setEnabled(true);   
        jButtonPrev.setEnabled(true);
        jButtonSimular.setEnabled(true);
        jTextValidation.setEnabled(true);
    }

    private void bloquearSimulacion() {
        jButtonNext.setEnabled(false);   
        jButtonPrev.setEnabled(false);
        jButtonSimular.setEnabled(false);
        jTextValidation.setEnabled(false);
    }
      
    /**
     * Funciones dedicadas al control de la simulación
     */
    
    /**
     * Método que inicia la simulación de la validación. Se encarga de inicializar
     * el entorno de simulación. 
     * 
     */
    private void iniciarSimulacion() {
        
        this.jTextFieldOutput.setText("");
        this.enSimulacion = true;                       // bandera que indica que estamos en medio de una simulación
        this.simulacionTerminada = false;               // bandera que indica que la simulación ha terminado        

        
        // Se inicia la simulación: 
        // 1. Crear un objeto simulación y validar la cadena de entrada para construir
        //    el camino de la simulación. 
        this.setSimulacion(new Simulacion(this.jTextValidation.getText(), automata));
        this.simulacionResult = this.getSimulacion().validar();
        Color c = Color.blue;   // color por defecto para estados y enlaces de simulación actuales
        

        if (this.HayArchivos) {
            this.borrarArchivos(false); // borrar archivos de simulaciones previas sin borrar original
        }
        
        this.fileList = new ArrayList<File>();  // creamos una nueva lista de archivos
        
        // Si se introdujo una cadena de prueba no vacía, simulamos, sino solo imprimimos
        // el mensaje de validación. 
        if (this.jTextValidation.getText().compareTo("") != 0) {

            this.jButtonNext.setEnabled(true);  // habilitamos el botón siguiente

            // Solo en para la simulación con Graphviz se habilita el retroceder del simulador
            if (this.library.compareTo("graphviz") == 0) {
                this.jButtonPrev.setEnabled(true);
            }
            
         
            if (this.library.compareTo("jgraph") == 0) {
                jgraph.empezarSimulacion(simulacion.getEstadosPath());
                this.enSimulacion = true;
                cargarMensajeSimulacion("Estados = {", Color.blue);
                return;
            }

            // Mensaje inicial de simulación
            String msg = "La simulación ha comenzado! Utilice 'Next' y 'Prev' para avanzar y retroceder...";

            this.cargarMensajeSimulacion(msg, c);

            // 2. Inicializamos variables de estado actual para la simulación
            this.camino = this.simulacion.getEstadosPath(); // camino completo de estados recorridos para simular validación        

            this.EstadoActual = this.camino.getEstado(0);   // estado actual de la simulación se coloca al comienzo del camino
            
            if (IndexActual < (this.camino.size() - 1)) {
                this.EstadoSiguiente = this.camino.getEstado(1);// estado siguiente de la simulación se coloca al comienzo del camino + 1
            }
            
            this.IndexActual = 0;                           // indice del estado actual de simulación en el camino

            this.validationString = this.jTextValidation.   // actualizamos la variable de la cadena e prueba
                    getText();
            this.CaracterActual = this.validationString.    // obtenemos el primer caracter de la prueba
                    charAt(this.IndexActual) + "";

            this.cargarAutomata();                          // cargamos el nuevo dibujo generado, con los estados y enlaces pintados

        } else {
            // Si el texto está vacío, damos por terminada la simulación y presentamos el resultado de la validación
            this.simulacionTerminada = true;
            c = this.simulacionResult ? Color.green : Color.red;
            this.cargarMensajeSimulacion("", c);
        }
        
        this.jButtonSimular.setText("Reiniciar");
        this.jButtonSimular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/view-refresh.png"))); // NOI18N
    }
    
    /**
     * Mueve el entorno de simulación al siguiente estado
     */
    private void nextStep() {
        
        if (this.library.compareTo("jgraph")==0){
            this.jgraph.nextSimulacion();
            Estado actual = this.jgraph.getEstadoActual();
            if(actual != null){
                String msg = this.jTextFieldOutput.getText()+
                                    " - " + actual.getId();
                cargarMensajeSimulacion(msg, Color.blue);
            }else{
                if(this.enSimulacion){
                    this.enSimulacion = false;    
                    this.simulacionTerminada = true;
                    String msg = this.jTextFieldOutput.getText() +"}";
                    cargarMensajeSimulacion(msg, Color.blue);
                }
            }
            return;
        }
        
        Color c = Color.blue; 
        // Si se introdujo una cadena de prueba no vacía, simulamos, sino solo imprimimos
        // el mensaje de validación. 
        if (this.enSimulacion) {

            String msg = ""; // Mensaje de simulación
            int cantidadEstados = this.camino.size();
            
            if (this.IndexActual < (cantidadEstados - 1)) {
                this.IndexActual++;                
                this.EstadoActual = this.camino.getEstado(this.IndexActual);   // estado actual de la simulación se coloca al comienzo del camino
                
                if (this.IndexActual != (cantidadEstados-1)) {
                    this.EstadoSiguiente = this.camino.getEstado(this.IndexActual + 1);
                    this.CaracterActual = this.validationString. // obtenemos el primer caracter de la prueba
                            charAt(this.IndexActual) + "";
                } else {
                    this.EstadoSiguiente = null;
                    this.CaracterActual = "";
                }
            } else {
                this.EstadoActual = this.camino.getEstado(this.IndexActual);   // estado actual de la simulación se coloca al comienzo del camino
                this.EstadoSiguiente = null; 
                this.simulacionTerminada = true;               // Llegamos al último estado 
                this.CaracterActual = "";
            }

            String actual = "Actual: -"+this.EstadoActual.getId() + "- | ";
            String simbolo = "Simbolo: -"+this.CaracterActual+"- ";
            String siguiente = (this.EstadoSiguiente == null)?"":"| Siguiente: -"+this.EstadoSiguiente.getId()+"-";
            
            msg = actual + simbolo + siguiente;
            this.cargarMensajeSimulacion(msg, c);
            
            this.cargarAutomata();                          // cargamos el nuevo dibujo generado, con los estados y enlaces pintados
            if(this.simulacionTerminada) {
                jButtonNext.setEnabled(false);
            } else {                
                jButtonNext.setEnabled(true);
            }

        } else {
            // Si el texto está vacío, damos por terminada la simulación y presentamos el resultado de la validación
            this.simulacionTerminada = true;
            c = this.simulacionResult ? Color.green : Color.red;
            this.cargarMensajeSimulacion("", c);
        }
    }

    /**
     * Mueve el entorno de simulación al paso anterior
     */
    private void prevStep() {

        
        Color c = Color.blue; 
        // Si se introdujo una cadena de prueba no vacía, simulamos, sino solo imprimimos
        // el mensaje de validación. 
        if (this.enSimulacion) {

            String msg = ""; // Mensaje de simulación
            int cantidadEstados = this.camino.size();
            this.simulacionTerminada = false;
            
            if (this.IndexActual > 0) {
                this.IndexActual--;                
                this.EstadoActual = this.camino.getEstado(this.IndexActual);   // estado actual de la simulación se coloca al comienzo del camino

            } else {
                this.EstadoActual = this.camino.getEstado(this.IndexActual);   // estado actual de la simulación se coloca al comienzo del camino
                this.EstadoSiguiente = this.camino.getEstado(this.IndexActual + 1);
                this.simulacionTerminada = true;               // Llegamos al último estado 
            }
            
            if (this.IndexActual < (cantidadEstados - 1)) {
                this.EstadoSiguiente = this.camino.getEstado(this.IndexActual + 1);
            }

            this.CaracterActual = this.validationString. // obtenemos el primer caracter de la prueba
                    charAt(this.IndexActual) + "";

            String actual = "Actual: -"+this.EstadoActual.getId() + "- | ";
            String simbolo = "Simbolo: -"+this.CaracterActual+"- ";
            String siguiente = (this.EstadoSiguiente == null)?"":"| Siguiente: -"+this.EstadoSiguiente.getId()+"-";
            
            msg = actual + simbolo + siguiente;
            this.cargarMensajeSimulacion(msg, c);
            this.cargarAutomata();                          // cargamos el nuevo dibujo generado, con los estados y enlaces pintados
            if(this.IndexActual == 0) {
                jButtonPrev.setEnabled(false);
            } else {                
                jButtonPrev.setEnabled(true);
            }
            this.jButtonNext.setEnabled(true);

        } else {
            // Si el texto está vacío, damos por terminada la simulación y presentamos el resultado de la validación
            this.simulacionTerminada = true;
            c = this.simulacionResult ? Color.green : Color.red;
            this.cargarMensajeSimulacion("", c);
        }
    }
    
    /**
     * Verifica si se puede habilitar la simulación. 
     * La versión actual no soporta la simulación en el AFN.
     * 
     * Si el automata a simular no es AFN, habilita los controles de simulación, 
     * sino deshabilita los controles. 
     */
    private void verificarSimulacion() {
        if (this.automata.getTipo() != TipoAutomata.AFN) {
            this.habilitarSimulacion();
            this.jButtonNext.setEnabled(false);
            this.jButtonPrev.setEnabled(false);
        } else {
            this.bloquearSimulacion();
        }
    }
    
    /**
     * Construye la sintaxis adecuada para generar el gráfico por medio de la
     * aplicación "dot" del toolkit de GraphViz. <br> <br>
     * 
     * De acuerdo a ciertos criterios del entorno de simulación, establece los
     * colores y otras características del grafo. <br><br>
     * 
     * El estado inicial y los finales también tienen un formato especial <br><br>
     * 
     * La sintaxis de GraphViz (El lenguaje DOT) se define aquí 
     * <href="http://www.graphviz.org/doc/info/lang.html">DOT Language</href>
     * 
     * @return String Cadena completa formateada del automata en versión grapviz
     */
    public String getDotSyntax(){

        String result_header = 
                "Digraph AFN {\n" +
                "\trankdir=LR;\n\toverlap=scale;\n";

        String result_nodes = "\tnode [shape = circle];\n";
        String result_edges = "";
        
        
        ListaEstados estados = this.automata.getEstados();
        
        for (Estado e : estados) {
            boolean mark = false; 
            
            if (this.enSimulacion) {
                mark = (e.getId() == this.EstadoActual.getId());
                
                if (!mark && this.EstadoSiguiente != null) {
                    mark = (e.getId() == this.EstadoSiguiente.getId());
                }
            }
            
            String EstadoStyle = this.getColorEstado(e,mark); 
                
            result_nodes+="\t"+e.getId() + " "+EstadoStyle+"\n";
           
            for (Enlace enlace : e.getEnlaces()) {
                
                Estado orig = enlace.getOrigen();
                Estado dest = enlace.getDestino();
                String label = enlace.getEtiqueta();                
                
                mark = ((label.compareTo(this.CaracterActual)==0) && (orig.getId() == this.EstadoActual.getId()));
                
                String EnlaceStyle = this.getEnlaceStyle(enlace,label,mark);
                
                result_edges += "\t"+orig.getId() + " -> " + dest.getId() + 
                                " "+EnlaceStyle+"\n";
                
            }            
        }
        String result = result_header + result_nodes + result_edges + "}";
        return result;
    }
    
    
    /**
     * GETTERS Y SETTERS DE ATRIBUTOS DE LA CLASE
     */    
    
    public void setAutomata(Automata automata) {
        this.automata = automata;
    }

    public void setJLabelTituloText(String label) {
        this.jLabelTitulo.setText(label);
    }

    public void setJTextReGexString(String jTextReGex) {
        this.jTextReGex.setText(jTextReGex);
    }

    public void setJTextAlphaString(String jTextAlpha) {
        this.jTextAlpha.setText(jTextAlpha);
    }
    
    public void setJTextValidation(String jText) {
        this.jTextValidation.setText(jText);
    }
    
    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getImgDir() {
        return imgDir;
    }

    public void setImgDir(String imgDir) {
        this.imgDir = imgDir;
    }

    public Simulacion getSimulacion() {
        return simulacion;
    }

    public void setSimulacion(Simulacion simulacion) {
        this.simulacion = simulacion;
    }

    public String getGraphvizPath() {
        return graphvizbin;
    }

    public void setGraphvizPath(String graphvizPath) {
        this.graphvizbin = graphvizPath;
    }
    
    
/**
 * 
 * TODO EL CÓDIGO GENERADO DE LA CONFIGURACIÓN VISUAL
 * 
 */    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitulo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextReGex = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextAlpha = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextValidation = new javax.swing.JTextField();
        jButtonClose = new javax.swing.JButton();
        jButtonSimular = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jScrollPane1 = new JScrollPane(this.ScrollPaneConstructor());
        ;
        jButtonPrev = new javax.swing.JButton();
        jTextFieldOutput = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("AfGen - Gráfico del Automata");

        jLabelTitulo.setFont(new java.awt.Font("Dialog", 1, 18));
        jLabelTitulo.setForeground(new java.awt.Color(100, 100, 255));
        jLabelTitulo.setText("Gráfico del Automata");

        jLabel2.setText("Expresión Regular:");

        jTextReGex.setBackground(new java.awt.Color(255, 255, 153));
        jTextReGex.setEditable(false);

        jLabel4.setText("Cadena de Prueba:");

        jTextAlpha.setBackground(new java.awt.Color(204, 255, 153));
        jTextAlpha.setEditable(false);

        jLabel3.setText("Alfabeto:");

        jTextValidation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextValidationKeyReleased(evt);
            }
        });

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jButtonSimular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/media-playback-start.png"))); // NOI18N
        jButtonSimular.setText("Simular");
        jButtonSimular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSimularActionPerformed(evt);
            }
        });

        jButtonNext.setText("Next");
        jButtonNext.setEnabled(false);
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Graph"));

        jButtonPrev.setText("Prev");
        jButtonPrev.setEnabled(false);
        jButtonPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevActionPerformed(evt);
            }
        });

        jTextFieldOutput.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelTitulo)
                        .addGap(216, 216, 216))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonClose)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButtonSimular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonPrev)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonNext))
                            .addComponent(jTextValidation, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextReGex, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(1, 1, 1)
                                .addComponent(jTextAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldOutput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextReGex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextValidation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClose)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonPrev)
                    .addComponent(jButtonSimular)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jTextFieldOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

/**
 * Handle de la acción que inicia el evento de simulación. 
 * @param evt
 */
private void jButtonSimularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSimularActionPerformed
    this.iniciarSimulacion();
}//GEN-LAST:event_jButtonSimularActionPerformed

private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
   // siguiente Paso de la simulación
    this.nextStep();
}//GEN-LAST:event_jButtonNextActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.setVisible(false);
    
   /* File imageIcon = new File(this.imgUrl);
    if (this.library.compareTo("graphviz")==0 && imageIcon.exists()) {
        if (imageIcon.exists()) {
            imageIcon.delete();
        }
    }    */
    
    this.borrarArchivos(true);
    this.dispose();  
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jTextValidationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextValidationKeyReleased
    this.jButtonSimular.setText("Simular");
    this.jButtonSimular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/media-playback-start.png"))); // NOI18N
    this.fileList = new ArrayList<File>();
}//GEN-LAST:event_jTextValidationKeyReleased

private void jButtonPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevActionPerformed
    this.prevStep();
}//GEN-LAST:event_jButtonPrevActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrev;
    private javax.swing.JButton jButtonSimular;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextAlpha;
    private javax.swing.JTextField jTextFieldOutput;
    private javax.swing.JTextField jTextReGex;
    private javax.swing.JTextField jTextValidation;
    // End of variables declaration//GEN-END:variables
 
}