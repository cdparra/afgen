/*
 * Main.java
 *
 * Created on 10 de noviembre de 2008, 10:50 PM
 */

package app;

import afgenjava.*;
import exceptions.*;
import graphviz.GraphViz;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;
import traductor.*;

/**
 *
 * Ventana principal de la aplicación de generación de Automatas Finitos para 
 * expresiones regulares. <br><br>
 * 
 * @author  Cristhian Parra ({@link cdparra@gmail.com})
 * @author  Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class Main extends javax.swing.JFrame {
    
    
    /**
     * Variables utilizadas
     */
    /**
     * Configuración de la aplicación utilizada en todo el programa
     */
    private Configuracion conf;
    /**
     * Automata finito no determinista de la expresión procesada
     */
    private Automata AFN;
    /**
     * Automata finito determinista de la expresión procesada
     */
    private Automata AFD;
    
    /**
     * Automata finito determinista mínimo de la expresión procesada
     */
    private Automata AFDMin;
    
    /**
     * Tabla de Transiciones de cada automata
     */
    private Dtrans DT_AFN;
    private Dtrans DT_AFD;
    private Dtrans DT_AFDMin;
    
    /**
     * Clases de simulación de automatas para cada tipo
     */
    private Simulacion afnSim;
    private Simulacion afdSim;
    private Simulacion afdMinSim;
    
    /**
     * Banderas de indicación del resultado de la validación
     */
    private boolean afnSimResult;
    private boolean afdSimResult;
    private boolean afdMinSimResult;
    
    /**
     * Ventana de about
     */
    private JFrame about;
    
    /**
     * Ventana de configuración
     */
    private JFrame config;
    
    /**
     * Ventana de dibujo y simulación
     */
    private AutomataGrafico graphics;
    
    /**
    * Implementación de procedimientos que hacen la lógica del Programa completo
    */

    /**
     * Método que verifica el campo de ingreso de la expresión regular, 
     * y habilita los controles relacionados si este texto no està vacío. 
     */
    public void checkRegEx() {
            String regex = jTextReGex.getText();
            if (regex.compareTo("") != 0) {
                this.habilitarRegExProcess();
            } else {
                this.bloquearRegExProcess();
            }        
    }
    
    /**
     * Llamada al proceso de validación de una cadena de entrada haciendo uso 
     * del AFN
     * @return boolean: True si la cadena pertenece al lenguaje definido por la expresión <br>
     * False si no pertenece al lenguaje
     */
    public boolean validarAFN() {
        this.setAfnSim(new Simulacion(this.jTextValidate.getText(), this.AFN));        
        this.jTabbedPaneTables.setSelectedIndex(0);
        return this.getAfnSim().validar();
    }
        
    /**
     * Llamada al proceso de validación de una cadena de entrada haciendo uso 
     * del AFD
     * 
     * @return boolean: True si la cadena pertenece al lenguaje definido por la expresión <br>
     * False si no pertenece al lenguaje
     */
    public boolean validarAFD() {
        this.setAfdSim(new Simulacion(this.jTextValidate.getText(), this.AFD));        
        boolean exito = this.getAfdSim().validar();     
        this.cambiarColorCeldaFinal(this.AFD,exito);
        return exito;
    }
    
    /**
     * Llamada al proceso de validación de una cadena de entrada haciendo uso 
     * del AFDMin
     * 
     * @return boolean: True si la cadena pertenece al lenguaje definido por la expresión <br>
     * False si no pertenece al lenguaje
     */
    public boolean validarAFDMin() {
        this.setAfdMinSim(new Simulacion(this.jTextValidate.getText(), this.AFDMin));        
        boolean exito = this.getAfdMinSim().validar();     
        this.cambiarColorCeldaFinal(this.AFDMin,exito);
        return exito;
    }
    
    /**
     * Copia el Alfabeto seleccionado al jTextField correspondiente
     */
    public void copyDefaultAlpha() {
        String defaultAlpha = (String) this.jListDefaultAlphas.getSelectedValue();

        String az = "abcdefghijklmnñopqrstuvwxyz";
        String numbers = "0123456789";
        String binario = "01";
        String vocales = "aeiou";
        String all = az + az.toUpperCase() + numbers;

        if (defaultAlpha.compareTo("[a-z]") == 0) {
            this.jTextAlpha.setText(az);
        } else if (defaultAlpha.compareTo("[A-Z]") == 0) {
            this.jTextAlpha.setText(az.toUpperCase());
        } else if (defaultAlpha.compareTo("[a-zA-Z]") == 0) {
            this.jTextAlpha.setText(az + az.toUpperCase());
        } else if (defaultAlpha.compareTo("[0-9]") == 0) {
            this.jTextAlpha.setText(numbers);
        } else if (defaultAlpha.compareTo("[0-1]") == 0) {
            this.jTextAlpha.setText(binario);
        } else if (defaultAlpha.compareTo("[vocals]") == 0) {
            this.jTextAlpha.setText(vocales);
        } else if (defaultAlpha.compareTo("[ALL]") == 0) {
            this.jTextAlpha.setText(all);
        } 
    }
    
    /**
     * 
     * Método principal que se encarga de procesar la entrada para generar los 
     * Automatas deseados. 
     * 
     */
    public void procesarRegEx() {


        String regex = jTextReGex.getText();
        String alpha = jTextAlpha.getText();
        boolean Errors = false;

        // Check entries
        if (regex.compareTo("") == 0) {
            jTextAreaOutput.append("# --> No se introdujo ninguna expresión regular\n");
            jTextAreaOutput.append("# <--------------------------------------------\n");
            Errors = true;
        } else if (alpha.compareTo("") == 0) {
            jTextAreaOutput.append("# --> No se introdujo ningún alfabeto\n");
            jTextAreaOutput.append("# <-------------------------------------------\n");
            Errors = true;
        } else {

            this.bloquearControles();

            // Procesar la expresión regular y Generar el AFN, el AFD y el AFDMínimos        
            jTextAreaOutput.append("# --> Generando el AFN...\n");
            Analizador traductor = new Analizador(regex, alpha);

            Errors = traductor.isHayErrores();
            // 1. Generar el AFN
            if (!Errors) {
                this.setAFN(traductor.traducir());
            }

            Errors = traductor.isHayErrores();
            
            // 1.2. Verificar si hubieron errores. 
            if (Errors) {
                jTextAreaOutput.append("# ERRORS: --> " + traductor.getErrMsg() + "\n");
                jTextAreaOutput.append("# <-------------------------------------------\n");
            } else {

                jTextAreaOutput.append("# --> AFN Generado con éxito!\n");

                // 2. Generar el AFD            
                jTextAreaOutput.append("# --> Generando el AFD...\n");
                AlgSubconjuntos algSub;
                Dtrans dtran;

                try {
                    algSub = new AlgSubconjuntos(this.AFN);
                    dtran = algSub.ejecutar();                    
                    this.AFD = dtran.convertAutomata();
                    
                    this.AFD = AlgSubconjuntos.eliminar_estados_inalcanzables(this.AFD);
                    
                    this.AFD.setAlpha(this.AFN.getAlpha());
                    this.AFD.setRegex(this.jTextReGex.getText());
                    this.AFD.setTipo(TipoAutomata.AFD);
                    
                    
                } catch (AutomataException ex) {
                    jTextAreaOutput.append("# ERRORS: --> " + ex.getMessage() + "\n");
                    jTextAreaOutput.append("# <-------------------------------------------\n");
                    Errors = true;
                } catch (Exception ex) {
                    jTextAreaOutput.append("# ERRORS: --> " + ex.getMessage() + "\n");
                    jTextAreaOutput.append("# <-------------------------------------------\n");
                    Errors = true;
                }

                if (!Errors) {
                    try {
                        jTextAreaOutput.append("# --> AFD Generado con éxito!\n");

                        // 3. Generar el AFDMínimo
                        jTextAreaOutput.append("# --> Generando el AFD Mínimo...\n");
                        
                        AlgMinimizacion minimize = new AlgMinimizacion(this.AFD);
                        this.AFDMin = minimize.minimizar();
                        this.AFDMin.eliminar_estados_muertos();
                        
                        this.AFDMin.setAlpha(this.AFN.getAlpha());
                        this.AFDMin.setRegex(this.jTextReGex.getText());
                        this.AFDMin.setTipo(TipoAutomata.AFDMin);

                        // 4. Poblar las tablas de la ventana principal
                        this.cargarTabla(jTableAFN, this.AFN);
                        this.cargarTabla(jTableAFD, this.AFD);
                        this.cargarTabla(jTableAFDMin, this.AFDMin);
                    } catch (AutomataException ex) {
                        jTextAreaOutput.append("# ERRORS: --> " + ex.getMessage() + "\n");
                        jTextAreaOutput.append("# <-------------------------------------------\n");
                        Errors = true;
                    } catch (Exception ex) {
                        jTextAreaOutput.append("# ERRORS: --> " + ex.getMessage() + "\n");
                        jTextAreaOutput.append("# <-------------------------------------------\n");
                        Errors = true;
                    }
                }
            }

            this.habilitarControles();

            if (Errors) {
                this.bloquearValidacion();
                this.bloquearVistas();
            }
        }
    }

    /**
     * Método que carga la tabla correspondiente con las transiciones del 
     * automata
     * @param Tabla compoente de tipo Jtable que se cargará
     * @param automata origen de los datos. 
     */
    public void cargarTabla(JTable Tabla, Automata automata) {
        AutomataTable tmodel = new AutomataTable(automata);
        tmodel.arreglarObjetosNulos();                
        Tabla.setModel(tmodel); 
        this.resetTablaRenderer(Tabla);
    }
   
    /**
     * Método que se encarga de construir la ventana de dibujo y simulación
     * @param automata
     */
    private void viewGraphics(Automata automata) {

        int toolSelected = jComboBoxGraph.getSelectedIndex();

        jTextAreaOutput.append("# --> Construyendo Imagen del Automata...\n");            
        
        if (toolSelected == 0) {
            jTextAreaOutput.append("# --> Debe seleccionar la herramienta para graficar\n");            
        } else if (toolSelected == 1) {               
            this.graphics = new AutomataGrafico("graphviz",automata,this.conf);
            this.graphics.setJTextAlphaString(jTextAlpha.getText());
            this.graphics.setJTextReGexString(jTextReGex.getText());
            this.graphics.setJTextValidation(jTextValidate.getText());            
            this.cargarSimulacion(automata);
            this.graphics.setVisible(true);
            this.graphics.toFront();            
        } else {
            this.graphics = new AutomataGrafico("jgraph",automata,this.conf);
            this.graphics.setAutomata(automata);
            this.graphics.setJTextAlphaString(jTextAlpha.getText());
            this.graphics.setJTextReGexString(jTextReGex.getText());
            this.graphics.setJTextValidation(jTextValidate.getText());
            this.cargarSimulacion(automata);
            this.graphics.setVisible(true);
            this.graphics.toFront();
        }
    }
     
    /**
     * 
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
   
    /**
     * Funciones auxiliares
     */
    
    /** Creates new form Main */
    public Main() {
        initComponents();        
        this.readConf();
    }

    private void bloquearControles() {
        this.bloquearValidacion();
        this.bloquearRegEx();
        this.bloquearRegExProcess();
        this.bloquearVistas();
        this.bloqearAlpha();
        this.bloquearDefaultAlpha();
    }

    private void cambiarColorCeldaFinal(Automata a, boolean exito) {
        JTable tableACambiar = null;        // Tabla del AFN usado para la validación
        Color incorrectoBack = Color.red;   // Color de fondo que le ponemos a las celdas pintadas si la validación fue incorrecta
        Color correctoBack   = Color.green; // Color de fondo que le ponemos a las celdas pintadas si la validación fue correcta
        Color textColor      = Color.white; // Color del Texto en celdas seleccionadas
        Color preFinalBack   = Color.blue;
        Color finalBack      = exito ? correctoBack : incorrectoBack;
        AutomataTable model  = null;        // Modelo de la tabla a actualizar
        Estado estadoFinal   = null;        // Estado final al que llegó el proceso de validación
        Estado estadoPreFin  = null;        // Estado anterior al estado final en el proceso de validación
        int estadoFinalCol   = 0;           // Columna del estado final
        int estadoFinalFil   = 0;           // Fila del estado final
        int estadoPreFinCol  = 0;           // Columna del estado anterior final
        int estadoPreFinFil  = 0;           // Fila del estado anterior final
        Component Celda      = null;         // Componente que representa a la Celda de la tabla a modificar
        Object textValue     = null;
        int indexTable       = 0;
                
        String valText       = this.jTextValidate.getText();
        int valTextLength    = valText.length();
        
        String lastChar      = "";
        if (valTextLength > 0) {
            lastChar      = valText.charAt(valTextLength-1) + "";
        }
        
        if (a.getTipo() == TipoAutomata.AFN) {
            estadoFinal   = this.afnSim.getEstadoFinal();
            estadoPreFin  = this.afnSim.getEstadoPreFinal();
            tableACambiar = this.jTableAFN;
            indexTable    = 0;
        } else if (a.getTipo() == TipoAutomata.AFD) {
            estadoFinal   = this.afdSim.getEstadoFinal();
            estadoPreFin  = this.afdSim.getEstadoPreFinal();            
            tableACambiar = this.jTableAFD;
            indexTable    = 1;
        } else if (a.getTipo() == TipoAutomata.AFDMin) {
            estadoFinal   = this.afdMinSim.getEstadoFinal();
            estadoPreFin  = this.afdMinSim.getEstadoPreFinal();
            tableACambiar = this.jTableAFDMin;
            indexTable    = 2;
        } 
        
        model = (AutomataTable) tableACambiar.getModel();
        
        if (estadoFinal != null) {
            estadoFinalFil = estadoFinal.getId();            
        }
        estadoFinalCol = 0;
        
        if (estadoPreFin != null) {
            estadoPreFinFil = estadoPreFin.getId();            
        }
        
        if (this.jTextAlpha.getText().indexOf(lastChar) < 0) {
            estadoPreFinCol = 0;
        } else {
            estadoPreFinCol = model.findColumn(lastChar);
        }
        
        if (estadoPreFinCol < 0) {
            estadoPreFinCol = 0; 
        }
        // Seleccionar la fila del estado final
        tableACambiar.setRowSelectionInterval(estadoFinalFil, estadoFinalFil);
        
        // Pintar los estados notables
        OneCellRenderer cr2 = new OneCellRenderer(estadoPreFinFil, estadoPreFinCol, preFinalBack, textColor);        
        tableACambiar.getColumnModel().getColumn(estadoPreFinCol).setCellRenderer(cr2);
        
        OneCellRenderer cr = new OneCellRenderer(estadoFinalFil, estadoFinalCol, finalBack, textColor);        
        tableACambiar.getColumnModel().getColumn(estadoFinalCol).setCellRenderer(cr);   
        
        tableACambiar.setCellSelectionEnabled(true);
        this.jTabbedPaneTables.setSelectedIndex(indexTable);
    }

    private void cargarSimulacion(Automata automata) {
        Simulacion sim = null;
        boolean simResult = false;
        
        if (automata.getTipo() == TipoAutomata.AFN) {
            if (afnSim != null) {
                sim = afnSim;
                simResult = afnSimResult;
            }
        } else if (automata.getTipo() == TipoAutomata.AFD) {
            if (afdSim != null) {
                sim = afdSim;
                simResult = afdSimResult;
            }
        } else {
            if (afdMinSim != null) {
                sim = afdMinSim;
                simResult = afdMinSimResult;
            }
        }
        
        if (this.graphics != null) {
            this.graphics.setSimulacion(sim);
            this.graphics.setSimulacionResult(simResult);
        }
    }

    private void readConf() {
        try {
            Serializer serializer = new Persister();
            File source = new File("conf.xml");

            if (source.exists()) {
                this.setConf(serializer.read(Configuracion.class, source));
            } else {
                // Si la configuración no existe, creamos una nueva
                
                String dotP = "/usr/bin";
                String imgD = "/tmp";
                
                // si estamos en windows, cambiamos los directorios por defecto
                if (File.separator.compareTo("\\")==0) {
                
                    dotP = "C:\\Archivos de programa\\Graphviz 2.21\\bin\\d";
                    imgD = "C:\\";
                }
                
                this.setConf(new Configuracion(dotP, 1));
                this.getConf().setEmptySymbol("(vacio)");
                this.getConf().setImgdir(imgD);
                
                serializer.write(this.conf, source);
            }
            
            CONSTANS.setVacio(this.getConf().getEmptySymbol());
            GraphViz.setDot(this.getConf().getDotPath() + File.separator + "dot");
            GraphViz.setDir(this.getConf().getImgdir());

            
        } catch (Exception ex) {
            this.jTextAreaOutput.append("ERROR: Hubo algún problema con la configuración" + ex.getMessage() + "\n");
            this.jTextAreaOutput.append("# <--------------------------------------------\n");
        }
        
    }
   
    private void resetTablaRenderer(JTable Tabla) {
        Tabla.setBackground(Color.white);
        Tabla.setForeground(Color.black);
        
        DefaultTableCellRenderer dt = (DefaultTableCellRenderer) Tabla.getDefaultRenderer(String.class);
        dt.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        dt.setBackground(Color.white);
        dt.setForeground(Color.black);
                
        OneColumnRenderer cr = new OneColumnRenderer(0, Color.gray, Color.white);
        cr.setFont(new Font("Verdana",Font.BOLD, 12));
        Tabla.getColumnModel().getColumn(0).setCellRenderer(cr);
    }
    
    private void changeValidationTextResult(boolean SimResult) {
    
        Color incorrecto = Color.red;
        Color correcto   = Color.green;
       
        if (SimResult) {
            this.jTextValidateResult.setText("La cadena pertenece al Lenguaje");
            this.jTextValidateResult.setForeground(correcto);
        } else {
            this.jTextValidateResult.setText("La cadena NO pertenece al Lenguaje");
            this.jTextValidateResult.setForeground(incorrecto);
        }
    }
    
    private void habilitarControles() {
        this.habilitarValidacion();
        this.habilitarRegEx();
        this.habilitarRegExProcess();
        this.habilitarVistas();
        this.habilitarAlpha();
        this.habilitarDefaultAlpha();
    }

    private void habilitarRegEx() {
        this.jTextReGex.setEnabled(true);
    }
    
    private void bloquearRegEx() {
        this.jTextReGex.setEnabled(false);
    }
    
    private void habilitarRegExProcess() {        
        this.processBtn.setEnabled(true);        
        this.jMenuItemProcesarReGex.setEnabled(true);
    }
    
    private void bloquearRegExProcess() {        
        this.processBtn.setEnabled(false);        
        this.jMenuItemProcesarReGex.setEnabled(false);
    }
    
    private void habilitarAlpha() {
        this.jTextAlpha.setEnabled(true);
    }
    
    private void bloqearAlpha() {
        this.jTextAlpha.setEnabled(false);
    }
    
    private void habilitarVistas() {        
        this.viewAFNbtn.setEnabled(true);
        this.viewAFDMinbtn.setEnabled(true);
        this.viewAFDbtn.setEnabled(true);
        this.jComboBoxGraph.setEnabled(true);
    }
    
    private void bloquearVistas() {        
        this.viewAFNbtn.setEnabled(false);
        this.viewAFDMinbtn.setEnabled(false);
        this.viewAFDbtn.setEnabled(false);
        this.jComboBoxGraph.setEnabled(false);
    }    
    
    private void habilitarValidacion() {        
        this.jTextValidate.setEnabled(true);
        this.validateBtn.setEnabled(true);        
        this.jComboBoxValidation.setEnabled(true);
    }
    
    private void bloquearValidacion() {        
        this.jTextValidate.setEnabled(false);
        this.validateBtn.setEnabled(false);       
        this.jComboBoxValidation.setEnabled(false); 
        this.afnSim = null;
        this.afdSim = null;
        this.afdMinSim = null;
        
    }
    
    private void habilitarDefaultAlpha() {
        useSelectedAlphaBtn.setEnabled(true);
    }
    
    private void bloquearDefaultAlpha() {
        useSelectedAlphaBtn.setEnabled(false);
    }
  
    /**
     * SETTERS Y GETTERS de la aplicación principal 
     */
    
    
    public Automata getAFN() {
        return AFN;
    }

    public void setAFN(Automata AFN) {
        this.AFN = AFN;
    }

    public Automata getAFD() {
        return AFD;
    }

    public void setAFD(Automata AFD) {
        this.AFD = AFD;
    }

    public Automata getAFDMin() {
        return AFDMin;
    }

    public void setAFDMin(Automata AFDMin) {
        this.AFDMin = AFDMin;
    }

    public Dtrans getDT_AFN() {
        return DT_AFN;
    }

    public void setDT_AFN(Dtrans DT_AFN) {
        this.DT_AFN = DT_AFN;
    }

    public Dtrans getDT_AFD() {
        return DT_AFD;
    }

    public void setDT_AFD(Dtrans DT_AFD) {
        this.DT_AFD = DT_AFD;
    }

    public Dtrans getDT_AFDMin() {
        return DT_AFDMin;
    }

    public void setDT_AFDMin(Dtrans DT_AFDMin) {
        this.DT_AFDMin = DT_AFDMin;
    }

    public boolean isAfnSimResult() {
        return afnSimResult;
    }

    public boolean isAfdSimResult() {
        return afdSimResult;
    }

    public boolean isAfdMinSimResult() {
        return afdMinSimResult;
    }

    public Simulacion getAfnSim() {
        return afnSim;
    }

    public void setAfnSim(Simulacion afnSim) {
        this.afnSim = afnSim;
    }

    public Simulacion getAfdSim() {
        return afdSim;
    }

    public void setAfdSim(Simulacion afdSim) {
        this.afdSim = afdSim;
    }

    public Simulacion getAfdMinSim() {
        return afdMinSim;
    }

    public void setAfdMinSim(Simulacion afdMinSim) {
        this.afdMinSim = afdMinSim;
    }

    public Configuracion getConf() {
        return conf;
    }

    public void setConf(Configuracion conf) {
        this.conf = conf;
        CONSTANS.setVacio(this.getConf().getEmptySymbol());
        GraphViz.setDot(this.getConf().getDotPath() + File.separator + "dot");
        GraphViz.setDir(this.getConf().getImgdir());
    }
    
    
    /**
     * Programación de los componentes gráficos y eventos
     */
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextReGex = new javax.swing.JTextField();
        jLabelReGex = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextAlpha = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListDefaultAlphas = new javax.swing.JList();
        processBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        viewAFNbtn = new javax.swing.JButton();
        viewAFDbtn = new javax.swing.JButton();
        viewAFDMinbtn = new javax.swing.JButton();
        jComboBoxGraph = new javax.swing.JComboBox();
        useSelectedAlphaBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTabbedPaneTables = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableAFN = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableAFD = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAFDMin = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaOutput = new javax.swing.JTextArea();
        cleanBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextValidate = new javax.swing.JTextField();
        validateBtn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemNewRegex = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jComboBoxValidation = new javax.swing.JComboBox();
        jLabelValidationResult = new javax.swing.JLabel();
        jTextValidateResult = new javax.swing.JTextField();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemNewRegex1 = new javax.swing.JMenuItem();
        jMenuItemConf = new javax.swing.JMenuItem();
        jMenuItemProcesarReGex = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ReGex Automaton Generator");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(153, 153, 153));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Definiciones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(51, 0, 102))); // NOI18N

        jTextReGex.setToolTipText("Introduzca su expresión regular Aquí");
        jTextReGex.setAutoscrolls(false);
        jTextReGex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextReGexActionPerformed(evt);
            }
        });
        jTextReGex.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextReGexKeyReleased(evt);
            }
        });

        jLabelReGex.setLabelFor(jTextReGex);
        jLabelReGex.setText("Expresión Regular");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setLabelFor(jTextAlpha);
        jLabel1.setText("Alfabeto");

        jListDefaultAlphas.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "[a-z]", "[A-Z]", "[0-9]", "[a-zA-Z]", "[0-1]", "[vocals]", "[ALL]" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListDefaultAlphas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListDefaultAlphas.setValueIsAdjusting(true);
        jListDefaultAlphas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListDefaultAlphasMouseClicked(evt);
            }
        });
        jListDefaultAlphas.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListDefaultAlphasValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListDefaultAlphas);

        processBtn.setText("Procesar");
        processBtn.setEnabled(false);
        processBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processBtnActionPerformed(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gráficos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        viewAFNbtn.setText("AFN");
        viewAFNbtn.setEnabled(false);
        viewAFNbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAFNbtnActionPerformed(evt);
            }
        });

        viewAFDbtn.setText("AFD");
        viewAFDbtn.setEnabled(false);
        viewAFDbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAFDbtnActionPerformed(evt);
            }
        });

        viewAFDMinbtn.setText("AFDMin");
        viewAFDMinbtn.setEnabled(false);
        viewAFDMinbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAFDMinbtnActionPerformed(evt);
            }
        });

        jComboBoxGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Graficar con...", "GraphViz", "jGraph" }));
        jComboBoxGraph.setEnabled(false);
        jComboBoxGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGraphActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(viewAFNbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewAFDbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewAFDMinbtn))
                    .addComponent(jComboBoxGraph, javax.swing.GroupLayout.Alignment.TRAILING, 0, 214, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewAFNbtn)
                    .addComponent(viewAFDbtn)
                    .addComponent(viewAFDMinbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jComboBoxGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        useSelectedAlphaBtn.setText("Usar Alfabeto");
        useSelectedAlphaBtn.setEnabled(false);
        useSelectedAlphaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSelectedAlphaBtnActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/system-log-out.png"))); // NOI18N
        jButton1.setText("Salir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(useSelectedAlphaBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(93, 93, 93)
                                .addComponent(processBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelReGex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextReGex, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelReGex)
                    .addComponent(jTextReGex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(processBtn))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(useSelectedAlphaBtn)
                            .addComponent(jButton1))))
                .addContainerGap())
        );

        jTabbedPaneTables.setBorder(javax.swing.BorderFactory.createTitledBorder("Tablas de Transición"));
        jTabbedPaneTables.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPaneTables.setAutoscrolls(true);

        jPanel2.setAutoscrolls(true);

        jTableAFN.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableAFN.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(jTableAFN);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );

        jTabbedPaneTables.addTab("AFN", jPanel2);

        jTableAFD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableAFD.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(jTableAFD);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );

        jTabbedPaneTables.addTab("AFD", jPanel3);

        jTableAFDMin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableAFDMin.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(jTableAFDMin);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );

        jTabbedPaneTables.addTab("AFDMin", jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 904, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Salida Textual"));

        jScrollPane5.setBackground(new java.awt.Color(255, 255, 204));

        jTextAreaOutput.setBackground(new java.awt.Color(255, 255, 204));
        jTextAreaOutput.setColumns(20);
        jTextAreaOutput.setEditable(false);
        jTextAreaOutput.setRows(5);
        jScrollPane5.setViewportView(jTextAreaOutput);

        cleanBtn.setText("Clean");
        cleanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(cleanBtn)
                .addContainerGap(102, Short.MAX_VALUE))
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cleanBtn)
                .addContainerGap())
        );

        jLabel2.setLabelFor(jTextValidate);
        jLabel2.setText("Texto de Validación");

        jTextValidate.setEnabled(false);

        validateBtn.setText("Validar");
        validateBtn.setEnabled(false);
        validateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateBtnActionPerformed(evt);
            }
        });

        jMenu1.setText("Acciones");

        jMenuItemNewRegex.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNewRegex.setIcon(new javax.swing.ImageIcon("/home/cparra/Projects/afgen/afgenjava/src/img/16x16/window-new.png")); // NOI18N
        jMenuItemNewRegex.setText("Nuevo...");
        jMenuItemNewRegex.setToolTipText("Ingresar una nueva Expresión regular");
        jMenuItemNewRegex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewRegexActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemNewRegex);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon("/home/cparra/Projects/afgen/afgenjava/src/img/16x16/button_ok.png")); // NOI18N
        jMenuItem1.setText("Generar Autómata");
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon("/home/cparra/Projects/afgen/afgenjava/src/img/16x16/system-log-out.png")); // NOI18N
        jMenuItem2.setText("Salir");
        jMenuItem2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem2MouseClicked(evt);
            }
        });
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ayuda");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem3.setIcon(new javax.swing.ImageIcon("/home/cparra/Projects/afgen/afgenjava/src/img/16x16/help.png")); // NOI18N
        jMenuItem3.setText("About...");
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jComboBoxValidation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Con...", "AFN", "AFD", "AFD Mínimo" }));
        jComboBoxValidation.setToolTipText("Seleccione con que Automata Validar");
        jComboBoxValidation.setEnabled(false);

        jLabelValidationResult.setText("Resultado de Validación");

        jTextValidateResult.setBackground(new java.awt.Color(255, 255, 153));
        jTextValidateResult.setEditable(false);
        jTextValidateResult.setFont(new java.awt.Font("Dialog", 1, 12));

        jMenu3.setText("Acciones");

        jMenuItemNewRegex1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNewRegex1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/window-new.png"))); // NOI18N
        jMenuItemNewRegex1.setText("Nuevo...");
        jMenuItemNewRegex1.setToolTipText("Ingresar una nueva Expresión regular");
        jMenuItemNewRegex1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewRegexActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemNewRegex1);

        jMenuItemConf.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemConf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/edit-clear.png"))); // NOI18N
        jMenuItemConf.setText("Configuraciones");
        jMenuItemConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConfActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemConf);

        jMenuItemProcesarReGex.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemProcesarReGex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/button_ok.png"))); // NOI18N
        jMenuItemProcesarReGex.setText("Generar Autómata");
        jMenuItemProcesarReGex.setEnabled(false);
        jMenuItemProcesarReGex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProcesarReGexActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemProcesarReGex);
        jMenu3.add(jSeparator2);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setIcon(new javax.swing.ImageIcon("/home/cparra/Projects/afgen/afgenjava/src/img/16x16/system-log-out.png")); // NOI18N
        jMenuItem5.setText("Salir");
        jMenuItem5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem2MouseClicked(evt);
            }
        });
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Ayuda");

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/16x16/help.png"))); // NOI18N
        jMenuItem6.setText("About...");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuBar2.add(jMenu4);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTabbedPaneTables, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                                .addGap(5, 5, 5))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabelValidationResult))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextValidateResult, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxValidation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextValidate, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(validateBtn)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPaneTables, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(validateBtn)
                            .addComponent(jLabel2)
                            .addComponent(jTextValidate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxValidation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelValidationResult)
                            .addComponent(jTextValidateResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem2MouseClicked
    System.exit(0);
}//GEN-LAST:event_jMenuItem2MouseClicked

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jMenuItemNewRegexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewRegexActionPerformed
    this.jTextReGex.setText("");
    this.jTextAlpha.setText("");
    this.bloquearRegExProcess();
    this.bloquearValidacion();
    this.bloquearVistas();
}//GEN-LAST:event_jMenuItemNewRegexActionPerformed

