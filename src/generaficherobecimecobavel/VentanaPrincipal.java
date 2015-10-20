/*

 */
package generaficherobecimecobavel;

/**
 *
 * @author INFORMATICA
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;   
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JComboBox;

/**
 *
 * @author ivanw7
 */
public class VentanaPrincipal extends JFrame{
    
    JTable rejilla;
    JTextField txtFechaInicio;
    JTextField txtFechaFin;
    JTextField txtCliente;
    JButton botonExportar;
    JComboBox comboFormato;
    static DefaultTableModel modelo;
    JMenuBar barraMenuPrincipal;
    
    private void LanzaRellenaRejillaBD(){
        if (this.txtFechaInicio.getText().isEmpty() || this.txtFechaFin.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Elija un rango de fechas correcto"); 
        }else if (this.txtCliente.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Introduzca un código de cliente.");       
                    
        }else
            switch (this.comboFormato.getSelectedIndex()){
                case 0:
                    JOptionPane.showMessageDialog(null, "Elija un formato de archivo: Cofarte o BaVel");
                    break;
                case 1:
                    RellenaRejillaBD  rellenaBD1 = new RellenaRejillaBD ("SELECT AlbaranesVentasCab.cliente, AlbaranesVentasCab.RazonSocial, AlbaranesVentasCab.Factura, Facturas.fecha, AlbaranesVentasCab.Albaran, AlbaranesVentasCab.Fecha,AlbaranesVentasCab.Importe, Facturas.importe\n" +
                        "FROM dbo.AlbaranesVentasCab JOIN facturas on AlbaranesVentasCab.Factura=Facturas.factura JOIN AlbaranesVentasLin on AlbaranesVentasCab.Albaran=AlbaranesVentasLin.Albaran\n" +
                        "WHERE Facturas.Fecha BETWEEN '"+txtFechaInicio.getText()+"' AND '"+txtFechaFin.getText()+"' AND AlbaranesVentasCab.CLIENTE LIKE '"+txtCliente.getText()+"' AND AlbaranesVentasLin.Articulo<>'NO' \n" +
                        "ORDER BY AlbaranesVentasCab.ALBARAN desc");
                        //Nos evitamos cambios en los datos de consulta para que no haya diferencia con los ficheros volcados si se cambiaran dichos datos
                        this.comboFormato.setEnabled(false);
                        bloqueaCampos();

                        
                    break;
                case 2:
                    RellenaRejillaBD  rellenaBD2 = new RellenaRejillaBD ("SELECT AlbaranesVentasCab.cliente, AlbaranesVentasCab.RazonSocial, AlbaranesVentasCab.Factura, Facturas.fecha,AlbaranesVentasCab.Albaran, AlbaranesVentasCab.Fecha, AlbaranesVentasCab.Importe, Facturas.importe\n" +
                        "FROM dbo.AlbaranesVentasCab JOIN facturas on AlbaranesVentasCab.Factura=Facturas.factura JOIN AlbaranesVentasLin on AlbaranesVentasCab.Albaran=AlbaranesVentasLin.Albaran\n" +
                        "WHERE Facturas.Fecha BETWEEN '"+txtFechaInicio.getText()+"' AND '"+txtFechaFin.getText()+"' AND AlbaranesVentasCab.CLIENTE LIKE '"+txtCliente.getText()+"' AND AlbaranesVentasLin.Articulo<>'NO' \n" +
                        "ORDER BY AlbaranesVentasCab.ALBARAN desc");
                        //Nos evitamos cambios en los datos de consulta para que no haya diferencia con los ficheros volcados si se cambiaran dichos datos
                        bloqueaCampos();
                    break;
            }
            

    }
     
