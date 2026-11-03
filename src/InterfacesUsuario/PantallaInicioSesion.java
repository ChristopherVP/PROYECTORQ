//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import objetosServicio.Fecha;
import persistencia.Usuarios;

public class PantallaInicioSesion extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JLabel lblOlvidaste;
    private InterfacesUsuario.PantallaBienvenida pantallaPadre;

    public PantallaInicioSesion(InterfacesUsuario.PantallaBienvenida padre) {
        this.pantallaPadre = padre;
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Iniciar Sesión - Sistema de Transporte");
        this.setDefaultCloseOperation(0);
        this.setExtendedState(6);
        this.setLocationRelativeTo(this.pantallaPadre);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                PantallaInicioSesion.this.regresar();
            }
        });
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(new Color(245, 245, 245));
        JPanel panelCard = new JPanel();
        panelCard.setLayout(new BoxLayout(panelCard, 1));
        panelCard.setBackground(Color.WHITE);
        panelCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(40, 50, 40, 50)));
        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Arial", 1, 24));
        lblTitulo.setAlignmentX(0.5F);
        panelCard.add(lblTitulo);
        panelCard.add(Box.createRigidArea(new Dimension(0, 30)));
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Arial", 0, 14));
        panelForm.add(lblUser, gbc);
        gbc.gridy = 1;
        this.txtUsuario = new JTextField(20);
        this.txtUsuario.setPreferredSize(new Dimension(280, 35));
        this.txtUsuario.setFont(new Font("Arial", 0, 14));
        panelForm.add(this.txtUsuario, gbc);
        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Arial", 0, 14));
        panelForm.add(lblPass, gbc);
        gbc.gridy = 3;
        this.txtContrasena = new JPasswordField(20);
        this.txtContrasena.setPreferredSize(new Dimension(280, 35));
        this.txtContrasena.setFont(new Font("Arial", 0, 14));
        panelForm.add(this.txtContrasena, gbc);
        panelCard.add(panelForm);
        panelCard.add(Box.createRigidArea(new Dimension(0, 25)));
        JPanel panelBotones = new JPanel(new FlowLayout(1, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        this.btnIngresar = new JButton("Ingresar");
        this.btnIngresar.setPreferredSize(new Dimension(120, 40));
        this.btnIngresar.setBackground(new Color(41, 128, 185));
        this.btnIngresar.setForeground(Color.WHITE);
        this.btnIngresar.setFocusPainted(false);
        this.btnIngresar.setFont(new Font("Arial", 1, 14));
        this.btnIngresar.setCursor(new Cursor(12));
        this.btnIngresar.setBorderPainted(false);
        this.btnIngresar.setOpaque(true);
        this.btnIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaInicioSesion.this.iniciarSesion();
            }
        });
        this.btnCancelar = new JButton("Cancelar");
        this.btnCancelar.setPreferredSize(new Dimension(120, 40));
        this.btnCancelar.setBackground(new Color(231, 76, 60));
        this.btnCancelar.setForeground(Color.WHITE);
        this.btnCancelar.setFocusPainted(false);
        this.btnCancelar.setFont(new Font("Arial", 1, 14));
        this.btnCancelar.setCursor(new Cursor(12));
        this.btnCancelar.setBorderPainted(false);
        this.btnCancelar.setOpaque(true);
        this.btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaInicioSesion.this.regresar();
            }
        });
        panelBotones.add(this.btnIngresar);
        panelBotones.add(this.btnCancelar);
        panelCard.add(panelBotones);
        panelCard.add(Box.createRigidArea(new Dimension(0, 20)));
        this.lblOlvidaste = new JLabel("<html>¿Olvidaste tu código?</html>");
        this.lblOlvidaste.setFont(new Font("Arial", 0, 13));
        this.lblOlvidaste.setForeground(new Color(52, 152, 219));
        this.lblOlvidaste.setCursor(new Cursor(12));
        this.lblOlvidaste.setAlignmentX(0.5F);
        this.lblOlvidaste.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PantallaInicioSesion.this.abrirRecuperarClave();
            }

            public void mouseEntered(MouseEvent e) {
                PantallaInicioSesion.this.lblOlvidaste.setText("<html><u>¿Olvidaste tu código?</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                PantallaInicioSesion.this.lblOlvidaste.setText("<html>¿Olvidaste tu código?</html>");
            }
        });
        panelCard.add(this.lblOlvidaste);
        panelFondo.add(panelCard);
        this.add(panelFondo);
        this.txtContrasena.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaInicioSesion.this.iniciarSesion();
            }
        });
    }

    private void regresar() {
        this.pantallaPadre.setVisible(true);
        this.dispose();
    }

    private void abrirRecuperarClave() {
        PantallaRecuperarClave recuperar = new PantallaRecuperarClave(this);
        recuperar.setVisible(true);
    }

    private void iniciarSesion() {
        if (!this.txtUsuario.getText().trim().isEmpty() && !(new String(this.txtContrasena.getPassword())).trim().isEmpty()) {
            String usuarioIngresado = this.txtUsuario.getText().trim();
            String contrasenaIngresada = new String(this.txtContrasena.getPassword());
            if (usuarioIngresado.equalsIgnoreCase("admin") && contrasenaIngresada.equals("Admin$2026")) {
                Usuario superAdmin = new Usuario("admin", "Administrador Principal", "9999999999", "Admin$2026", "admin@sistema.com", "0000000000", "Administrador", new Fecha(), (Empresa)null, "ACTIVO");
                this.abrirPantallaPrincipal(superAdmin);
            } else {
                try {
                    Usuarios usuarios = new Usuarios("usuarios.dat");
                    List<Usuario> listaUsuarios = usuarios.lista();
                    System.out.println("Usuarios en el sistema: " + listaUsuarios.size());
                    System.out.println("Usuario ingresado: '" + usuarioIngresado + "'");
                    System.out.println("Contraseña ingresada: '" + contrasenaIngresada + "'");
                    boolean encontrado = false;
                    Usuario usuarioEncontrado = null;

                    for(int i = 0; i < listaUsuarios.size(); ++i) {
                        Usuario u = (Usuario)listaUsuarios.get(i);
                        PrintStream var10000 = System.out;
                        String var10001 = u.getIdUsuario();
                        var10000.println("Comparando con usuario: '" + var10001 + "' / contraseña: '" + u.getContrasena() + "'");
                        if (u.getIdUsuario().equalsIgnoreCase(usuarioIngresado) && u.getContrasena().equals(contrasenaIngresada)) {
                            encontrado = true;
                            usuarioEncontrado = u;
                            break;
                        }
                    }

                    if (encontrado) {
                        this.abrirPantallaPrincipal(usuarioEncontrado);
                    } else {
                        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", 0);
                        this.txtContrasena.setText("");
                    }
                } catch (PersistenciaException e) {
                    JOptionPane.showMessageDialog(this, "Error al acceder al sistema: " + e.getMessage(), "Error", 0);
                    e.printStackTrace();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña", "Error", 0);
        }
    }

    private void abrirPantallaPrincipal(Usuario u) {
        this.dispose();
        this.pantallaPadre.dispose();
        PantallaPrincipal pantallaPrincipal = new PantallaPrincipal(u);
        pantallaPrincipal.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InterfacesUsuario.PantallaBienvenida bienvenida = new InterfacesUsuario.PantallaBienvenida();
                PantallaInicioSesion inicioSesion = new PantallaInicioSesion(bienvenida);
                inicioSesion.setVisible(true);
            }
        });
    }
}