private void processBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processBtnActionPerformed
    this.procesarRegEx();
}//GEN-LAST:event_processBtnActionPerformed

private void jListDefaultAlphasValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListDefaultAlphasValueChanged
    useSelectedAlphaBtn.setEnabled(true);   
}//GEN-LAST:event_jListDefaultAlphasValueChanged

private void useSelectedAlphaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSelectedAlphaBtnActionPerformed
    this.copyDefaultAlpha();
}//GEN-LAST:event_useSelectedAlphaBtnActionPerformed

private void cleanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanBtnActionPerformed
    jTextAreaOutput.setText("");
}//GEN-LAST:event_cleanBtnActionPerformed

private void jMenuItemProcesarReGexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProcesarReGexActionPerformed
    this.processBtnActionPerformed(evt);
}//GEN-LAST:event_jMenuItemProcesarReGexActionPerformed

private void jListDefaultAlphasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListDefaultAlphasMouseClicked
    
    // Revisamos si se hizo doble-click    
    if (evt.getClickCount() == 2) {
        this.copyDefaultAlpha();
    }
}//GEN-LAST:event_jListDefaultAlphasMouseClicked

private void jTextReGexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextReGexActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextReGexActionPerformed

private void jTextReGexKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextReGexKeyReleased
    this.checkRegEx();
}//GEN-LAST:event_jTextReGexKeyReleased

