/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generaficherobecimecobavel;


import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ivanw7
 */



public class RellenaRejillaBD  {  
        boolean veri=false;  // se pondra en true si hay más registros
        int i;
        ResultSetMetaData rsmetadatos;
        Date pasaFecha;
        SimpleDateFormat formatoFecha;
        FileWriter ficheroSalida;
        
        
public RellenaRejillaBD (String cadenaPasada){
        try {
            DriverManager.registerDriver (new com.microsoft.sqlserver.jdbc.SQLServerDriver()); //registramos el driver
            try (
                Connection conn = DriverManager.getConnection ("jdbc:sqlserver://SERVIDOR:1433;databaseName=becimeco;user=icm;password=sqlbecimeco2015");
                Statement stmt = conn.createStatement(); 
                ResultSet rset = stmt.executeQuery(cadenaPasada) // rset guarda el resultado del query
                )
            {
                rsmetadatos = rset.getMetaData();
                int nCol;
                nCol= rsmetadatos.getColumnCount();
                while(rset.next()){ // mientras consiga una nueva fila (se puede mover a la siguiente ) = obtener siguiente fila.
                    veri=true;  //Hay datos.
                    String[] filaNueva = new String[nCol+1]; // creamos un array de objetos del tamaño de las columnas encontradas
                    for (i=0; i < nCol; i++){
                        if ("DATE".equals(rsmetadatos.getColumnTypeName(i+1))){
                            pasaFecha=rset.getDate(i+1);
                            this.formatoFecha = new SimpleDateFormat("dd MMM yyyy");
                            String fechaConvert=formatoFecha.format(pasaFecha);
                            filaNueva[i]= fechaConvert;// por posicion (el +1 porque getString empieza a contar de 1;
                            }
                        else{
                            filaNueva[i]= rset.getString(i+1);
                        }      
                    }
                    VentanaPrincipal.modelo.addRow(filaNueva); // añadirmos fila a fila
                }
                VentanaPrincipal.modelo.fireTableDataChanged(); // decimos que han cambiado los datos para que se repinte
                
                if (veri==false) JOptionPane.showConfirmDialog(null, "No hay datos que devolver.", "Sin datos",  JOptionPane.CLOSED_OPTION);
                rset.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex){
                Logger.getLogger(RellenaRejillaBD.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RellenaRejillaBD.class.getName()).log(Level.SEVERE, null, ex);
        }

}   
}