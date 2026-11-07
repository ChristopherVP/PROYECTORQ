package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import excepciones.Validador;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import objetosNegocio.Usuario;

/**
 * Cambio de contraseña OBLIGATORIO en el primer ingreso de una cuenta con contraseña temporal
 * (el administrador principal creado por el sistema y los colaboradores nuevos). Si se cancela, no se entra.
 */
public class DialogoCambioClave extends JDialog {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final Usuario usuario;
    private final JPasswordField txtActual = Tema.clave(22);
    private final JPasswordField txtNueva = Tema.clave(22);
    private final JPasswordField txtConfirmar = Tema.clave(22);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JLabel lblMensaje = Tema.mensaje();
    private final ValidacionUI.Reglas reglas = new ValidacionUI.Reglas();
    private boolean cambiada;

    public DialogoCambioClave(Window padre, Usuario usuario) {
        super(padre, "Cambio de contraseña obligatorio", Dialog.ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        setIconImage(IconoRQ.crear(64));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Tema.encabezado("Elige tu contraseña", "Hola " + usuario.getNombre() + ": la contraseña actual es temporal"), BorderLayout.NORTH);

        JLabel errActual = ValidacionUI.etiquetaError();
        JLabel errNueva = ValidacionUI.etiquetaError();
        JLabel errConfirmar = ValidacionUI.etiquetaError();
        JPanel f = Tema.panelFormulario();
        int fila = 0;
        fila = ValidacionUI.fila(f, fila, "Contraseña actual *", txtActual, errActual);
        fila = ValidacionUI.fila(f, fila, "Nueva contraseña *", txtNueva, errNueva);
        Tema.fila(f, fila++, "", lblFortaleza);
        fila = ValidacionUI.fila(f, fila, "Confirmar nueva *", txtConfirmar, errConfirmar);
        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseñas");
        chkMostrar.setOpaque(false);
        chkMostrar.setFont(Tema.fuente(Font.PLAIN, 12f));
        char eco = txtActual.getEchoChar();
        chkMostrar.addActionListener(e -> {
            char c = chkMostrar.isSelected() ? (char) 0 : eco;
            txtActual.setEchoChar(c);
            txtNueva.setEchoChar(c);
            txtConfirmar.setEchoChar(c);
        });
        Tema.fila(f, fila, "", chkMostrar);
        Tema.vincularFortaleza(txtNueva, lblFortaleza);

        reglas.agregar(txtActual, errActual, () -> new String(txtActual.getPassword()).isEmpty() ? "Escribe tu contraseña actual" : null);
        reglas.agregar(txtNueva, errNueva, () -> {
            String nueva = new String(txtNueva.getPassword());
            String problema = Validador.errorContrasena(nueva);
            if (problema == null && nueva.equals(new String(txtActual.getPassword()))) {
                problema = "La nueva contraseña debe ser distinta de la temporal";
            }
            return problema;
        });
        reglas.agregar(txtConfirmar, errConfirmar, () -> new String(txtNueva.getPassword()).equals(new String(txtConfirmar.getPassword()))
                ? null : "Las contraseñas no coinciden");

        JLabel nota = Tema.nota(440, "<html>Por seguridad debes reemplazar la contraseña temporal antes de usar el sistema. "
                + "Usa mayúscula, minúscula y número (8 a 40 caracteres).</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);

        JButton guardar = Tema.boton("Cambiar contraseña", true);
        guardar.addActionListener(e -> guardar());
        JButton cancelar = Tema.boton("Cancelar", false);
        cancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(guardar);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(guardar);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        centro.add(nota, BorderLayout.NORTH);
        centro.add(f, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        centro.add(sur, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        setPreferredSize(new Dimension(560, 560));
        pack();
        setLocationRelativeTo(padre);
    }

    public boolean fueCambiada() {
        return cambiada;
    }

    private void guardar() {
        if (!reglas.validar()) {
            Tema.mostrarMensaje(lblMensaje, "Revisa los campos marcados en rojo.", false);
            return;
        }
        ResultadoOperacion r = controlador.cambiarContrasena(usuario.getIdUsuario(), new String(txtActual.getPassword()),
                new String(txtNueva.getPassword()), new String(txtConfirmar.getPassword()));
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        cambiada = true;
        Tema.info(this, "Contraseña actualizada. Ya puedes usar el sistema.");
        dispose();
    }
}