private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
    
    if (about == null) {
        about = new About();
    }
    
    about.setEnabled(true);
    about.setVisible(true);
}//GEN-LAST:event_jMenuItem6ActionPerformed

private void jMenuItemConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfActionPerformed

    if (this.config == null) {
            try {
                this.config = new jFrameConf(this.conf);
            } catch (Exception ex) {
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                this.jTextAreaOutput.append("ERROR: Hubo algún problema con la configuración"+ex.getMessage()+"\n");
                this.jTextAreaOutput.append("# <--------------------------------------------\n");
            }
    }
    
    this.config.setEnabled(true);
    this.config.setVisible(true);
}//GEN-LAST:event_jMenuItemConfActionPerformed

private void validateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateBtnActionPerformed
    
    this.resetTablaRenderer(jTableAFD);
    this.resetTablaRenderer(jTableAFN);
    this.resetTablaRenderer(jTableAFDMin);
    
    
    switch(this.jComboBoxValidation.getSelectedIndex()) {
        case 1: this.afnSimResult = this.validarAFN();
            this.changeValidationTextResult(this.afnSimResult);
            break;
        case 2: this.afdSimResult = this.validarAFD();
            this.changeValidationTextResult(this.afdSimResult);        
            break;
        case 3: this.afdMinSimResult = this.validarAFDMin();
            this.changeValidationTextResult(this.afdMinSimResult);        
            break;
        default:
            jTextAreaOutput.append("# --> Seleccione con que Automata desea Validar!\n");            
    }
}//GEN-LAST:event_validateBtnActionPerformed

