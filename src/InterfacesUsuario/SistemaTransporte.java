//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SistemaTransporte {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception var2) {
            System.err.println("No se pudo establecer el Look and Feel del sistema");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    InterfacesUsuario.PantallaBienvenida pantallaBienvenida = new InterfacesUsuario.PantallaBienvenida();
                    pantallaBienvenida.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al iniciar el sistema: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
