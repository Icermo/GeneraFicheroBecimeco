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
import java.util.ArrayList;
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
        FileWriter ficheroEscritura;
        



public FicherosBavel (String cadenaPasada, String clientePasado){
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
                String numFacComp="-sin datos-";//Variable para guardar el número de factura y detectar cuando ha cambiado para así generar un nuevo
                int contNumFich=0;
                String[] filaNueva = new String[nCol+1]; 
                ArrayList<String[]> arrList = new ArrayList<>();
                float sumatorioIgic=0;
                float sumatorioBase=0;
                int contLin=0;
                String stringTemp;
                
                while(rset.next()){//Baja de línea y rser.next
                    veri=true;  //verificador de que hay datos.
                    numFacAct=rset.getString(3); //al principio del while rset.next, es decir, al bajar una línea de la consulta, pillamos el nuevo num de fact
                    for (j=0;j<nCol;j++)filaNueva[j]="";
                    // según sea una factura o una rectificativa //Metemos el tipo de factura
                    if (Float.parseFloat(rset.getString(36))>=0) {
                        tipoFac="D";  //Normal
                        filaNueva[0]=tipoFac;
                    }  
                    else {
                        tipoFac="C";  //Rectificativa, negativa o abono
                        filaNueva[0]=tipoFac;
                    } 
                    
                    if (iguala==false){  // Si nunca hemos igualado por primera vez ni creado el primer fichero
                        numFacComp=numFacAct; // Igualamos por primera vez al numero de factura actual
                        
                        try {// creo el PRIMER writer
                            this.ficheroOut = new OutputStreamWriter(new FileOutputStream("C:\\exporBaVel\\Fac."+tipoFac+"-Cli."+clientePasado+"-Num.406-"+numFacComp+".csv"),"UTF-8");
                            contNumFich++;
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
                                filaNueva[i+1]=fechaConvert;
                                }
                            else if ("IMPIGICLIN".equals(rsmetadatos.getColumnName(i+1))){
                                stringTemp=Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                filaNueva[i+1]=stringTemp;
                                sumatorioIgic=sumatorioIgic+Float.parseFloat(stringTemp);
                                }
                            else if ("IMPLIN".equals(rsmetadatos.getColumnName(i+1))){
                                stringTemp=Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                filaNueva[i+1]=stringTemp;
                                sumatorioBase=sumatorioBase+Float.parseFloat(stringTemp);
                                }
                            else{
                                filaNueva[i+1]=rset.getString(i+1);
                            }      
                        }
                        contLin++;
                        arrList.add(filaNueva);
                        for (int z=0;z<nCol+1;z++)System.out.println(filaNueva[z]); 
                    }
                    
                    // Si el número de factura ha cambiado, guardamos el nuevo número y creamos un fichero nuevo con este nuevo número
                    else if(!numFacComp.equals(numFacAct)){ 
                        //while que mete los sumatorios de IGICs y Totales en las líneas de la factura anterior ya recorrida
                        while (contLin>0){
                            filaNueva=arrList.get(contLin-1);
                            filaNueva[34]=Float.toString(sumatorioIgic);
                            filaNueva[36]=Float.toString(sumatorioBase);
                            
                        
                        //escribimos el fichero con cada elemento del array;
                            try {
                                for (j=0;j<nCol+1;j++){
                                    ficheroOut.write(filaNueva[j]);    
                                }
                                ficheroOut.write("\n");
                               
                            } catch (IOException ex) {
                                Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(null, "Error al escribir en fichero (if no equals)"+ex);
                                
                            }
                            contLin--;
                        }
                        
                        try { // cierro el anterior fichero de escritura
                            ficheroOut.close();
                            //this.ficheroBuff.close(); 
                            //this.ficheroEscritura.close(); 
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al cerrar fichero (one)"+ex);
                            
                        }
                        
                        //como ya hemos escrito todos las líneas de la factura contenidas en el array limpiamos todo
                        //contLin no hace talta ponerlo a cero  pq ya lo hemos ido restando hasta llegar a cero en el while
                        arrList.clear();//limpiamos el array
                        sumatorioIgic=0;//Pongo a cero el igic
                        sumatorioBase=0; //a cero
                        numFacComp=numFacAct; // las hacemos iguales otra vez
                        
                        //abro nuevo fichero de escritura
                        try {
                            this.ficheroOut = new OutputStreamWriter(new FileOutputStream("C:\\exporBaVel\\Fac."+tipoFac+"-Cli."+clientePasado+"-Num.406-"+numFacComp+".csv"),"UTF-8");
                            contNumFich++;
                        } catch (IOException ex) {
                            Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al crear uno de los ficheros"+ex);
                        }
                    
                        //for que rellena la linea con cada dato de columna del rset
                        for (i=0; i<nCol; i++){  //for que rellena una linea con cada dato de columna del rset
                            if ("datetime".equals(rsmetadatos.getColumnTypeName(i+1))){
                                pasaFecha=rset.getDate(i+1);
                                this.formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                                String fechaConvert=formatoFecha.format(pasaFecha);
                                filaNueva[i+1]=fechaConvert;
                                }
                            else if ("IMPIGICLIN".equals(rsmetadatos.getColumnName(i+1))){
                                stringTemp=Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                filaNueva[i+1]=stringTemp;
                                sumatorioIgic=sumatorioIgic+Float.parseFloat(stringTemp);
                                }
                            else if ("IMPLIN".equals(rsmetadatos.getColumnName(i+1))){
                                stringTemp=Double.toString(Math.rint(Double.parseDouble(rset.getString(i+1))*1000)/1000); //Paso de String a Float, redondeo, paso de nuevo a String y añado a la cadena
                                filaNueva[i+1]=stringTemp;
                                sumatorioBase=sumatorioBase+Float.parseFloat(stringTemp);
                                }
                            
                            else{
                                filaNueva[i+1]=rset.getString(i+1);
                            }      
                        }
                        contLin++;
                        arrList.add(filaNueva);
                        for (int z=0;z<nCol+1;z++)System.out.println(filaNueva[z]); 
                    }
                }
                
                //escribo el último fichero con los datos de la ult fact o abono si veri = true, si han habido datos
                if (veri){
                    while(contLin>0){
                            try {
                                for (j=0;j<nCol;j++){
                                    ficheroOut.write(filaNueva[j]);  
                                }
                                ficheroOut.write("\n");
                                
                            } catch (IOException ex) {
                                Logger.getLogger(FicherosBavel.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(null, "Error al escribir en fichero (last)"+ex);

                            }
                            contLin--;
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
                    JOptionPane.showMessageDialog(null, "Fin del proceso. Se generaron "+contNumFich+" ficheros.");
                }
                else  JOptionPane.showConfirmDialog(null, "No hay datos que devolver.", "Sin datos",  JOptionPane.CLOSED_OPTION);
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