private void viewAFDbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAFDbtnActionPerformed
    this.viewGraphics(this.AFD);
}//GEN-LAST:event_viewAFDbtnActionPerformed

private void viewAFNbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAFNbtnActionPerformed
    this.viewGraphics(this.AFN);
}//GEN-LAST:event_viewAFNbtnActionPerformed

private void viewAFDMinbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAFDMinbtnActionPerformed
    this.viewGraphics(this.AFDMin);
}//GEN-LAST:event_viewAFDMinbtnActionPerformed

private void jComboBoxGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGraphActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jComboBoxGraphActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cleanBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBoxGraph;
    private javax.swing.JComboBox jComboBoxValidation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelReGex;
    private javax.swing.JLabel jLabelValidationResult;
    private javax.swing.JList jListDefaultAlphas;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItemConf;
    private javax.swing.JMenuItem jMenuItemNewRegex;
    private javax.swing.JMenuItem jMenuItemNewRegex1;
    private javax.swing.JMenuItem jMenuItemProcesarReGex;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPaneTables;
    private javax.swing.JTable jTableAFD;
    private javax.swing.JTable jTableAFDMin;
    private javax.swing.JTable jTableAFN;
    private javax.swing.JTextField jTextAlpha;
    private javax.swing.JTextArea jTextAreaOutput;
    private javax.swing.JTextField jTextReGex;
    private javax.swing.JTextField jTextValidate;
    private javax.swing.JTextField jTextValidateResult;
    private javax.swing.JButton processBtn;
    private javax.swing.JButton useSelectedAlphaBtn;
    private javax.swing.JButton validateBtn;
    private javax.swing.JButton viewAFDMinbtn;
    private javax.swing.JButton viewAFDbtn;
    private javax.swing.JButton viewAFNbtn;
    // End of variables declaration//GEN-END:variables
}
