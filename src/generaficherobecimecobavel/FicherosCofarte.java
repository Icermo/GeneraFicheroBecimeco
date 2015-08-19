package generaficherobecimecobavel;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
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
        SimpleDateFormat formatoFecha;
        OutputStreamWriter ficheroFacts;
        OutputStreamWriter ficheroAbos;

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
                nCol= rsmetadatos.getColumnCount();
                boolean veriCreaFichFacts=false;
                boolean veriCreaFichAbos=false;
                while(rset.next()){ // mientras consiga una nueva fila (se puede mover a la siguiente ) = obtener siguiente fila.
                    veri=true;  //Hay datos.
                    for (i=0; i < nCol; i++){
                        if (Integer.parseInt(rset.getString(7))>=0){ // facturas no rectificativas, normales
                            if (veriCreaFichFacts==false){
                                veriCreaFichFacts=true; //hago saltar el indicador
                                try {// creo el fichero de facturas, una vez pasamos el if para evtar crearlo para nada
                                    this.ficheroFacts = new OutputStreamWriter(new FileOutputStream("C:\\exporCofarte\\FactMESAÑO"+".csv"),"UTF-8");
                                    //this.ficheroFisico = new File ("C:\\Users\\ivanw7\\Desktop\\exporBaVel\\Fac-"+tipoFac+"Cli-"+VentanaPrincipal.txtCliente.getText()+"Num-"+numFacComp+".csv"); //creo el primer fichero físico
                                    //this.ficheroEscritura =  new FileWriter (ficheroFisico);   // Creo el primer fichero de escritura
                                    //this.ficheroBuff = new BufferedWriter(ficheroEscritura);
                                } catch (IOException ex) {
                                    Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                    JOptionPane.showMessageDialog(null, "Error al crear el fichero de facturas (no rectificativas)"+ex);
                                }
                            }
                        if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(5))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"B");
                                }
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(5))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"A");
                            }  
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "0".equals(rset.getString(5))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"0");  //caso que no creo que se de
                            } 
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(6))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"B");
                            }      
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(6))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"A");//caso que no creo que se de
                            } 
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "0".equals(rset.getString(6))){
                                 ficheroFacts.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"0");// Caso que no creo que se de
                            } 
                        }
                        else if (Integer.parseInt(rset.getString(7))<0){ // al ser negativo es un abono, rectificativas, y los abonos van en un fichero aparte
                            if (veriCreaFichAbos==false){
                                veriCreaFichAbos=true; //hago saltar el indicador
                                try {// creo el fichero de facturas, una vez pasamos el if para evtar crearlo para nada
                                    this.ficheroFacts = new OutputStreamWriter(new FileOutputStream("C:\\exporCofarte\\FactMESAÑO"+".csv"),"UTF-8");
                                    //this.ficheroFisico = new File ("C:\\Users\\ivanw7\\Desktop\\exporBaVel\\Fac-"+tipoFac+"Cli-"+VentanaPrincipal.txtCliente.getText()+"Num-"+numFacComp+".csv"); //creo el primer fichero físico
                                    //this.ficheroEscritura =  new FileWriter (ficheroFisico);   // Creo el primer fichero de escritura
                                    //this.ficheroBuff = new BufferedWriter(ficheroEscritura);
                                } catch (IOException ex) {
                                    Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                    JOptionPane.showMessageDialog(null, "Error al crear el fichero de facturas (no rectificativas)"+ex);
                                }
                            }
                            if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(5))){
                                ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"B");
                                }
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(5))){
                                 ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"A");
                            }  
                            else if ("PjeIva1".equals(rsmetadatos.getColumnName(i+1)) && "0".equals(rset.getString(5))){
                                 ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(3)+"#"+"0");//caso que no creo que se de
                            } 
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "7".equals(rset.getString(6))){
                                 ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"B");
                            }      
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "3".equals(rset.getString(6))){
                                 ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"A");//caso que no creo que se de
                            } 
                            else if ("PjeIva2".equals(rsmetadatos.getColumnName(i+1)) && "0".equals(rset.getString(6))){
                                 ficheroAbos.write(rset.getString(1)+"##"+rset.getString(2)+"#"+"95"+"#"+rset.getString(4)+"#"+"0");//caso que no creo que se de
                            }      
                        }
                    }
                }
                if (veriCreaFichFacts==true){
                    try { 
                        ficheroFacts.close();
                        //this.ficheroBuff.close(); 
                        //this.ficheroEscritura.close(); 
                        } catch (IOException ex) {
                                Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(null, "Error al cerrar fichero de facturas (no rectificativas)"+ex);
                        }
                    }
                if (veriCreaFichAbos==true){
                    try { 
                        ficheroAbos.close();
                        //this.ficheroBuff.close(); 
                        //this.ficheroEscritura.close(); 
                        } catch (IOException ex) {
                                Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(null, "Error al cerrar fichero de abonos (rectificativas)"+ex);
                        }
                    }
            if (veri==false) JOptionPane.showConfirmDialog(null, "No hay datos que devolver.", "Sin datos",  JOptionPane.CLOSED_OPTION);
                rset.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex){
                Logger.getLogger(RellenaRejillaBD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FicherosCofarte.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RellenaRejillaBD.class.getName()).log(Level.SEVERE, null, ex);
        }

}   
}