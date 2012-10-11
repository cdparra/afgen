/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Clase que implementa el coloreo de una celda en particular
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
public class OneCellRenderer extends DefaultTableCellRenderer {

    private int fila;
    private int columna;
    private Color background;
    private Color foreground;

    public OneCellRenderer() {
        this.fila = 0;
        this.columna = 0;
        this.background = Color.white;
        this.foreground = Color.black;

    }

    public OneCellRenderer(int filaFin, int columnaFin, Color b, Color f) {
        this.fila = filaFin;
        this.columna = columnaFin;
        this.background = b;
        this.foreground = f;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        setEnabled(table == null || table.isEnabled()); // see question above

        if ((row == this.fila) && (column == this.columna)) {
            setBackground(this.background);
            setForeground(this.foreground);
            setFont(new Font("Verdana",Font.BOLD,12));
        } else if (column == 0) {
            setBackground(Color.gray);
            setForeground(Color.white);            
            setFont(new Font("Verdana",Font.BOLD,12));
        } else {
            setBackground(Color.white);
            setForeground(Color.black);            
        }
        

        setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);

        return this;
    }
}
