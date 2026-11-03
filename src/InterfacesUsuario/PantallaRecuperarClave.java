//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import objetosNegocio.Usuario;
import persistencia.Usuarios;

public class PantallaRecuperarClave extends JDialog {
    private JTextField txtUsuario;
    private JButton btnBuscar;
    private JButton btnCerrar;

    public PantallaRecuperarClave(JFrame padre) {
        super(padre, "Recuperar Contraseña", true);
        this.initComponents();
    }

    private void initComponents() {
        this.setSize(400, 250);
        this.setLocationRelativeTo(this.getParent());
        this.setResizable(false);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = 2;
        JLabel lblInstruccion = new JLabel("Ingrese su usuario para ver su contraseña:");
        lblInstruccion.setFont(new Font("Arial", 0, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblInstruccion, gbc);
        this.txtUsuario = new JTextField(20);
        this.txtUsuario.setFont(new Font("Arial", 0, 14));
        gbc.gridy = 1;
        panel.add(this.txtUsuario, gbc);
        JPanel panelBotones = new JPanel(new FlowLayout(1));
        this.btnBuscar = new JButton("Buscar");
        this.btnCerrar = new JButton("Cerrar");
        this.btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaRecuperarClave.this.buscarClave();
            }
        });
        this.btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaRecuperarClave.this.dispose();
            }
        });
        panelBotones.add(this.btnBuscar);
        panelBotones.add(this.btnCerrar);
        gbc.gridy = 2;
        panel.add(panelBotones, gbc);
        this.add(panel);
    }

    private void buscarClave() {
        String idUsuario = this.txtUsuario.getText().trim();
        if (idUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un usuario");
        } else {
            try {
                Usuarios usuarios = new Usuarios("usuarios.dat");
                Usuario uBusqueda = new Usuario();
                uBusqueda.setIdUsuario(idUsuario);
                Usuario u = usuarios.obten(uBusqueda);
                if (u != null) {
                    JOptionPane.showMessageDialog(this, "Usuario encontrado.\nSu contraseña es: " + u.getContrasena(), "Recuperación Exitosa", 1);
                } else {
                    JOptionPane.showMessageDialog(this, "El usuario no existe en el sistema.", "Error", 0);
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }
}
