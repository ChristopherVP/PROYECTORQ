package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import objetosNegocio.Usuario;

/** Perfil de la cuenta: datos de contacto (correo opcional), contraseña y pregunta de seguridad. */
public class DialogoPerfil extends JDialog {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final Usuario usuario;
    private final Runnable alActualizar;

    private final JTextField txtNombre = Tema.campo(22);
    private final JTextField txtEmail = Tema.campo(22);
    private final JTextField txtTelefono = Tema.campo(22);
    private final JLabel msjDatos = Tema.mensaje();
    private final JPasswordField txtActual = Tema.clave(22);
    private final JPasswordField txtNueva = Tema.clave(22);
    private final JPasswordField txtConfirmar = Tema.clave(22);
    private final JLabel fortaleza = Tema.mensaje();
    private final JLabel msjClave = Tema.mensaje();
    private final JPasswordField txtClaveSeg = Tema.clave(22);
    private final JComboBox<String> cmbPregunta = new JComboBox<>(Tema.PREGUNTAS);
    private final JTextField txtRespuesta = Tema.campo(22);
    private final JLabel msjSeg = Tema.mensaje();

    public DialogoPerfil(Window padre, Usuario usuario, Runnable alActualizar) {
        super(padre, "Mi perfil", Dialog.ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.alActualizar = alActualizar;
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado(usuario.getNombre(), usuario.getTipo() + "  ·  usuario: " + usuario.getLogin()), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Tema.fuente(Font.BOLD, 13f));
        tabs.addTab("Mis datos", pestanaDatos());
        tabs.addTab("Contraseña", pestanaClave());
        tabs.addTab("Seguridad", pestanaSeguridad());
        add(tabs, BorderLayout.CENTER);
        setPreferredSize(new Dimension(640, 520));
        pack();
        setLocationRelativeTo(padre);
    }

    private JPanel contenedor(JPanel formulario, JLabel mensaje, javax.swing.JButton boton) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));
        p.add(formulario, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(mensaje, BorderLayout.NORTH);
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER));
        b.setOpaque(false);
        b.add(boton);
        sur.add(b, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JPanel pestanaDatos() {
        txtNombre.setText(usuario.getNombre());
        txtNombre.setEnabled(usuario.getRol() != objetosNegocio.Rol.CONDUCTOR);
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        JTextField cedula = Tema.campo(22);
        cedula.setText(usuario.getCedula());
        cedula.setEnabled(false);
        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Nombre", txtNombre);
        Tema.fila(f, 1, "Cédula", cedula);
        Tema.fila(f, 2, "Correo (opcional)", txtEmail);
        Tema.fila(f, 3, "Teléfono (opcional)", txtTelefono);
        javax.swing.JButton guardar = Tema.boton("Guardar cambios", true);
        guardar.addActionListener(e -> {
            ResultadoOperacion r = controlador.actualizarPerfil(usuario.getIdUsuario(), txtNombre.getText(), txtEmail.getText(), txtTelefono.getText());
            Tema.mostrarMensaje(msjDatos, r.getMensaje(), r.isExito());
            if (r.isExito()) {
                Navegador.refrescar(usuario, (Usuario) r.getDatos());
                alActualizar.run();
            }
        });
        return contenedor(f, msjDatos, guardar);
    }

    private JPanel pestanaClave() {
        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Contraseña actual", txtActual);
        Tema.fila(f, 1, "Contraseña nueva", txtNueva);
        Tema.fila(f, 2, "", fortaleza);
        Tema.fila(f, 3, "Confirmar nueva", txtConfirmar);
        Tema.vincularFortaleza(txtNueva, fortaleza);
        javax.swing.JButton cambiar = Tema.boton("Cambiar contraseña", true);
        cambiar.addActionListener(e -> {
            ResultadoOperacion r = controlador.cambiarContrasena(usuario.getIdUsuario(), new String(txtActual.getPassword()),
                    new String(txtNueva.getPassword()), new String(txtConfirmar.getPassword()));
            Tema.mostrarMensaje(msjClave, r.getMensaje(), r.isExito());
            if (r.isExito()) {
                Navegador.refrescar(usuario, (Usuario) r.getDatos());
                txtActual.setText("");
                txtNueva.setText("");
                txtConfirmar.setText("");
            }
        });
        return contenedor(f, msjClave, cambiar);
    }

    private JPanel pestanaSeguridad() {
        cmbPregunta.setEditable(true);
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));
        if (usuario.tienePreguntaSeguridad()) {
            cmbPregunta.setSelectedItem(usuario.getPreguntaSeguridad());
        }
        JLabel nota = Tema.nota(470, "<html>La pregunta de seguridad te permite recuperar tu contraseña usando solo tu cédula. "
                + (usuario.tienePreguntaSeguridad() ? "Ya tienes una configurada; puedes cambiarla." : "<b>Aún no tienes una configurada.</b>") + "</html>");
        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Pregunta", cmbPregunta);
        Tema.fila(f, 1, "Respuesta", txtRespuesta);
        Tema.fila(f, 2, "Tu contraseña actual", txtClaveSeg);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(nota, BorderLayout.NORTH);
        centro.add(f, BorderLayout.CENTER);
        javax.swing.JButton guardar = Tema.boton("Guardar pregunta", true);
        guardar.addActionListener(e -> {
            ResultadoOperacion r = controlador.cambiarPreguntaSeguridad(usuario.getIdUsuario(), new String(txtClaveSeg.getPassword()),
                    String.valueOf(cmbPregunta.getEditor().getItem()), txtRespuesta.getText());
            Tema.mostrarMensaje(msjSeg, r.getMensaje(), r.isExito());
            if (r.isExito()) {
                Navegador.refrescar(usuario, (Usuario) r.getDatos());
                txtRespuesta.setText("");
                txtClaveSeg.setText("");
            }
        });
        return contenedor(centro, msjSeg, guardar);
    }
}
