package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import objetosNegocio.Usuario;

public class PantallaInicioSesion extends JFrame {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final JTextField txtUsuario = Tema.campo(20);
    private final JPasswordField txtClave = Tema.clave(20);
    private final JLabel lblMensaje = Tema.mensaje();

    public PantallaInicioSesion() {
        setTitle("RQ - Iniciar sesión");
        setIconImage(IconoRQ.crear(64));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearPanelMarca(), BorderLayout.WEST);
        add(crearFormulario(), BorderLayout.CENTER);
        setPreferredSize(new Dimension(860, 520));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel crearPanelMarca() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setPaint(new GradientPaint(0, 0, Tema.AZUL_OSCURO, getWidth(), getHeight(), new Color(0x1B6CA8)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        p.setPreferredSize(new Dimension(340, 520));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.insets = new Insets(0, 0, 14, 0);
        p.add(new JLabel(IconoRQ.icono(150)), g);
        JLabel t = new JLabel("RQ");
        t.setFont(Tema.fuente(Font.BOLD, 40f));
        t.setForeground(Color.WHITE);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 4, 0);
        p.add(t, g);
        JLabel s = new JLabel("<html><div style='text-align:center;width:230px'>Sistema de Transporte Interprovincial<br><br>"
                + "Compra tus pasajes, administra tus viajes y mantén todo el transporte en orden.</div></html>");
        s.setFont(Tema.fuente(Font.PLAIN, 13f));
        s.setForeground(new Color(0xCFE3F3));
        g.gridy = 2;
        p.add(s, g);
        return p;
    }

    private JPanel crearFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(4, 0, 4, 0);

        g.gridy = 0;
        p.add(Tema.titulo("Iniciar sesión", 26f), g);
        g.gridy = 1;
        JLabel sub = Tema.etiqueta("Ingresa con tu usuario y contraseña");
        sub.setForeground(Tema.TEXTO_SUAVE);
        p.add(sub, g);
        g.gridy = 2;
        g.insets = new Insets(18, 0, 2, 0);
        p.add(Tema.etiqueta("Usuario"), g);
        g.gridy = 3;
        g.insets = new Insets(2, 0, 4, 0);
        p.add(txtUsuario, g);
        g.gridy = 4;
        g.insets = new Insets(10, 0, 2, 0);
        p.add(Tema.etiqueta("Contraseña"), g);
        g.gridy = 5;
        g.insets = new Insets(2, 0, 4, 0);
        p.add(txtClave, g);

        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setOpaque(false);
        chkMostrar.setFont(Tema.fuente(Font.PLAIN, 12f));
        char eco = txtClave.getEchoChar();
        chkMostrar.addActionListener(e -> txtClave.setEchoChar(chkMostrar.isSelected() ? (char) 0 : eco));
        g.gridy = 6;
        p.add(chkMostrar, g);

        g.gridy = 7;
        g.insets = new Insets(2, 0, 2, 0);
        p.add(lblMensaje, g);

        javax.swing.JButton btnIngresar = Tema.boton("Ingresar", true);
        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar);
        g.gridy = 8;
        g.insets = new Insets(8, 0, 8, 0);
        p.add(btnIngresar, g);

        g.gridy = 9;
        g.insets = new Insets(4, 0, 2, 0);
        p.add(enlace("¿Olvidaste tu contraseña?", () -> new PantallaRecuperarClave(this).setVisible(true)), g);
        g.gridy = 10;
        p.add(enlace("¿No tienes cuenta? Regístrate como pasajero o conductor", () -> {
            PantallaRegistro registro = new PantallaRegistro(this);
            registro.setVisible(true);
            if (registro.getLoginCreado() != null) {
                txtUsuario.setText(registro.getLoginCreado());
                txtClave.requestFocusInWindow();
            }
        }), g);
        g.gridy = 11;
        g.weighty = 1;
        p.add(new JLabel(), g);
        return p;
    }

    private JLabel enlace(String texto, Runnable accion) {
        JLabel l = new JLabel("<html><u>" + texto + "</u></html>");
        l.setFont(Tema.fuente(Font.PLAIN, 12.5f));
        l.setForeground(Tema.CELESTE);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
        return l;
    }

    private void ingresar() {
        ResultadoOperacion r = controlador.autenticar(txtUsuario.getText(), new String(txtClave.getPassword()));
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            txtClave.setText("");
            txtClave.requestFocusInWindow();
            return;
        }
        Navegador.abrirSesion((Usuario) r.getDatos(), this);
    }
}
