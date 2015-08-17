/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generaficherobecimecobavel;

/**
 *
 * @author INFORMATICA
 */
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author ICERMO
 */
public class VentanaCreditos   extends JFrame{
    
    public VentanaCreditos ()
    {
        this.add ( new JLabel("   Programa hecho por ICERMO (ivancemo87@gmail.com)"));
        this.setSize(450,100); // tamaño de la ventana
        this.setLocation(500,280);// posicion dentro de la pantalla
    }
    
}