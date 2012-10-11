/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import org.simpleframework.xml.*;

/**
 * Clase que implementa el modelo de la configuración del sistema
 * 
 * @author Cristhian Parra ({@link cdparra@gmail.com})
 * @author Fernando Mancía ({@link fernandomancia@gmail.com})
 */
@Root
public class Configuracion {

    /**
     * Path al directorio que contiene el binario ejecutable de Dot (Graphviz)
     */
   @Element
   private String dotpath = "/usr/bin/dot";
   
   /**
    * Símbolo a ser desplegado como etiqueta de los enlaces vacíos
    */
   @Element
   private String emptySymbol="(vacio)";

   /**
    * Path al directorio que almacenará temporalmente las imágenes que se generan
    */
   @Element
   private String imgdir = "/tmp";

   /**
    * Índice de propiedades de configuración (pensado para implementar versiones 
    * de configuración en el futuro)
    */
   @Attribute
   private int index;
   
   
   public Configuracion() {
      super();
   }  

   public Configuracion(String text, int index) {
      this.index = index;
      this.dotpath = text;
   }

   public String getDotPath() {
      return dotpath;
   }

   public int getGraphViz() {
      return index;
   }

    public String getEmptySymbol() {
        return emptySymbol;
    }

    public void setEmptySymbol(String emptySymbol) {
        this.emptySymbol = emptySymbol;
    }

    public void setImgdir(String text) {
        this.imgdir = text;
    }
    
    public String getImgdir() {
        return this.imgdir;
    }

    void setDotPath(String absolutePath) {
        this.dotpath = absolutePath;
    }
}
