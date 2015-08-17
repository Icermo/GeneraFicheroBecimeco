package generaficherobecimecobavel;


import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
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
public class FicherosBavel  {  
        Date pasaFecha;
        SimpleDateFormat formatoFecha;
        OutputStreamWriter ficheroOut;
        File ficheroFisico;
        FileWriter ficheroEscritura;
        BufferedWriter ficheroBuff;



public FicherosBavel (String cadenaPasada){
        try {
            DriverManager.registerDriver (new com.microsoft.sqlserver.jdbc.SQLServerDriver()); //registramos el driver
            try (
                Connection conn = DriverManager.getConnection ("jdbc:sqlserver://SERVIDOR:1433;databaseName=becimeco;user=icm;password=sqlbecimeco2015");
                Statement stmt = conn.createStatement(); 
                ResultSet rset = stmt.executeQuery(cadenaPasada) // rset guarda el resultado del query
                )
            {
                boolean veri=false;  // se pondra en true si hay más registros
                boolean iguala=false; // Se pone en true cuando hemos igualdo por primera vez y creado el primer fichero
                int i;
                int j;
                ResultSetMetaData rsmetadatos;
                rsmetadatos = rset.getMetaData();
                int nCol= rsmetadatos.getColumnCount();
                String tipoFac;
                String numFacAct;//Variable para guardar el número de factura y detectar cuando ha cambiado para así generar un nuevo
                String numFacComp="-sin dato-";//Variable para guardar el número de factura y detectar cuando ha cambiado para así generar un nuevo
                String filaNueva; // creamos un array de objetos del tamaño de las columnas encontradas
                
                
                while(rset.next()){ // For que baja de línea y rser.next
                    numFacAct=rset.getString(3); //al principio del while rset.next, es decir, al bajar una línea de la consulta, pillamos el nuevo num de fact
                    veri=true;  //verificador de que hay datos.
                    filaNueva="";//limpiamos
                    
                    // según sea una factura o una rectificativa //Metemos el tipo de factura
                    if (Float.parseFloat(rset.getString(36))>=0) {
                        tipoFac="D";  //Normal
                        filaNueva=tipoFac;
                    }  
                    else {
                        tipoFac="C";  //Rectificativa, negativa o abono
                        filaNueva=tipoFac;
                    } 
                    
                    if (iguala==false){  // Si nunca hemos igualado por primera vez ni creado el primer fichero
                        numFacComp=numFacAct; // Igualamos por primera vez al numero de factura actual
                        
                        try {// creo el PRIMER filewriter
                            this.ficheroOut = new OutputStreamWriter(new FileOutputStream("C:\\exporBaVel\\Fac."+tipoFac+"-Cli."+VentanaPrincipal.txtCliente.getText()+"-Num.406-"+numFacComp+".csv"),"UTF-8");
                            //this.ficheroFisico = new File ("C:\\Users\\ivanw7\\Desktop\\exporBaVel\\Fac-"+tipoFac+"Cli-"+VentanaPrincipal.txtCliente.getText()+"Num-"+numFacComp+".csv"); //creo el primer fichero físico
                            //this.ficheroEscritura =  new FileWriter (ficheroFisico);   // Creo el primer fichero de escritura
                            //this.ficheroBuff = new BufferedWriter(ficheroEscritura);
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al crear el primer fichero"+ex);
                        }
                        iguala=true;  // Cambiamos valor para que no vuelva a entrar en este if
                    }
                    
                    // Si el numero de factura no ha cambiado:
                    else if(numFacComp.equals(numFacAct)){ 
                        for (i=0; i < nCol; i++){  //for que rellena una linea con cada dato de columna del rset
                            if ("datetime".equals(rsmetadatos.getColumnTypeName(i+1))){
                                pasaFecha=rset.getDate(i+1);
                                this.formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                                String fechaConvert=formatoFecha.format(pasaFecha);
                                filaNueva=filaNueva+fechaConvert;
                                }
                            else if ("RED1".equals(rsmetadatos.getColumnName(i+1))||"RED2".equals(rsmetadatos.getColumnName(i+1))||"RED3".equals(rsmetadatos.getColumnName(i+1))){
                                filaNueva=filaNueva+Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena//Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                }
                            
                            else{
                                filaNueva=filaNueva+rset.getString(i+1);
                            }      
                        }
                        
                        try {
                            ficheroOut.write(filaNueva+"\n");
                            //ficheroBuff.write(filaNueva);
                            //ficheroBuff.newLine();
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al escribir en fichero (if equals)"+ex);
                        }
                    }
                    
                    // Si el número de factura ha cambiado, guardamos el nuevo número y creamos un fichero nuevo con este nuevo número
                    else if(!numFacComp.equals(numFacAct)){ 
                        numFacComp=numFacAct; // las hacemos iguales otra vez
                        try { // cierro el anterior fichero de escritura
                            ficheroOut.close();
                            //this.ficheroBuff.close(); 
                            //this.ficheroEscritura.close(); 
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al cerrar fichero (one)"+ex);
                            
                        }
                        try {
                            this.ficheroOut = new OutputStreamWriter(new FileOutputStream("C:\\exporBaVel\\Fac."+tipoFac+"-Cli."+VentanaPrincipal.txtCliente.getText()+"-Num.406-"+numFacComp+".csv"),"UTF-8");
                            //this.ficheroFisico = new File ("C:\\Users\\ivanw7\\Desktop\\exporBaVel\\"+numFacComp+".csv"); //creo el nuevo fichero físico
                            //this.ficheroEscritura =  new FileWriter (ficheroFisico);   // Creo el nuevo fichero de escritura
                            //this.ficheroBuff = new BufferedWriter(ficheroEscritura);
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al crear fichero"+ex);
                        }
                    
                        //for que rellena la linea con cada dato de columna del rset
                        for (i=0; i < nCol; i++){  //for que rellena una linea con cada dato de columna del rset
                            if ("datetime".equals(rsmetadatos.getColumnTypeName(i+1))){
                                pasaFecha=rset.getDate(i+1);
                                this.formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                                String fechaConvert=formatoFecha.format(pasaFecha);
                                filaNueva=filaNueva+fechaConvert;
                                }
                            else if ("RED1".equals(rsmetadatos.getColumnName(i+1))||"RED2".equals(rsmetadatos.getColumnName(i+1))||"RED3".equals(rsmetadatos.getColumnName(i+1))){
                                filaNueva=filaNueva+Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                }
                            
                            else{
                                filaNueva=filaNueva+rset.getString(i+1);
                            }      
                        }
                        try {
                            ficheroOut.write(filaNueva+"\n");
                            //ficheroBuff.write(filaNueva);
                            //ficheroBuff.newLine();
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al escribir en fichero (if no equals)"+ex);
                            
                        }
                    }
                }
                //cierro el ÚLTIMO fichero de escritura
                try { 
                    ficheroOut.close();
                    //this.ficheroBuff.close(); 
                    //this.ficheroEscritura.close(); 
                    } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al cerrar fichero (last)"+ex);
                    }
                
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