    //Nos evitamos cambios en los datos de consulta para que no haya diferencia con los ficheros volcados si se cambiaran dichos datos
    private void bloqueaCampos(){
        this.comboFormato.setEnabled(false);
        this.txtFechaInicio.setEditable(false);
        this.txtFechaFin.setEditable(false);
        this.txtCliente.setEnabled(false);
        this.botonExportar.setEnabled(true);//aquí permitimos exportar datos, una vez comprobados todos los campos y devulta la consulta previa ok.
    }
    
    
    private void LanzaConsulta(){
        
        if (this.txtFechaInicio.getText().isEmpty() || this.txtFechaFin.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Elija un rango de fechas correcto");
        }else
            switch (this.comboFormato.getSelectedIndex()){
                case 1:
                    String cadenaAPasar1=("SELECT Transporte, Albaran, Total=(ImporteBase1), Total2=(ImporteBase2), PjeIva1, PjeIva2, Importe, Fecha\n" +
                                            "FROM dbo.AlbaranesVentasCab\n" +
                                            "WHERE FECHA BETWEEN '26/06/15' AND '25/07/15'\n" +
                                            "AND CLIENTE LIKE '07562'\n" +
                                            "ORDER BY ALBARAN");
                    FicherosCofarte  FCofarte = new FicherosCofarte (cadenaAPasar1);
                    break;
                case 2:
                    String cadenaAPasar2;
                    cadenaAPasar2=("SELECT ';','406-', AlbaranesVentasCab.Factura, ';;',Facturas.fecha, ';','EUR',';' ,AlbaranesVentasLin.Articulo\n" +//9
                        ",';', AlbaranesVentasLin.Descripcion, ';', 'Unidades', ';', AlbaranesVentasLin.Cantidad, ';', AlbaranesVentasLin.Precio\n" +//17
                        ", ';', AlbaranesVentasLin.PjeDto, ';;', AlbaranesVentasLin.PjeIva, ';', 'IGIC', ';', (AlbaranesVentasLin.Importe*(AlbaranesVentasLin.PjeIva/100)) AS IMPIGICLIN \n" +//25
                        ",';', (((AlbaranesVentasLin.Cantidad*AlbaranesVentasLin.Precio)-(AlbaranesVentasLin.Precio*(AlbaranesVentasLin.PjeDto/100)))) AS IMPLIN, ';'\n" +//28
                        ",'406-',AlbaranesVentasCab.Albaran, ';', AlbaranesVentasCab.Fecha,';;'\n" +//33
                        ", (Facturas.ImporteIva1+Facturas.ImporteIva2+Facturas.ImporteIva3+Facturas.ImporteIva4) AS IMPIGICTOTFAC,';'"//35
                        + ", (Facturas.ImporteBase1+Facturas.ImporteBase2+Facturas.ImporteBase3+Facturas.ImporteBase4) AS BASIMP,';'\n" +//37
                        "FROM dbo.AlbaranesVentasCab\n" +
                        "JOIN facturas on AlbaranesVentasCab.Factura=Facturas.factura\n" +
                        "JOIN AlbaranesVentasLin on AlbaranesVentasCab.Albaran=AlbaranesVentasLin.Albaran\n" +
                        "WHERE Facturas.Fecha BETWEEN '"+txtFechaInicio.getText()+"' AND '"+txtFechaFin.getText()+"' AND AlbaranesVentasCab.CLIENTE LIKE '"+txtCliente.getText()+"' AND AlbaranesVentasLin.Articulo<>'NO'\n" +
                        "ORDER BY AlbaranesVentasCab.ALBARAN desc;");
 
                    FicherosBavel  FBavel = new FicherosBavel (cadenaAPasar2, this.txtCliente.getText());

                    break;
            }
    }
    
