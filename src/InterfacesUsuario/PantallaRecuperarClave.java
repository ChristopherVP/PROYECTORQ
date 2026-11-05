package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Recuperación de contraseña sin escribir el usuario: se identifica con la cédula y se verifica con la pregunta de seguridad. */
public class PantallaRecuperarClave extends JDialog {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final CardLayout pasos = new CardLayout();
    private final JPanel contenedor = new JPanel(pasos);
    private final JTextField txtCedula = Tema.campo(20);
    private final JLabel lblPregunta = Tema.titulo(" ", 15f);
    private final JTextField txtRespuesta = Tema.campo(20);
    private final JPasswordField txtNueva = Tema.clave(20);
    private final JPasswordField txtConfirmar = Tema.clave(20);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JLabel lblMensaje1 = Tema.mensaje();
    private final JLabel lblMensaje2 = Tema.mensaje();
    private String cedulaVerificada;

    public PantallaRecuperarClave(Window padre) {
        super(padre, "Recuperar contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado("Recuperar contraseña", "No necesitas recordar tu usuario"), BorderLayout.NORTH);
        contenedor.setBackground(Tema.FONDO);
        contenedor.add(crearPaso1(), "paso1");
        contenedor.add(crearPaso2(), "paso2");
        add(contenedor, BorderLayout.CENTER);
        setPreferredSize(new Dimension(520, 470));
        pack();
        setLocationRelativeTo(padre);
    }

    private JPanel crearPaso1() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        p.add(Tema.nota(440, "<html><b>Paso 1 de 2.</b> Escribe la cédula con la que creaste tu cuenta. "
                + "Te mostraremos tu pregunta de seguridad.</html>"), BorderLayout.NORTH);
        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Cédula", txtCedula);
        p.add(f, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setOpaque(false);
        sur.add(lblMensaje1, BorderLayout.NORTH);
        javax.swing.JButton continuar = Tema.boton("Continuar", true);
        continuar.addActionListener(e -> verificarCedula());
        getRootPane().setDefaultButton(continuar);
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER));
        b.setOpaque(false);
        b.add(continuar);
        sur.add(b, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPaso2() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));
        JPanel arriba = new JPanel(new BorderLayout(0, 6));
        arriba.setOpaque(false);
        arriba.add(Tema.nota(440, "<html><b>Paso 2 de 2.</b> Responde tu pregunta y elige una contraseña nueva.</html>"), BorderLayout.NORTH);
        arriba.add(lblPregunta, BorderLayout.SOUTH);
        p.add(arriba, BorderLayout.NORTH);
        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Respuesta", txtRespuesta);
        Tema.fila(f, 1, "Contraseña nueva", txtNueva);
        Tema.fila(f, 2, "", lblFortaleza);
        Tema.fila(f, 3, "Confirmar", txtConfirmar);
        Tema.vincularFortaleza(txtNueva, lblFortaleza);
        p.add(f, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setOpaque(false);
        sur.add(lblMensaje2, BorderLayout.NORTH);
        javax.swing.JButton atras = Tema.boton("Atrás", false);
        atras.addActionListener(e -> pasos.show(contenedor, "paso1"));
        javax.swing.JButton restablecer = Tema.boton("Restablecer contraseña", true);
        restablecer.addActionListener(e -> restablecer());
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        b.setOpaque(false);
        b.add(atras);
        b.add(restablecer);
        sur.add(b, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private void verificarCedula() {
        ResultadoOperacion r = controlador.obtenerPreguntaRecuperacion(txtCedula.getText());
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje1, r.getMensaje(), false);
            return;
        }
        cedulaVerificada = txtCedula.getText().trim();
        lblPregunta.setText("<html>" + String.valueOf(r.getDatos()).replace("<", "&lt;") + "</html>");
        lblMensaje2.setText(" ");
        pasos.show(contenedor, "paso2");
        txtRespuesta.requestFocusInWindow();
    }

    private void restablecer() {
        ResultadoOperacion r = controlador.restablecerContrasena(cedulaVerificada, txtRespuesta.getText(),
                new String(txtNueva.getPassword()), new String(txtConfirmar.getPassword()));
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje2, r.getMensaje(), false);
            return;
        }
        Tema.info(this, r.getMensaje());
        dispose();
    }
}
