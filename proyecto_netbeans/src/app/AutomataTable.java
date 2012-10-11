package app;

import afgenjava.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

/**
 * Tabla de Transiciones de un automata ajustado para ser utilizado como el 
 * modelo de un componente Jtable
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class AutomataTable extends AbstractTableModel {

    /**
     * Automata cuya tabla de tranciciones se quiere representar
     */
    private Automata automata;
    
    /**
     * Cantidad de columnas de la Tabla
     */
    private int columnCount = 0;
    
    /**
     * Cantidad de filas de la tabla
     */
    private int rowCount = 0;
    
    /**
     * Matriz que contiene los data a desplegar en el JTable. 
     */
    private Object [][] data;
    
    /**
     * Etiqueta de cada columna de la tabla
     */
    private String [] columnsName;
    
    /**
     * Cuenta de simbolos del alfabeto que realmente se utilizan
     */
    private int columnRealCount;

    public AutomataTable(Automata automata) {
        this.automata       = automata;        
        
        if (automata != null) {
            this.columnCount = this.automata.getAlpha().size()+1; // La primera columna es de las etiquetas de estado

            // Si es AFN, se debe tener entre los elementos del alfabeto al vacio
            if (this.automata.getTipo() == TipoAutomata.AFN) {
                this.columnCount++;
                this.columnRealCount++;
            }

            this.rowCount = this.automata.getEstados().size();
            this.columnsName = new String[this.columnCount]; 
            this.data = new Object[this.rowCount][this.columnCount];
            this.columnRealCount = 0;

            // Si es AFN, se debe tener entre los elementos del alfabeto al vacio
            if (this.automata.getTipo() == TipoAutomata.AFN) {
                this.columnsName[1] = CONSTANS.getVacio();
                this.columnsName[0] = "Estado";
            }

            this.loadTable();
        }
    }
    
    /**
     * Constructor principal de la clase 
     * @param col Cantidad de columnas de la tabla
     * @param fil Cantidad de filas de la tabla
     */
    public AutomataTable(int fil, int col) {
        this.rowCount       = fil;
        this.columnCount    = col;
        this.columnsName    = new String[col];
        this.data           = new Object[fil][col];
    }
    
    /**
     * Obtener la cantidad de columnas de la tabla
     * @return Cantidad de columnas de la tabla
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Obtener la cantidad de filas de la tabla.
     * @return Cantidad de filas de la tabla
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Obtener el nombre de una de las columnas de la tabla
     * @param col Número de columna cuyo nombre desea obtenerse.
     * @return El nombre de la columna.
     */
    @Override
    public String getColumnName(int col) {
        return this.columnsName[col];
    }
    
    /**
     * Establecer el nombre de una columna de la Tabla
     * @param col Número de columna de la Tabla cuyo nombre desea establecerse.
     * @param nombre Nombre de la columna.
     */
    public void setColumnName(int col, String nombre) {
        this.columnsName[col] = nombre;
    }

    /**
     * Obtener un valor almacenado en la Tabla.
     * @param row Número de fila de la Tabla.
     * @param col Número de columna de la Tabla.
     * @return Objeto almacenado en las posiciones [row,col]
     */
    public Object getValueAt(int row, int col) {
        return this.data[row][col];
    }
    
    /**
     * Establecer un valor en la Tabla.
     * @param value Valor a almacenar en la Tabla en la posici�n [row,col].
     * @param row Número de columna en la Tabla.
     * @param col Número de columna en la Tabla.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        this.data[row][col] = value;
        this.fireTableCellUpdated(row, col);
    }

    /**
     * Determinar el renderizador por defecto para cada celda.
     * @param c Número de columna cuyo tipo de Clase se quiere conocer.
     * @return Class de la columna en cuesti�n.
     */
    @Override
    public Class getColumnClass(int c) {
        return this.getValueAt(0, c).getClass();
    }
    
    /**
     * Arreglar las posiciones de la Tabla donde no se estableció ningún valor
     * (tiene objetos null). Los objetos null se reemplazan por Strings Vacíos
     */
    public void arreglarObjetosNulos() {
        String vacio = " ";
        for (int i = 0; i < this.rowCount; i++) {
            for (int j = 0; j < this.columnCount; j++) {
                Object o = this.data[i][j];
                if (o == null) {
                    this.setValueAt(vacio, i, j);
                }
            }
        }
    }

    public

    /**
     * Automata cuya tabla de tranciciones se quiere representar
     */
    Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
    }

    private void loadTable() {
        
        // Recorremos el automata estado a estado y en cada paso, cargamos la 
        // tabla en el índice que corresponde a la columna y fila del par 
        // etiqueta, estado procesado
        for (Iterator<Estado> it = this.automata.getEstados().getIterator(); it.hasNext();) {
            
            Estado current = it.next();                     // Obtenemos el estadoa actual a procesar
            ListaEnlaces enlaces = current.getEnlaces();    // Obtenemos sus enlaces            
            int rowEstado = current.getId();                // La fila del estado es igual a su id
            
            String estadoLabel = rowEstado+"";
            if (current.isEstadoinicial()) {
                estadoLabel+="(ini)";
            }
            
            if (current.isEstadofinal()) {
                estadoLabel+="(fin)";
            }
            
            this.setValueAt(estadoLabel,rowEstado, 0);
            
            // Iteramos sobre los enlaces para agregar los destinos en las celdas
            // adecuadas de la matriz
            for (Iterator<Enlace> ite = enlaces.getIterator(); ite.hasNext();) {
                
                Enlace currentLink = ite.next();            // enlace actual a procesar                
                String symbol = currentLink.getEtiqueta();  // simbolo del enlace                
                int indexCol = this.findColumn(symbol);     // obtenemos la columna de la etiqueta
                
                // Si la columna obtenida es -1, todavía no se cargó 
                // esta etiqueta al encabezado
                if (indexCol < 0) {                    
                   indexCol = this.columnRealCount+1;
                   this.columnsName[indexCol] = symbol;
                   this.columnRealCount++;
                   
                }
                
                Estado destino = currentLink.getDestino();  // obtenemos el destino de esta enlace
                
                // vemos si para para este estado,simbolo ya habían destinos asociados
                Object estados = this.getValueAt(rowEstado, indexCol);
                
                if (estados == null) {
                    estados = new ListaEstados();
                }
                
                ((ListaEstados) estados).add(destino);                       // agregamos el nuevo destino a la lista
                
                // Cargamos la lista de nuevo en la matriz de objetos                
                this.setValueAt(estados,rowEstado, indexCol);
            }            
        }        
    }    
    
    @Override
    public String toString() {
        String result="";
        
        for (int i = 0; i < data.length; i++) {
            Object[] objects = data[i]; 
            for (int j = 0; j < objects.length; j++) {
                Object object = objects[j];    
                if (object != null) {
                    result = result + ( (ListaEstados) object).toString()+"\t";
                } else {
                    result = result + "null\t";
                }
            }
            result += "\n";
        }
        
        return result; 
    }
}