    private void LimpiarRejilla(){
        try {
        int filas=rejilla.getRowCount();
        for (int i = 0;filas>i; i++) {
                modelo.removeRow(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al limpiar la tabla."+e);
        }
        this.comboFormato.setEnabled(true);
        this.comboFormato.setEnabled(true);
        this.txtFechaInicio.setEditable(true);
        this.txtFechaFin.setEditable(true);
        this.txtCliente.setEnabled(true);
        this.botonExportar.setEnabled(false);
    }
    private void haceNada(){
        
    }
    
    
    private String RestaAñosADate(java.sql.Date fecha, int nAños){  //FUNCIO QUE RESTA O SUMA AÑOS A UNA FECHA DADA
        String fechaConvert;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.YEAR,nAños);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");  //Formateamos la fecha como nos interasa
        fechaConvert=(sdf.format(calendar.getTime())); //La metemos en el txtField.
        return fechaConvert;
    }

    public VentanaPrincipal(String titulo)
    {
        super (titulo); // otra forma de ponerle el titulo..

        this.setSize(970,730); // tamaño de la ventana
        this.setLocation(10,10);// posicion 
        this.setResizable(false);  //evitamos el cambio de tamaño
        
        
        this.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        
        VentanaPrincipal.modelo = new DefaultTableModel() // Esto define el contenido de la rejilla.
                            {                   //HACE QUE EL CONTENIDO DE LA REJILLA NO SE PUEDA MODIFICAR POR EL OPERADOR
                                @Override
                                public boolean isCellEditable(int fila, int columna) 
                                    {
                                return false; //Con esto conseguimos que la tabla no se pueda editar
                               }
                             };


        modelo.addColumn("Cód. cliente");
        modelo.addColumn("Nombre de cliente");
        modelo.addColumn("Nº factura");
        modelo.addColumn("Fecha factura");
        modelo.addColumn("Nº albarán");
        modelo.addColumn("Fecha albarán");
        modelo.addColumn("Importe albarán");
        modelo.addColumn("Importe factura");
        
        this.rejilla= new JTable(VentanaPrincipal.modelo); // creamos la rejilla usando este modelo 
        
        this.rejilla.getColumn("Cód. cliente").setPreferredWidth(100);
        this.rejilla.getColumn("Nombre de cliente").setPreferredWidth(190);
        this.rejilla.getColumn("Nº factura").setPreferredWidth(90);
        this.rejilla.getColumn("Fecha factura").setPreferredWidth(90);
        this.rejilla.getColumn("Nº albarán").setPreferredWidth(90);
        this.rejilla.getColumn("Fecha albarán").setPreferredWidth(90);
        this.rejilla.getColumn("Importe albarán").setPreferredWidth(90);
        this.rejilla.getColumn("Importe factura").setPreferredWidth(90);
        
        
        this.rejilla.setPreferredScrollableViewportSize(new Dimension(830, 550)); // tamaño
        
        
        JLabel lblVendedor = new JLabel("Formato de fichero:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST;//Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(lblVendedor,constraints);   
        
        this.comboFormato= new JComboBox();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST;//Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.comboFormato.addItem("Elija formato...");
        this.comboFormato.addItem("Formato Cofarte");
        this.comboFormato.addItem("Formato Bavel");
        this.getContentPane().add(comboFormato,constraints);
        
        JLabel lblFechaInicio = new JLabel("Fecha inicial:");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(lblFechaInicio,constraints);      

        
        this.txtFechaInicio = new JTextField(10);
        //txtFechaInicio.setEditable(false);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(txtFechaInicio,constraints);
        
        /*
        final JDateChooser dCInicio = new JDateChooser();
        dCInicio.add(txtFechaInicio);
        //dCInicio.setDate();
        this.getContentPane().add(dCInicio,constraints);
        
        dCInicio.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if ("date".equals(e.getPropertyName())) { //Si la propiedad obtenia es una fecha
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");  //Formateamos la fecha como nos interasa
                txtFechaInicio.setText(sdf.format(dCInicio.getDate())); //La metemos en el txtField.
            }
        }
    });*/

        JLabel lblFechaFin = new JLabel("Fecha final:");
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(lblFechaFin,constraints);
        
        this.txtFechaFin = new JTextField(10);
        //txtFechaFin.setEditable(false);
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(txtFechaFin,constraints);
        
        /*
        final JDateChooser dCFin = new JDateChooser();
        dCFin.add(txtFechaFin);
        this.getContentPane().add(dCFin,constraints);
        
        dCFin.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if ("date".equals(e.getPropertyName())) { //Si la propiedad obtenia es una fecha
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");  //Formateamos la fecha como nos interasa
                txtFechaFin.setText(sdf.format(dCFin.getDate())); //La metemos en el txtField.    
            }
        }
    });*/
        
        
        JLabel lblCLiente = new JLabel("Cliente:");
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(lblCLiente,constraints);
        
        this.txtCliente = new JTextField(10);
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth=1;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        this.getContentPane().add(txtCliente,constraints);
        
        JScrollPane panelScroll = new JScrollPane(this.rejilla);

        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 2; // El área de texto empieza en la fila cero
        constraints.gridwidth=6;
        this.getContentPane().add(panelScroll, constraints); // añadimos un texto
        
        
        JButton botonLimpiar = new JButton ("Nueva consulta");
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.EAST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        ActionListener actionListenerParaLimpiar;       // creamos un actionlistener para salir (lo guardamos en una variable 
                                                    // puesto que usaremos el mismo en la opcion "Salir" del menu.
        
        actionListenerParaLimpiar =new ActionListener()
                                                  {
                                                    @Override
                                                    public void actionPerformed( ActionEvent evento) {LimpiarRejilla();} // llamamos al metodo de la clase ventanaPrincipal que nos lo va a controlar
                                                  }  ;      
                                     
        botonLimpiar.addActionListener(actionListenerParaLimpiar); // que hacer si pincamos aqui..          
        this.getContentPane().add(botonLimpiar,constraints);
        
        JButton botonConsultar = new JButton ("Consultar");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.WEST; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        ActionListener actionListenerParaConsultar;       // creamos un actionlistener para salir (lo guardamos en una variable 
                                                    // puesto que usaremos el mismo en la opcion "Salir" del menu.
        
        actionListenerParaConsultar =new ActionListener()
                                                  {
                                                    @Override
                                                    public void actionPerformed( ActionEvent evento) {
                                                        LanzaRellenaRejillaBD();
                                                    } 
                                                  }  ;      
                                     
        botonConsultar.addActionListener(actionListenerParaConsultar); // que hacer si pincamos aqui..          
        this.getContentPane().add(botonConsultar,constraints);
      
        
        botonExportar = new JButton ("Exportar");
        this.botonExportar.setEnabled(false);
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER; //Situar el botón en la parte superior izq (anchor = situarlo sin estirarlo)
        ActionListener actionListenerParaExportar;       // creamos un actionlistener para salir (lo guardamos en una variable 
                                                    // puesto que usaremos el mismo en la opcion "Salir" del menu.
        
        actionListenerParaExportar =new ActionListener()
                                                  {
                                                    @Override
                                                    public void actionPerformed( ActionEvent evento) {
                                                        LanzaConsulta();
                                                    } // llamamos al metodo de la clase ventanaPrincipal que nos lo va a controlar
                                                  }  ;      
                                     
        botonExportar.addActionListener(actionListenerParaExportar); // que hacer si pincamos aqui..          
        this.getContentPane().add(botonExportar,constraints);
        
        
        this.barraMenuPrincipal = new JMenuBar(); // creamos el menu princiapl..
        
        JMenu menu; // es un elemento que tiene submenu..
        JMenuItem menuItem; // esto es un elemento de menu = si pinchas vas a un opcion.
        
        menu = new JMenu  ("Aplicación");  // menu 
        
        this.barraMenuPrincipal.add(menu);  // ponemos el menu en la barra princpal


        menuItem= new JMenuItem ("Acerca de este programita");
        menuItem.addActionListener(new ActionListener() // Que hacer cuando pinchamos en esta opcion "Acerca de.."
                                                  {
                                                    @Override
                                                    public void actionPerformed( ActionEvent evento) {        
                                                        VentanaCreditos ventana = new VentanaCreditos();
                                                        ventana.setVisible(true);} // llamamos al metodo de la clase ventanaPrincipal que nos lo va a controlar
                                                  }  
                                   );
        menu.add(menuItem);
        
        this.barraMenuPrincipal.add(menu); // ya tenemos el menu..  
        //// decimos que esta barra de menus es para nuestra ventana. 
        this.setJMenuBar (this.barraMenuPrincipal); 

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        //VentanaPrincipal.modelo.fireTableDataChanged(); // decimos que han cambiado los datos para que se repinte
    }
};
