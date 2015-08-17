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
public class FicherosCofarte  {  
        boolean veri=false;  // se pondra en true si hay más registros
        int i;
        ResultSetMetaData rsmetadatos;
        Date pasaFecha;
        SimpleDateFormat formatoFecha;
        FileWriter ficheroSalida;

public FicherosCofarte (String cadenaPasada){
        try {
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver()); //registramos el driver
            try (
                Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@192.168.38.221:1521:orcl", "foncal", "foncal");
                Statement stmt = conn.createStatement(); 
                ResultSet rset = stmt.executeQuery(cadenaPasada) // rset guarda el resultado del query
                )
            {
                rsmetadatos = rset.getMetaData();
                int nCol;
                int contadorPos=0;
                nCol= rsmetadatos.getColumnCount();
                while(rset.next()){ // mientras consiga una nueva fila (se puede mover a la siguiente ) = obtener siguiente fila.
                    contadorPos++;
                    veri=true;  //Hay datos.
                    String[] filaNueva = new String[nCol+1]; // creamos un array de objetos del tamaño de las columnas encontradas
                    for (i=0; i < nCol; i++){
                        if (Integer.parseInt(rset.getString(13))>=0){ // facturas no rectificativas, normales
                            if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(11))){
                                //Linea del IGIC 7 en el primer campo (B)
                                }
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(11))){
                                //Linea del IGIC 3 en el primer campo(A)
                            }  
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(12))){
                                //Linea del IGIC 7 en el segundo campo (B)
                            }      
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(12))){
                                //Linea del IGIC 3 en el segundo campo, (puede que no se de nunca) (A)
                            } 
                        }
                        else if (Integer.parseInt(rset.getString(13))<0){ // al ser negativo es un abono, rectificativas, y los abonos van en un fichero aparte
                            if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(11))){
                                //Linea del IGIC 7 en el primer campo (B)
                                }
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(11))){
                                //Linea del IGIC 3 en el primer campo(A)
                            }  
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(12))){
                                //Linea del IGIC 7 en el segundo campo (B)
                            }      
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(12))){
                                //Linea del IGIC 3 en el segundo campo, (puede que no se de nunca) (A)
                            } 
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